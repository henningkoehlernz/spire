package th;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeMap;

@SpireInitializer
public class TreasureHunter implements
        EditCardsSubscriber,
        EditKeywordsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(TreasureHunter.class.getName());

    // mod config variables
    public static final String MODNAME = "TreasureHunter";
    public static final String IMG_PATH = MODNAME + "/img/";
    // files in preferences directory are synced automatically
    private static final String TREASURE_PATH = "preferences/treasure.json";
    // old config
    private static final String CONFIG_TREASURE = "treasure";
    private static TreeMap<String, int[]> treasure = new TreeMap<String, int[]>();
    // treasure cards
    public static ArrayList<AbstractCard> treasures = new ArrayList<AbstractCard>();

    private static int getMaxAscensionLevel(AbstractPlayer p) {
        Prefs pref = p.getPrefs();
        return pref == null ? 0 : pref.getInteger("ASCENSION_LEVEL", 1);
    }

    private static void saveConfig() {
        String sTreasure = (new Gson()).toJson(treasure);
        Gdx.files.local(TREASURE_PATH).writeString(sTreasure, false, String.valueOf(StandardCharsets.UTF_8));
        logger.info("saved treasure=" + sTreasure);
    }

    private static void loadConfig() {
        String sTreasure = null;
        if (Gdx.files.local(TREASURE_PATH).exists()) {
            sTreasure = Gdx.files.local(TREASURE_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
        } else try {
            // legacy config
            SpireConfig config = new SpireConfig(MODNAME, "config");
            config.load();
            sTreasure = config.getString("treasure");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sTreasure != null) {
            logger.info("loaded treasure=" + sTreasure);
            // parsing maps requires Type object to get around type erasure
            Type mapType = new TypeToken<TreeMap<String, int[]>>(){}.getType();
            treasure = (new Gson()).fromJson(sTreasure, mapType);
        }
    }

    public TreasureHunter() {
        BaseMod.subscribe(this);
    }

    public static void addTreasure(AbstractPlayer.PlayerClass pc, int ascension, int amount) {
        int[] pTreasure = treasure.getOrDefault(pc.name(), new int[0]);
        if ( ascension >= pTreasure.length ) {
            pTreasure = Arrays.copyOf(pTreasure, ascension + 1);
            treasure.put(pc.name(), pTreasure);
        }
        pTreasure[Math.max(ascension, 0)] += amount;
        logger.info("added " + amount + " gold for ascension " + ascension);
        saveConfig();
    }

    public static int getTreasureTotal(AbstractPlayer.PlayerClass pc, int ascension) {
        int[] pTreasure = treasure.getOrDefault(pc.name(), new int[0]);
        int total = 0;
        for (int a = 0; a <= Math.max(ascension, pTreasure.length); a++) {
            if (a < pTreasure.length)
                total += pTreasure[a];
            if (a < ascension)
                total /= 2;
        }
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
        treasures.add(new EnergyPotion());
        treasures.add(new TreasureMap());
        treasures.add(new DeckOfManyThings());
        treasures.add(new ArmorSpikes());
        treasures.add(new Mimic());
        treasures.add(new PixieDust());
        treasures.add(new ElvenBoots());
        treasures.add(new HealingPotion());
        for ( AbstractCard card : treasures )
            BaseMod.addCard(card);
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Strongbox(), RelicType.SHARED);
        //BaseMod.addRelic(new HealthInsurance(), RelicType.SHARED);
        BaseMod.addRelic(new PirateNurse(), RelicType.SHARED);
        BaseMod.addRelic(new Anvil(), RelicType.SHARED);
        BaseMod.addRelic(new Rum(), RelicType.SHARED);
        BaseMod.addRelic(new Parrot(), RelicType.SHARED);
        BaseMod.addRelic(new GoldenGun(), RelicType.SHARED);
        BaseMod.addRelic(new BlackFlag(), RelicType.SHARED);
        BaseMod.addRelic(new CursedGold(), RelicType.SHARED);
    }

    public static AbstractCard getRandomTreasure() {
        return treasures.get(AbstractDungeon.cardRng.random(treasures.size() - 1));
    }

    private static String languagePath() {
        if ( Settings.language == Settings.GameLanguage.ZHS )
            return MODNAME + "/loc/zhs/";
        return MODNAME + "/loc/eng/";
    }

    @Override
    public void receiveEditStrings() {
        String basePath = languagePath();
        BaseMod.loadCustomStringsFile(CardStrings.class, basePath + "TH-CardStrings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, basePath + "TH-RelicStrings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, basePath + "TH-UIStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        // read keywords from json
        String keywordPath = languagePath() + "TH-Keywords.json";
        String jsonString = Gdx.files.internal(keywordPath).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = (Keyword[])(new Gson()).fromJson(jsonString, Keyword[].class);
        // register with the game
        for ( Keyword k : keywords )
            BaseMod.addKeyword(k.NAMES, k.DESCRIPTION);
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        BaseMod.registerModBadge(badgeTexture, "Treasure Hunter", "Henning Koehler",
                "Enables treasure mechanic for persistent improvements.", null);
        loadConfig();
    }
}
