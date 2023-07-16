package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

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

    @Override
    public void setCounter(int counter) {
        if (this.counter != counter) {
            this.counter = counter;
            updateDescription();
        }
    }

    public void updateBloodied() {
        AbstractPlayer p = AbstractDungeon.player;
        int current = p.currentHealth + getCurrentAgility();
        int max = p.maxHealth + getMaxAgility();
        if (current <= max / 2) {
            if (!p.isBloodied) {
                p.isBloodied = true;
                for (AbstractRelic r : p.relics)
                    r.onBloodied();
            }
        } else {
            if (p.isBloodied) {
                p.isBloodied = false;
                for (AbstractRelic r : p.relics)
                    r.onNotBloodied();
            }
        }
    }

    public void increaseMaxAgility(int amount) {
        int maxIncrease = COUNTER_MOD - 1 - getMaxAgility();
        setCounter(counter + Math.min(amount, maxIncrease));
    }

    public void decreaseMaxAgility(int amount) {
        int newMax = Math.max(0, getMaxAgility() - amount);
        int newCurrent = Math.min(newMax, getCurrentAgility());
        setCounter(newCurrent * COUNTER_MOD + newMax);
    }

    public void increaseCurrentAgility(int amount, boolean checkBloodied) {
        int missing = getMaxAgility() - getCurrentAgility();
        amount = Math.min(amount, missing);
        setCounter(counter + amount * COUNTER_MOD);
        if (checkBloodied)
            updateBloodied();
    }

    public void decreaseCurrentAgility(int amount, boolean checkBloodied) {
        int current = getCurrentAgility();
        amount = Math.min(amount, current);
        setCounter(counter - amount * COUNTER_MOD);
        if (checkBloodied)
            updateBloodied();
    }

    private void convertHealth() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.maxHealth > 1) {
            increaseMaxAgility(p.maxHealth - 1);
            increaseCurrentAgility(p.currentHealth - 1, false);
            p.decreaseMaxHealth(p.maxHealth - 1);
        }
    }

    private void revertHealth() {
        AbstractPlayer p = AbstractDungeon.player;
        p.maxHealth += getMaxAgility();
        p.currentHealth += getCurrentAgility();
        p.healthBarUpdatedEvent();
        setCounter(0);
    }

    public float getDodgeChance() {
        int agility = getCurrentAgility();
        return agility / (50.0f + agility);
    }

    public boolean isActive() {
        return counter > 0;
    }

    @Override
    public int onPlayerHeal(int healAmount) {
        increaseCurrentAgility(healAmount, true);
        return healAmount;
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BufferPower(AbstractDungeon.player, 1), 1));
        convertHealth();
    }

    @Override
    public void onVictory() {
        revertHealth();
    }

    @Override
    public boolean canSpawn() { return false; }

    @Override
    public String getUpdatedDescription() {
        int percentChance = Math.round(getDodgeChance() * 100.0f);
        return DESCRIPTIONS[0] + percentChance + DESCRIPTIONS[1];
    }

    private void updateDescription() {
        description = getUpdatedDescription();
        tips.clear();
        tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }
}
