package icr;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;

public class Serenity extends CustomCard {
    public static final String ID = "ICR:Serenity";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // Get object containing the strings that are displayed in the game.
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = IroncladRager.IMG_PATH + "serenity.png";
    private static final int COST = 1;

    public Serenity() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.SKILL, AbstractCard.CardColor.PURPLE,
                AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if ( p.stance.ID.equals("Calm") )
            addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));
        else
            addToBot(new ChangeStanceAction("Calm"));
        addToBot(new PressEndTurnButtonAction());
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if ( canUse && !p.stance.ID.equals("Calm") ) {
            this.cantUseMessage = CardCrawlGame.languagePack.getUIString(ID).TEXT[0];
            return false;
        }
        return canUse;
    }

    @Override
    public void triggerOnGlowCheck() {
        if ( AbstractDungeon.player.stance.ID.equals("Calm") ) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
        if ( !this.upgraded ) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

}
