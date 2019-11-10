package th;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static th.TreasurePatch.TREASURE;

public class DeckOfManyThings extends AbstractTreasure {
    public static final String ID = "TH:DeckOfManyThings";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = TreasureHunter.IMG_PATH + "deck.png";
    private static final int COST = 1;

    public DeckOfManyThings() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card;
        if ( AbstractDungeon.cardRng.random(0,1) == 0 )
            card = TreasureHunter.getRandomTreasure();
        else
            card = AbstractDungeon.rareCardPool.getRandomCard(true);
        if ( upgraded ) {
            card = card.makeStatEquivalentCopy();
            card.upgrade();
        }
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card));
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
