package nimble;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

import java.util.ArrayList;

public class NimblePatcher {

    // add ShrinkRayGun to starting relics
    @SpirePatch(
            clz = CharSelectInfo.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, int.class, int.class, int.class, int.class, int.class, AbstractPlayer.class, ArrayList.class, ArrayList.class, boolean.class}
    )
    public static class CharSelectInfoConstructor {
        public static void Postfix(CharSelectInfo __instance, String name, String flavorText, int currentHp, int maxHp, int maxOrbs, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, boolean resumeGame) {
            __instance.relics.add(ShrinkRayGun.ID);
        }
    }

    // make player look smaller
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "loadAnimation",
            paramtypez = {String.class, String.class, float.class}
    )
    public static class LoadAnimation {
        public static void Prefix(AbstractCreature p, String atlasUrl, String skeletonUrl, @ByRef float[] scale) {
            if (p instanceof AbstractPlayer && ((AbstractPlayer)p).hasRelic(ShrinkRayGun.ID))
                scale[0] += 0.5f;
        }
    }

    // trigger powers/relics/cards that normally trigger on HP loss (mimics AbstractPlayer.damage)
    public static int handleAgilityLoss(AbstractPlayer p, DamageInfo info, int damageAmount) {
        for (AbstractRelic r : p.relics)
            damageAmount = r.onLoseHpLast(damageAmount);
        if (damageAmount > 0) {
            for (AbstractPower pow : p.powers)
                damageAmount = pow.onLoseHp(damageAmount);
            for (AbstractRelic r : p.relics)
                r.onLoseHp(damageAmount);
            for (AbstractPower pow : p.powers)
                pow.wasHPLost(info, damageAmount);
            for (AbstractRelic r : p.relics)
                r.wasHPLost(damageAmount);
            Reflection.invoke(p, AbstractPlayer.class, "updateCardsOnDamage");
            ++p.damagedThisCombat;
        }
        return damageAmount;
    }

    // damage avoidance mechanic
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage",
            paramtypez = {DamageInfo.class}
    )
    public static class AbstractPlayerDamage {
        public static SpireReturn<Void> Prefix(AbstractPlayer p, DamageInfo info) {
            ShrinkRayGun r = (ShrinkRayGun)p.getRelic(ShrinkRayGun.ID);
            if (r == null || !r.isActive() || info.output <= 0)
                return SpireReturn.Continue();
            // HP loss applies to agility instead
            if (info.type == DamageInfo.DamageType.HP_LOSS) {
                int agilityLoss = handleAgilityLoss(p, info, info.output);
                r.decreaseCurrentAgility(agilityLoss, true);
                AbstractDungeon.effectList.add(new StrikeEffect(p, p.hb.cX, p.hb.cY, agilityLoss));
                p.lastDamageTaken = 0;
                return SpireReturn.Return();
            }
            // unblocked damage can be avoided
            boolean freeDodge = p.hasRelic(SnakeskinBelt.ID);
            if (freeDodge || info.output > p.currentBlock) {
                float chance = r.getDodgeChance();
                // energy drink may reduce dodge chance
                EnergyDrink drink = (EnergyDrink)p.getRelic(EnergyDrink.ID);
                if (drink != null && EnergyPanel.getCurrentEnergy() == 0) {
                    chance = Math.max(0, chance - 0.25f);
                    drink.flash();
                }
                // daredevil boots provide minimum dodge chance
                DaredevilBoots boots = (DaredevilBoots)p.getRelic(DaredevilBoots.ID);
                if (chance < 0.5f && boots != null) {
                    chance = 0.5f;
                    boots.flash();
                }
                // synthacardium increases dodge chance against weak attacks
                Synthacardium heart = (Synthacardium)p.getRelic(Synthacardium.ID);
                if (heart != null && info.output <= 2) {
                    chance = Math.min(1.0f, chance + 0.25f);
                    heart.flash();
                }
                // loaded dice increases dodge chance after failed check
                AbstractRelic dice = p.getRelic(LoadedDice.ID);
                if (dice != null && !dice.grayscale) {
                    chance = 1.0f;
                    dice.flash();
                    dice.usedUp();
                }
                boolean success = AbstractDungeon.miscRng.randomBoolean(chance);
                // daredevil boots can provide re-roll of dodge chance
                if (!success && boots != null && !p.hasPower(BufferPower.POWER_ID)) {
                    AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, boots));
                    success = AbstractDungeon.miscRng.randomBoolean(chance);
                }
                if (success) {
                    for (AbstractPower pow : p.powers)
                        pow.onAttacked(info, 0);
                    for (AbstractRelic rel : p.relics)
                        rel.onAttacked(info, 0);
                    if (!freeDodge)
                        r.decreaseCurrentAgility(1, true);
                    r.flash();
                    AbstractDungeon.effectList.add(new BlockedWordEffect(p, p.hb.cX, p.hb.cY, r.DESCRIPTIONS[2]));
                    p.useStaggerAnimation();
                    p.lastDamageTaken = 0;
                    return SpireReturn.Return();
                } else if (dice != null && !dice.usedUp) {
                    dice.grayscale = false;
                }
            }
            return SpireReturn.Continue();
        }
    }

    // prevent healing from triggering invalid bloodied updates
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "heal",
            paramtypez = {int.class}
    )
    public static class PlayerHeal {
        public static SpireReturn<Void> Prefix(AbstractPlayer p, int healAmount) {
            ShrinkRayGun gun = (ShrinkRayGun)p.getRelic(ShrinkRayGun.ID);
            if (gun == null || !gun.isActive())
                return SpireReturn.Continue();
            // trigger relics and powers
            for (AbstractRelic r : p.relics)
                r.onPlayerHeal(healAmount);
            for (AbstractPower pow : p.powers)
                pow.onHeal(healAmount);
            // visual feedback
            AbstractDungeon.topPanel.panelHealEffect();
            AbstractDungeon.effectsQueue.add(new HealEffect(p.hb.cX - p.animX, p.hb.cY, healAmount));
            return SpireReturn.Return();
        }
    }

    // max HP reductions during combat (e.g. Battle Towers) reduce max agility instead
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "decreaseMaxHealth",
            paramtypez = {int.class}
    )
    public static class DecreaseMaxHealth {
        public static void Prefix(AbstractCreature p, @ByRef int[] amount) {
            if (p instanceof AbstractPlayer) {
                ShrinkRayGun r = (ShrinkRayGun)((AbstractPlayer)p).getRelic(ShrinkRayGun.ID);
                if (r != null && r.isActive() && p.maxHealth <= amount[0]) {
                    int hpDecrease = p.maxHealth - 1;
                    r.decreaseMaxAgility(amount[0] - hpDecrease);
                    amount[0] = hpDecrease;
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "renderHealth",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderHealth {
        private static final Color agilityBarColor = new Color(0.6F, 0.3F, 1.0F, 0.75F);
        private static final Color agilityTextColor = new Color(1.0F, 1.0F, 1.0F, 0.75F);
        public static void Prefix(AbstractCreature p, SpriteBatch sb) {
            if (!(p instanceof AbstractPlayer))
                return;
            ShrinkRayGun r = (ShrinkRayGun)((AbstractPlayer)p).getRelic(ShrinkRayGun.ID);
            if (r != null && !Settings.hideCombatElements) {
                float hbYOffset = (Float)Reflection.get(p, AbstractCreature.class, "hbYOffset");
                float HEALTH_BAR_HEIGHT = (Float)Reflection.get(null, AbstractCreature.class, "HEALTH_BAR_HEIGHT");
                float HEALTH_BAR_OFFSET_Y = (Float)Reflection.get(null, AbstractCreature.class, "HEALTH_BAR_OFFSET_Y");
                float HEALTH_TEXT_OFFSET_Y = (Float)Reflection.get(null, AbstractCreature.class, "HEALTH_TEXT_OFFSET_Y");
                // calculate location and width for agility bar
                float x = p.hb.cX - p.hb.width / 2.0F;
                float y = p.hb.cY - p.hb.height / 2.0F + hbYOffset + HEALTH_BAR_HEIGHT * 1.1F;
                float agilityBarWidth = p.hb.width * r.getCurrentAgility() / r.getMaxAgility();
                // render agility bar
                Reflection.invoke(p, AbstractCreature.class, "renderHealthBg", sb, x, y);
                if (r.getCurrentAgility() > 0) {
                    sb.setColor(agilityBarColor);
                    sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                    sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, agilityBarWidth, HEALTH_BAR_HEIGHT);
                    sb.draw(ImageMaster.HEALTH_BAR_R, x + agilityBarWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                }
                // render agility text
                FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, r.getCurrentAgility() + "/" + r.getMaxAgility(),
                        p.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y + 5.0F * Settings.scale, agilityTextColor);
            }
        }
    }

}
