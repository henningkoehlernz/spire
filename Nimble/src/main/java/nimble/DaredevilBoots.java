package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class DaredevilBoots extends CustomRelic {

    public static final String ID = "nimble:DaredevilBoots";
    private static final String IMG_PATH = Nimble.IMG_PATH + "daredevil_boots.png";

    public DaredevilBoots() {
        super(ID, new Texture(IMG_PATH), RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}