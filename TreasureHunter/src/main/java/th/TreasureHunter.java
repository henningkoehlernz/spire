package th;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.PowerStrings;

import basemod.BaseMod;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.nio.charset.StandardCharsets;

@SpireInitializer
public class TreasureHunter implements EditCardsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber {

    private static final Logger logger = LogManager.getLogger(TreasureHunter.class.getName());

    // mod config variables
    private static final String MODNAME = "TreasureHunter";
    private static final String CONFIG_TREASURE = "treasure";
    private static Properties defaultConfig = new Properties();
    private static int treasure = 0;

    public TreasureHunter() {
        BaseMod.subscribe(this);
        defaultConfig.setProperty(CONFIG_TREASURE, "0");
        try {
            SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
            config.load();
            treasure = config.getInt(CONFIG_TREASURE);
            logger.info("loaded treasure=" + treasure);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addTreasure(int amount) {
        treasure += amount;
        try {
            SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
            config.setInt(CONFIG_TREASURE, treasure);
            config.save();
            logger.info("saved treasure=" + treasure);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        new th.TreasureHunter();
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new CopperCoins());
    }

    private static String languagePath() {
        if ( Settings.language == Settings.GameLanguage.ZHS )
            return "zhs";
        return "eng";
    }

    @Override
    public void receiveEditStrings() {
        String basePath = "loc/" + languagePath() + "/";
        BaseMod.loadCustomStringsFile(CardStrings.class, basePath + "TH-CardStrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, basePath + "TH-PowerStrings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class, basePath + "TH-CharacterStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        // read keywords from json
        String keywordPath = "loc/" + languagePath() + "/TH-Keywords.json";
        String jsonString = Gdx.files.internal(keywordPath).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = (Keyword[])(new Gson()).fromJson(jsonString, Keyword[].class);
        // register with the game
        for ( Keyword k : keywords )
            BaseMod.addKeyword(k.NAMES, k.DESCRIPTION);
    }

}
