package icr;

import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class AutoFrostPower extends AbstractPower {
    public static final String POWER_ID = "ICR:AutoFrost";
    public static String[] DESCRIPTIONS = null;

    public AutoFrostPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.amount = amount;
        this.owner = owner;
        this.type = AbstractPower.PowerType.BUFF;
        if ( DESCRIPTIONS == null )
            DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
        // re-use existing graphics
        loadRegion("storm");
        this.priority = 3;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        flash();
        for ( int i = 0; i < this.amount; i++ )
            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost()));
    }

}
