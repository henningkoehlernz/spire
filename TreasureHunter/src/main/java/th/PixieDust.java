package th;

import com.megacrit.cardcrawl.actions.common.TransformCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
        int handSize = p.hand.size() - 1; // don't count pixie dust itself
        if (handSize <= 0)
            return;
        if (upgraded && handSize > 1) {
            AbstractDungeon.actionManager.addToBottom(new TransformAndUpgradeAction());
        } else {
            int cardIndex = AbstractDungeon.miscRng.random(handSize - 1);
            //LogManager.getLogger(PixieDust.class.getName()).info("hand size=" + handSize + ", index=" + cardIndex);
            AbstractCard newCard = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
            newCard.upgrade();
            AbstractDungeon.actionManager.addToBottom(new TransformCardInHandAction(cardIndex, newCard));
        }
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
