package icr;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;

public class DragonStrike extends CustomCard {
    public static final String ID = "ICR:DragonStrike";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = IroncladRager.IMG_PATH + "dragon_strike.png";
    private static final int COST = 1;
    private static final int ATTACK_DMG = 6;
    private static final int WRATH_DMG = 2;

    public DragonStrike() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.ATTACK, AbstractCard.CardColor.PURPLE,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);
        this.damage = this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = WRATH_DMG;
        tags.add(CardTags.STRIKE);
        tags.add(BaseModCardTags.BASIC_STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
            this.upgradeMagicNumber(1);
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        String stance = AbstractDungeon.player.stance.ID;
        if ( stance.equals("Wrath") || stance.equals("Divinity") ) {
            int realBaseDamage = this.baseDamage;
            this.baseDamage += this.magicNumber;
            super.calculateCardDamage(mo);
            this.baseDamage = realBaseDamage;
            this.isDamageModified = (this.damage != this.baseDamage);
        } else
            super.calculateCardDamage(mo);
    }

    @Override
    public void applyPowers() {
        String stance = AbstractDungeon.player.stance.ID;
        if ( stance.equals("Wrath") || stance.equals("Divinity") ) {
            int realBaseDamage = this.baseDamage;
            this.baseDamage += this.magicNumber;
            super.applyPowers();
            this.baseDamage = realBaseDamage;
            this.isDamageModified = (this.damage != this.baseDamage);
        } else
            super.applyPowers();
    }

}
