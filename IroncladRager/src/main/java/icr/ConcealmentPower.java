package icr;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ConcealmentPower extends AbstractPower {
    public static final String POWER_ID = "ICR:Concealment";
    public static String[] DESCRIPTIONS = null;

    public ConcealmentPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.amount = amount;
        this.owner = owner;
        this.type = AbstractPower.PowerType.BUFF;
        if ( DESCRIPTIONS == null )
            DESCRIPTIONS = (CardCrawlGame.languagePack.getPowerStrings(this.ID)).DESCRIPTIONS;
        this.name = (CardCrawlGame.languagePack.getPowerStrings(this.ID)).NAME;
        updateDescription();
        // re-use graphics for intangible power
        loadRegion("intangible");
        this.priority = 99;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType damageType) {
        if ( damage > 1.0f ) {
            damage -= this.amount;
            if ( damage < 1.0f )
                return 1.0f;
        }
        return damage;
    }

    @Override
    public void atEndOfRound() {
        flash();
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

}

