package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Strongbox extends CustomRelic {

    public static final String ID = "TH:Strongbox";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "strongbox.png";

    public Strongbox() {
        super(ID, new Texture(IMG_PATH), RelicTier.RARE, LandingSound.CLINK);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if ( c.type == TreasurePatch.TREASURE ) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
