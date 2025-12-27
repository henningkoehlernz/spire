package th;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SacredBark;

public class HealingPotion extends AbstractTreasure {
    public static final String ID = "TH:HealingPotion";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = TreasureHunter.IMG_PATH + "healing.png";
    private static final int COST = 1;

    public HealingPotion() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardTarget.NONE);
        this.baseMagicNumber = this.magicNumber = 1;
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();
        String potionKey = CardCrawlGame.languagePack.getUIString("TH:Potion").TEXT[0].toLowerCase();
        if (!keywords.contains(potionKey))
            keywords.add(potionKey);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int healing = p.hasRelic(SacredBark.ID) ? 2 * magicNumber : magicNumber;
        p.heal(healing);
        // trigger relics like Toy Ornithopter
        for (AbstractRelic r : p.relics)
            r.onUsePotion();
    }

    @Override
    public void upgrade() {
        if ( !this.upgraded ) {
            this.upgradeName();
            this.upgradeMagicNumber(2);
        }
    }

}
