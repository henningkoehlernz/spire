package evo;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.RelicStrings;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.*;

@SpireInitializer
public class Evolution implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostCreateStartingDeckSubscriber,
        PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(Evolution.class.getName());

    // mod config variables
    public static final String MODNAME = "Evolution";
    public static final String IMG_PATH = MODNAME + "/img/";
    private static final String CONFIG_EP = "evolution";
    private static Properties defaultConfig = new Properties();
    private static TreeMap<String, int[]> evolution = new TreeMap<String, int[]>();

    private static int getMaxAscensionLevel(AbstractPlayer p) {
        Prefs pref = p.getPrefs();
        return pref == null ? 0 : pref.getInteger("ASCENSION_LEVEL", 1);
    }

    private static void saveConfig() {
        String sEvolution = (new Gson()).toJson(evolution);
        try {
            SpireConfig config = new SpireConfig(MODNAME, "evolution", defaultConfig);
            config.setString(CONFIG_EP, sEvolution);
            config.save();
            logger.info("saved evolution=" + sEvolution);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() {
        try {
            SpireConfig config = new SpireConfig(MODNAME, "evolution", defaultConfig);
            config.load();
            String sEvolution = config.getString(CONFIG_EP);
            logger.info("loaded evolution=" + sEvolution);
            if ( sEvolution.charAt(0) == '[' ) {
                int[] legacyEvolution =  (new Gson()).fromJson(sEvolution, int[].class);
                // divide evenly between qualifying characters
                int[] charactersByLevel = new int[legacyEvolution.length];
                ArrayList<AbstractPlayer> players = CardCrawlGame.characterManager.getAllCharacters();
                for ( AbstractPlayer p : players ) {
                    int maxAscensionLevel = Math.min(getMaxAscensionLevel(p), legacyEvolution.length - 1);
                    for ( int level = 0; level <= maxAscensionLevel; level++ )
                        charactersByLevel[level]++;
                }
                for ( AbstractPlayer p : players ) {
                    int maxAscensionLevel = Math.min(getMaxAscensionLevel(p), legacyEvolution.length - 1);
                    int[] pEvolution = new int[maxAscensionLevel + 1];
                    for ( int level = 0; level <= maxAscensionLevel; level++ )
                        pEvolution[level] = legacyEvolution[level] / charactersByLevel[level];
                    evolution.put(p.chosenClass.name(), pEvolution);
                }
                saveConfig();
            } else {
                // parsing maps requires Type object to get around type erasure
                Type mapType = new TypeToken<TreeMap<String, int[]>>(){}.getType();
                evolution = (new Gson()).fromJson(sEvolution, mapType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Evolution() {
        BaseMod.subscribe(this);
        defaultConfig.setProperty(CONFIG_EP, "{}");
    }

    public static void addEvolution(AbstractPlayer.PlayerClass pc, int ascension, int amount) {
        int[] pEvolution = evolution.getOrDefault(pc.name(), new int[0]);
        if ( ascension >= pEvolution.length ) {
            pEvolution = Arrays.copyOf(pEvolution, ascension + 1);
            evolution.put(pc.name(), pEvolution);
        }
        pEvolution[Math.max(ascension, 0)] += amount;
        saveConfig();
    }

    public static int getEvolutionTotal(AbstractPlayer.PlayerClass pc, int ascension) {
        int[] pEvolution = evolution.getOrDefault(pc.name(), new int[0]);
        int total = 0;
        for ( int a = Math.max(ascension, 0); a < pEvolution.length; a++ )
            total += pEvolution[a];
        return total;
    }

    public static void initialize() {
        new Evolution();
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Axolotl(), RelicType.SHARED);
    }

    private static String languagePath() {
        if ( Settings.language == Settings.GameLanguage.ZHS )
            return MODNAME + "/loc/zhs/";
        return MODNAME + "/loc/eng/";
    }

    @Override
    public void receiveEditStrings() {
        String basePath = languagePath();
        BaseMod.loadCustomStringsFile(RelicStrings.class, basePath + "EVO-RelicStrings.json");
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        BaseMod.registerModBadge(badgeTexture, "Evolution", "Henning Koehler",
                "Defeat bosses for persistent improvements.", null);
        loadConfig();
    }

    @Override
    public void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass pc, CardGroup cg) {
        AbstractPlayer p = AbstractDungeon.player;
        int ep = Evolution.getEvolutionTotal(p.chosenClass, AbstractDungeon.ascensionLevel);
        AbstractRelic relic = new Axolotl();
        relic.counter = ep;
        relic.instantObtain();
        CardReplacer.replaceBasicCards(ep);
        logger.info("added evolution relic");
    }

}
