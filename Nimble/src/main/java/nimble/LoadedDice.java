package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class LoadedDice extends CustomRelic {

    public static final String ID = "nimble:LoadedDice";
    private static final String IMG_PATH = Nimble.IMG_PATH + "loaded_dice.png";

    public LoadedDice() {
        super(ID, new Texture(IMG_PATH), RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        grayscale = true;
    }

}
