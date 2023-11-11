package nimble;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class SnakeskinBelt extends CustomRelic {

    public static final String ID = "nimble:SnakeskinBelt";
    private static final String IMG_PATH = Nimble.IMG_PATH + "snakeskin_belt.png";

    public SnakeskinBelt() {
        super(ID, new Texture(IMG_PATH), RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
