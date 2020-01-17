package icr;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.stances.AbstractStance;

public class YinYangPower extends AbstractPower {
    public static final String POWER_ID = "ICR:YinYang";
    public static String[] DESCRIPTIONS = null;
    private static final Texture tex128 = new Texture(IroncladRager.IMG_PATH + "powers/yin_yang_128.png");
    private static final Texture tex48 = new Texture(IroncladRager.IMG_PATH + "powers/yin_yang_48.png");

    public YinYangPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.amount = amount;
        this.owner = owner;
        this.type = AbstractPower.PowerType.BUFF;
        if ( DESCRIPTIONS == null )
            DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
        // new graphics
        this.region128 = new TextureAtlas.AtlasRegion(tex128, 0, 0, 128, 128);
        this.region48 = new TextureAtlas.AtlasRegion(tex48, 0, 0, 48, 48);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    @Override
    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if ( oldStance.ID.equals(newStance.ID) )
            return;
        if ( newStance.ID.equals("Calm") ) {
            flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new DexterityPower(this.owner, this.amount), this.amount));
        } else if ( newStance.ID.equals("Wrath") ) {
            flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
        }
    }
}
