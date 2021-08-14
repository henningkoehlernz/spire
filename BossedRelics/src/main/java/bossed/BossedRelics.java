package bossed;

import basemod.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class BossedRelics implements EditStringsSubscriber, PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(bossed.BossedRelics.class.getName());

    // mod config variables
    public static final String MODNAME = "BossedRelics";

    public BossedRelics() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new bossed.BossedRelics();
    }

    private static String languagePath() {
        if ( Settings.language == Settings.GameLanguage.ZHS )
            return MODNAME + "/loc/zhs/";
        else if ( Settings.language == Settings.GameLanguage.FRA )
            return MODNAME + "/loc/fra/";
        return MODNAME + "/loc/eng/";
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(RelicStrings.class, languagePath() + "BossedRelicStrings.json");
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        BaseMod.registerModBadge(badgeTexture, "Bossed Relics", "Henning Koehler",
                "Improves or reworks some of the weaker boss relics, as well as non-boss relics with one time effects.", null);
    }
}
