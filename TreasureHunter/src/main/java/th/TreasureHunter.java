package th;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.UIStrings;

import basemod.BaseMod;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

@SpireInitializer
public class TreasureHunter implements EditCardsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber {

    private static final Logger logger = LogManager.getLogger(TreasureHunter.class.getName());

    // mod config variables
    private static final String MODNAME = "TreasureHunter";
    private static final String CONFIG_TREASURE = "treasure";
    private static Properties defaultConfig = new Properties();
    private static int[] treasure = {};
    // treasure cards
    public static ArrayList<AbstractCard> treasures = new ArrayList<AbstractCard>();

    public TreasureHunter() {
        BaseMod.subscribe(this);
        defaultConfig.setProperty(CONFIG_TREASURE, "[]");
        try {
            SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
            config.load();
            String sTreasure = config.getString(CONFIG_TREASURE);
            treasure = (new Gson()).fromJson(sTreasure, int[].class);
            logger.info("loaded treasure=" + sTreasure);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addTreasure(int ascension, int amount) {
        if ( ascension >= treasure.length )
            treasure = Arrays.copyOf(treasure, ascension + 1);
        treasure[ascension] += amount;
        String sTreasure = (new Gson()).toJson(treasure);
        try {
            SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
            config.setString(CONFIG_TREASURE, sTreasure);
            config.save();
            logger.info("saved treasure=" + sTreasure);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTreasureTotal(int ascension) {
        int total = 0;
        for ( int a = ascension; a < treasure.length; a++ )
            total += treasure[a];
        return total;
    }

    public static void initialize() {
        new th.TreasureHunter();
    }

    @Override
    public void receiveEditCards() {
        treasures.add(new CopperCoins());
        treasures.add(new BikiniMail());
        treasures.add(new CursedSword());
        for ( AbstractCard card : treasures )
            BaseMod.addCard(card);
    }

    static AbstractCard getRandomTreasure() {
        return treasures.get(AbstractDungeon.cardRng.random(treasures.size() - 1));
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
        BaseMod.loadCustomStringsFile(UIStrings.class, basePath + "TH-UIStrings.json");
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
