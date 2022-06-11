package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Rum extends CustomRelic {

    public static final String ID = "TH:Rum";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "rum.png";

    public Rum() {
        super(ID, new Texture(IMG_PATH), RelicTier.COMMON, LandingSound.SOLID);
    }

    @Override
    public void atBattleStart() {
        grayscale = false;
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (!grayscale && drawnCard.type == TreasurePatch.TREASURE) {
            flash();
            grayscale = true;
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 1), 1));
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
