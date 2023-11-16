package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class BufferingCap extends CustomRelic {

    public static final String ID = "nimble:BufferingCap";
    private static final String IMG_PATH = Nimble.IMG_PATH + "buffering_cap.png";

    public BufferingCap() {
        super(ID, new Texture(IMG_PATH), RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
