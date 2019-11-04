package th;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static th.TreasurePatch.TREASURE;

public class TreasureMap extends AbstractTreasure {
    public static final String ID = "TH:TreasureMap";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = TreasureHunter.IMG_PATH + "map.png";
    private static final int COST = 2;

    public TreasureMap() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card = AbstractDungeon.rareCardPool.getRandomCard(true);
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card));
    }

}
