package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class ShrinkRayGun extends CustomRelic {

    public static final String ID = "nimble:ShrinkRayGun";
    private static final String IMG_PATH = Nimble.IMG_PATH + "shrink_ray_gun.png";

    public ShrinkRayGun() {
        super(ID, new Texture(IMG_PATH), RelicTier.STARTER, LandingSound.MAGICAL);
        counter = 0;
    }

    // max and current agility are both encoded in counter
    private static final int COUNTER_MOD = 1000;

    public int getMaxAgility() {
        return counter % COUNTER_MOD;
    }

    public int getCurrentAgility() {
        return counter / COUNTER_MOD;
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        String counterString =  getCurrentAgility() + "/" + getMaxAgility();
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, counterString, this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
    }

    private void increaseMaxAgility(int amount) {
        counter += amount * (COUNTER_MOD + 1);
        updateDescription();
    }

    private void increaseCurrentAgility(int amount) {
        int missing = getMaxAgility() - getCurrentAgility();
        amount = Math.min(amount, missing);
        counter += amount * COUNTER_MOD;
        updateDescription();
    }

    private void decreaseCurrentAgility(int amount) {
        int current = getCurrentAgility();
        amount = Math.min(amount, current);
        counter -= amount * COUNTER_MOD;
        updateDescription();
    }

    private void updateHealth() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.maxHealth > 1) {
            int transfer = p.maxHealth - 1;
            increaseMaxAgility(transfer);
            p.decreaseMaxHealth(transfer);
        }
    }

    public double getDodgeChance() {
        double agility = getCurrentAgility();
        return (20.0 + agility) / (100.0 + agility);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        updateHealth();
    }

    // decrementBlock is called in AbstractPlayer.damage
    // we reduce damage here to ensure blocked damage is reduced as well
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "decrementBlock",
            paramtypez = {DamageInfo.class, int.class}
    )
    public static class DecrementBlock {
        public static void Prefix(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount) {
            if (__instance instanceof AbstractPlayer && damageAmount[0] > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                AbstractPlayer p = (AbstractPlayer)__instance;
                ShrinkRayGun r = (ShrinkRayGun)p.getRelic(ShrinkRayGun.ID);
                if (r != null) {
                    if (AbstractDungeon.miscRng.randomBoolean((float)r.getDodgeChance())) {
                        damageAmount[0] = 0;
                        r.flash();
                    }
                    r.decreaseCurrentAgility(1);
                }
            }
        }
    }

    @Override
    public int onPlayerHeal(int healAmount) {
        increaseCurrentAgility(healAmount);
        return healAmount;
    }

    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BufferPower(AbstractDungeon.player, 1), 1));
    }

    @Override
    public boolean canSpawn() { return false; }

    @Override
    public String getUpdatedDescription() {
        int percentChance = (int)(getDodgeChance() * 100.0);
        return DESCRIPTIONS[0] + percentChance + DESCRIPTIONS[1];
    }

    private void updateDescription() {
        description = getUpdatedDescription();
        tips.clear();
        tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }
}
