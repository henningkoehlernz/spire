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

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;

public class Dodge extends CustomCard {
    public static final String ID = "ICR:Dodge";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = "img/dodge.png";
    private static final int COST = 1;
    private static final int BLOCK = 4;
    private static final int CONCEAL = 1;

    public Dodge() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.SKILL, AbstractCard.CardColor.GREEN,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);
        this.block = this.baseBlock = BLOCK;
        this.magicNumber = this.baseMagicNumber = CONCEAL;
        tags.add(BaseModCardTags.BASIC_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(
                p, p, new ConcealmentPower(p, this.magicNumber), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(2);
            this.upgradeMagicNumber(1);
        }
    }

    /*
    private int countExtraEnemies() {
        int activeEnemies = 0;
        for ( AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters ) {
            if ( !monster.isDying && monster.currentHealth > 0 && !monster.isEscaping )
                activeEnemies++;
        }
        return activeEnemies > 1 ? activeEnemies - 1 : 0;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseBlock = this.baseBlock;
        this.baseBlock += this.magicNumber * countExtraEnemies();
        super.calculateCardDamage(mo);
        this.baseBlock = realBaseBlock;
        this.isBlockModified = (this.block != this.baseBlock);
    }

    @Override
    public void applyPowers() {
        int realBaseBlock = this.baseBlock;
        this.baseBlock += this.magicNumber * countExtraEnemies();
        super.applyPowers();
        this.baseBlock = realBaseBlock;
        this.isBlockModified = (this.block != this.baseBlock);
    }
    */
}
