package th;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PixieDust extends AbstractTreasure {
    public static final String ID = "TH:PixieDust";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = TreasureHunter.IMG_PATH + "dust.png";
    private static final int COST = 1;

    public PixieDust() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if ( upgraded )
            AbstractDungeon.actionManager.addToBottom(new ExhaustAction(p, p, 1, false));
        else
            AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, 1, false));
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
    }

    @Override
    public void upgrade() {
        if ( !this.upgraded ) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

}
