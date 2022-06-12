package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PirateNurse extends CustomRelic {

    public static final String ID = "TH:PirateNurse";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "pirate_nurse.png";

    public PirateNurse() {
        super(ID, new Texture(IMG_PATH), RelicTier.SHOP, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if ( p.currentHealth > 0 && p.currentHealth <= p.maxHealth / 2) {
            int healing = Math.min(p.gold, TreasurePatch.getTreasureCount());
            if ( healing > 0 ) {
                flash();
                setCounter(counter < 0 ? healing : counter + healing);
                AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                p.heal(healing);
                p.loseGold(healing);
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
