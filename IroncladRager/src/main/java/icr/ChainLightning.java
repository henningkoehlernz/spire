package icr;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;

public class ChainLightning extends CustomCard {
    public static final String ID = "ICR:ChainLightning";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "img/chain_lightning.png";
    private static final int COST = 1;

    public ChainLightning() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.POWER, AbstractCard.CardColor.BLUE,
                AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = 1;
        if ( this.cost == -1 ) {
            effect = EnergyPanel.totalCount;
            if ( !this.freeToPlayOnce ) {
                p.energy.use(EnergyPanel.totalCount);
            }
            if ( p.hasRelic("Chemical X") ) {
                effect += 2;
                p.getRelic("Chemical X").flash();
            }
        }
        if ( effect > 0 )
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new AutoLightningPower(p, effect)));
    }

    @Override
    public void upgrade() {
        if ( !this.upgraded ) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
            this.cost = -1; // cost X
        }
    }

}
