package icr;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GainEnergyIfBloodiedAction extends AbstractGameAction {
    private int energyGain;

    public GainEnergyIfBloodiedAction(int amount) {
        setValues(AbstractDungeon.player, AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
        this.energyGain = amount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p.isBloodied) {
                p.gainEnergy(this.energyGain);
                AbstractDungeon.actionManager.updateEnergyGain(this.energyGain);
                for (AbstractCard c : p.hand.group) {
                    c.triggerOnGainEnergy(this.energyGain, true);
                }
            }
        }
        tickDuration();
    }
}