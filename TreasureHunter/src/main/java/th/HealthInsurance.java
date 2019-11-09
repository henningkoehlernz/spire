package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HealthInsurance extends CustomRelic {

    public static final String ID = "TH:HealthInsurance";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "health_insurance.png";

    public HealthInsurance() {
        super(ID, new Texture(IMG_PATH), RelicTier.SHOP, LandingSound.FLAT);
    }

    @Override
    public void atTurnStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if ( p.currentHealth > 0 && p.currentHealth <= p.maxHealth / 2) {
            int healing = Math.min(p.gold, TreasurePatch.getTreasureCount());
            if ( healing > 0 ) {
                flash();
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
