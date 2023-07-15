package nimble;

import basemod.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.RelicStrings;

import basemod.helpers.RelicType;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@SpireInitializer
public class Nimble implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostCreateStartingRelicsSubscriber,
        PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(Nimble.class.getName());

    // mod config variables
    public static final String MODNAME = "nimble";
    public static final String IMG_PATH = MODNAME + "/img/";

    public Nimble() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new Nimble();
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new ShrinkRayGun(), RelicType.SHARED);
    }

    private static String languagePath() {
        return MODNAME + "/loc/eng/";
    }

    @Override
    public void receiveEditStrings() {
        String basePath = languagePath();
        BaseMod.loadCustomStringsFile(RelicStrings.class, basePath + "Nimble-RelicStrings.json");
    }

    @Override
    public void receivePostCreateStartingRelics(AbstractPlayer.PlayerClass pc, ArrayList<String> relics) {
        relics.add(ShrinkRayGun.ID);
        logger.info("added nimble relic");
    }

    @Override
    public void receivePostInitialize() {
    }
}
