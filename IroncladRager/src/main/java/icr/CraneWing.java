package icr;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;

public class CraneWing extends CustomCard {
    public static final String ID = "ICR:CraneWing";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = IroncladRager.IMG_PATH + "crane_wing.png";
    private static final int COST = 1;
    private static final int BLOCK = 5;
    private static final int CALM_BONUS = 3;

    public CraneWing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.SKILL, AbstractCard.CardColor.PURPLE,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);
        this.block = this.baseBlock = BLOCK;
        this.magicNumber = this.baseMagicNumber = CALM_BONUS;
        tags.add(BaseModCardTags.BASIC_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeMagicNumber(1);
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        if ( AbstractDungeon.player.stance.ID.equals("Calm") ) {
            int realBaseBlock = this.baseBlock;
            this.baseBlock += this.magicNumber;
            super.calculateCardDamage(mo);
            this.baseBlock = realBaseBlock;
            this.isBlockModified = (this.block != this.baseBlock);
        }
    }

    @Override
    public void applyPowers() {
        if ( AbstractDungeon.player.stance.ID.equals("Calm") ) {
            int realBaseBlock = this.baseBlock;
            this.baseBlock += this.magicNumber;
            super.applyPowers();
            this.baseBlock = realBaseBlock;
            this.isBlockModified = (this.block != this.baseBlock);
        } else
            super.applyPowers();
    }

}
