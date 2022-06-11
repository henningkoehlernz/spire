package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Parrot extends CustomRelic {

    public static final String ID = "TH:Parrot";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "parrot.png";

    public Parrot() {
        super(ID, new Texture(IMG_PATH), RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (drawnCard.type == TreasurePatch.TREASURE) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
