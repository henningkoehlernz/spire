package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class EnergyDrink extends CustomRelic {

    public static final String ID = "nimble:EnergyDrink";
    private static final String IMG_PATH = Nimble.IMG_PATH + "energy_drink.png";

    public EnergyDrink() {
        super(ID, new Texture(IMG_PATH), RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }

}