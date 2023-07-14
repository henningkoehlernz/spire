package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class ShrinkRayGun extends CustomRelic {

    public static final String ID = "nimble:ShrinkRayGun";
    private static final String IMG_PATH = Nimble.IMG_PATH + "shrink_ray_gun.png";

    public ShrinkRayGun() {
        super(ID, new Texture(IMG_PATH), RelicTier.STARTER, LandingSound.MAGICAL);
    }

    // max and current agility are both encoded in counter
    private static final int COUNTER_MOD = 1000;

    public int getMaxAgility() {
        return counter < 0 ? 0 : counter % COUNTER_MOD;
    }

    public int getCurrentAgility() {
        return counter < 0 ? 0 : counter / COUNTER_MOD;
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        String counterString =  getCurrentAgility() + "/" + getMaxAgility();
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, counterString, this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
    }

    private void increaseMaxAgility(int amount) {
        if (counter < 0)
            counter = 0;
        counter += amount * (COUNTER_MOD + 1);
    }

    private void increaseCurrentAgility(int amount) {
        int missing = getMaxAgility() - getCurrentAgility();
        amount = Math.min(amount, missing);
        counter += amount * COUNTER_MOD;
    }

    private void decreaseCurrentAgility(int amount) {
        int current = getCurrentAgility();
        amount = Math.min(amount, current);
        counter -= amount * COUNTER_MOD;
    }

    private void updateHealth() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.maxHealth > 1) {
            int transfer = p.maxHealth - 1;
            increaseMaxAgility(transfer);
            p.decreaseMaxHealth(transfer);
        }
    }

    @Override
    public void onEquip() {
        updateHealth();
    }
    @Override
    public void onEnterRoom(AbstractRoom room) {
        updateHealth();
    }

    @Override
    public boolean canSpawn() { return false; }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
