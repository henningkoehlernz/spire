package nimble;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
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
            // attack damage can be avoided
            if (info.type == DamageInfo.DamageType.NORMAL && info.owner != p && info.owner != null) {
                if (AbstractDungeon.miscRng.randomBoolean(r.getDodgeChance())) {
                    r.decreaseCurrentAgility(1, true);
                    r.flash();
                    AbstractDungeon.effectList.add(new BlockedWordEffect(p, p.hb.cX, p.hb.cY, r.DESCRIPTIONS[2]));
                    p.useStaggerAnimation();
                    p.lastDamageTaken = 0;
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }
            // HP loss applies to agility instead
            if (info.type == DamageInfo.DamageType.HP_LOSS) {
                int agilityLoss = handleAgilityLoss(p, info, info.output);
                r.decreaseCurrentAgility(agilityLoss, true);
                AbstractDungeon.effectList.add(new StrikeEffect(p, p.hb.cX, p.hb.cY, agilityLoss));
                p.lastDamageTaken = 0;
                return SpireReturn.Return();
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
}
