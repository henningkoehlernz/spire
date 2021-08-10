package th;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;

import static th.TreasurePatch.TREASURE;

public class EnergyPotion extends AbstractTreasure {
    public static final String ID = "TH:EnergyPotion";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = TreasureHunter.IMG_PATH + "potion.png";
    private static final int COST = 0;

    public EnergyPotion() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardTarget.NONE);
        this.baseMagicNumber = this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PoisonPower(p, p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if ( !this.upgraded ) {
            this.upgradeName();
            this.upgradeMagicNumber(-2);
        }
    }

}
