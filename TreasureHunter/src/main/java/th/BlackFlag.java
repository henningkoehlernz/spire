package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class BlackFlag extends CustomRelic {

    public static final String ID = "TH:BlackFlag";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "black_flag.png";

    public BlackFlag() {
        super(ID, new Texture(IMG_PATH), RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
