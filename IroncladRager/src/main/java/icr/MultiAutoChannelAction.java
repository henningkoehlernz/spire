package icr;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class MultiAutoChannelAction extends AbstractGameAction {

    public enum OrbType { FROST, LIGHTNING };

    private OrbType orb;
    private int energyOnUse;
    private boolean freeToPlayOnce;

    public MultiAutoChannelAction(OrbType orb, int energyOnUse, boolean freeToPlayOnce) {
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = AbstractGameAction.ActionType.SPECIAL;
        this.orb = orb;
        this.energyOnUse = energyOnUse;
        this.freeToPlayOnce = freeToPlayOnce;
    }

    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        if ( p.hasRelic("Chemical X") ) {
            energyOnUse += 2;
            p.getRelic("Chemical X").flash();
        }
        if ( energyOnUse > 0 ) {
            AbstractPower pow = this.orb == OrbType.FROST ?
                    new AutoFrostPower(p, energyOnUse) :
                    new AutoLightningPower(p, energyOnUse);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, pow));
            if ( !freeToPlayOnce ) {
                p.energy.use(EnergyPanel.totalCount);
            }
        }
        isDone = true;
    }
}
