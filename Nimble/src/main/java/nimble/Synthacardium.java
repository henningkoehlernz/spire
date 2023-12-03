package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class Synthacardium extends CustomRelic {

    public static final String ID = "nimble:Synthacardium";
    private static final String IMG_PATH = Nimble.IMG_PATH + "synthacardium.png";

    public Synthacardium() {
        super(ID, new Texture(IMG_PATH), RelicTier.SHOP, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
