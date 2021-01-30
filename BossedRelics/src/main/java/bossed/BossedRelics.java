package bossed;

import basemod.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class BossedRelics implements EditStringsSubscriber {

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
        return MODNAME + "/loc/eng/";
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(RelicStrings.class, languagePath() + "BossedRelicStrings.json");
    }

}
