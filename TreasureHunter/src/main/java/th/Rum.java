package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Rum extends CustomRelic {

    public static final String ID = "TH:Rum";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "rum.png";

    public Rum() {
        super(ID, new Texture(IMG_PATH), RelicTier.COMMON, LandingSound.SOLID);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.type == TreasurePatch.TREASURE) {
            this.flash();
            AbstractCreature p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(p, this));
            p.heal(1);
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(p, p, WeakPower.POWER_ID,1));
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(p, p, FrailPower.POWER_ID,1));
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(p, p, VulnerablePower.POWER_ID,1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
