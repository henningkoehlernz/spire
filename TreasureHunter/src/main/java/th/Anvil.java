package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Anvil extends CustomRelic {

    public static final String ID = "TH:Anvil";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "anvil.png";

    public Anvil() {
        super(ID, new Texture(IMG_PATH), RelicTier.UNCOMMON, LandingSound.HEAVY);
    }

    public void onObtainCard(AbstractCard c) {
        if ( c.type == TreasurePatch.TREASURE && c.canUpgrade() ) {
            c.upgrade();
        }
    }

    @Override
    public boolean canSpawn() {
        return (Settings.isEndless || AbstractDungeon.floorNum <= 40);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
