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

    @Override
    public void setCounter(int counter) {
        if (this.counter != counter) {
            this.counter = counter;
            updateDescription();
        }
    }

    public void increaseMaxAgility(int amount) {
        setCounter(counter + amount * (COUNTER_MOD + 1));
    }

    public void increaseCurrentAgility(int amount) {
        int missing = getMaxAgility() - getCurrentAgility();
        amount = Math.min(amount, missing);
        setCounter(counter + amount * COUNTER_MOD);
    }

    public void decreaseCurrentAgility(int amount) {
        int current = getCurrentAgility();
        amount = Math.min(amount, current);
        setCounter(counter - amount * COUNTER_MOD);
    }

    public float getDodgeChance() {
        int agility = getCurrentAgility();
        return agility / (50.0f + agility);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.maxHealth > 1) {
            int transfer = p.maxHealth - 1;
            increaseMaxAgility(transfer);
            p.decreaseMaxHealth(transfer);
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
