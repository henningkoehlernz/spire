import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;

import basemod.abstracts.CustomCard;
import basemod.helpers.CardTags;
import basemod.helpers.BaseModCardTags;

public class RageStrike extends CustomCard {
    public static final String ID = "IroncladRager:RageStrike";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = "img/strike.png";
    private static final int COST = 1;
    private static final int ATTACK_DMG = 4;

    public RageStrike() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.ATTACK, AbstractCard.CardColor.RED,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);
        this.damage = this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = 1;
        //this.setBackgroundTexture("img/512/bk_attack.png", "img/1024/bk_attack.png");
        //this.setOrbTexture("img/512/orb_small.png", "img/512/orb_large.png");
        //this.setBannerTexture("img/custom_banner_large.png", "img/custom_banner_large.png");
        tags.add(CardTags.STRIKE);
        tags.add(BaseModCardTags.BASIC_STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        if ( !this.upgraded )
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
        }
    }
}
