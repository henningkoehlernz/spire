package bossed;

import basemod.*;
import basemod.interfaces.*;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

@SpireInitializer
public class BossedRelics implements EditStringsSubscriber, PostInitializeSubscriber {

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
        BaseMod.loadCustomStringsFile(UIStrings.class, languagePath() + "BossedUiStrings.json");
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        BaseMod.registerModBadge(badgeTexture, "Bossed Relics", "Henning Koehler",
                "Improves or reworks some of the weaker boss relics, as well as non-boss relics with one time effects.", null);
    }
}
