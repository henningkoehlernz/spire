package evolution;

import basemod.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.helpers.RelicType;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class Evolution implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostCreateStartingDeckSubscriber,
        PostInitializeSubscriber {

    private static final Logger logger = LogManager.getLogger(Evolution.class.getName());

    // mod config variables
    public static final String MODNAME = "evolution";
    public static final String IMG_PATH = MODNAME + "/img/";
    // files in preferences directory are synced automatically
    private static final String EVOLUTION_PATH = "preferences/evolution";
    private static final String CONFIG_PATH = "preferences/evolution.cfg";
    private static final String CONFIG_VARIETY = "variety";
    private static TreeMap<String, int[]> evolution = new TreeMap<String, int[]>();
    private static TreeMap<String, String> config = new TreeMap<String, String>();

    private static int getMaxAscensionLevel(AbstractPlayer p) {
        Prefs pref = p.getPrefs();
        return pref == null ? 0 : pref.getInteger("ASCENSION_LEVEL", 1);
    }

    private static void saveEvolution() {
        String sEvolution = (new Gson()).toJson(evolution);
        Gdx.files.local(EVOLUTION_PATH).writeString(sEvolution, false, String.valueOf(StandardCharsets.UTF_8));
        logger.info("saved evolution=" + sEvolution);
    }

    private static void saveConfig() {
        String sConfig = (new Gson()).toJson(config);
        Gdx.files.local(CONFIG_PATH).writeString(sConfig, false, String.valueOf(StandardCharsets.UTF_8));
        logger.info("saved config=" + sConfig);
    }

    private static void loadEvolution() {
        String sEvolution = "{}";
        if (Gdx.files.local(EVOLUTION_PATH).exists()) {
            sEvolution = Gdx.files.local(EVOLUTION_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
            logger.info("loaded evolution=" + sEvolution);
        }
        // parsing maps requires Type object to get around type erasure
        Type mapType = new TypeToken<TreeMap<String, int[]>>(){}.getType();
        evolution = (new Gson()).fromJson(sEvolution, mapType);
    }

    private static void loadConfig() {
        String sConfig = "{}";
        if (Gdx.files.local(CONFIG_PATH).exists()) {
            sConfig = Gdx.files.local(CONFIG_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
            logger.info("loaded config=" + sConfig);
        }
        // parsing maps requires Type object to get around type erasure
        Type mapType = new TypeToken<TreeMap<String, String>>(){}.getType();
        config = (new Gson()).fromJson(sConfig, mapType);
    }

    public Evolution() {
        BaseMod.subscribe(this);
    }

    public static void addEvolution(AbstractPlayer.PlayerClass pc, int ascension, int amount) {
        int[] pEvolution = evolution.getOrDefault(pc.name(), new int[0]);
        if ( ascension >= pEvolution.length ) {
            pEvolution = Arrays.copyOf(pEvolution, ascension + 1);
            evolution.put(pc.name(), pEvolution);
        }
        pEvolution[Math.max(ascension, 0)] += amount;
        saveEvolution();
    }

    public static int getEvolutionTotal(AbstractPlayer.PlayerClass pc, int ascension) {
        int[] pEvolution = evolution.getOrDefault(pc.name(), new int[0]);
        int total = 0;
        for ( int a = Math.max(ascension, 0); a < pEvolution.length; a++ )
            total += pEvolution[a];
        return total;
    }

    public static int getVariety() {
        return Integer.valueOf(config.getOrDefault(CONFIG_VARIETY, "1"));
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
        loadEvolution();
        loadConfig();
        // setup config panel
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        ModPanel configPanel = new ModPanel();
        // configure replacement card variety
        ModLabel varietyLabel = new ModLabel("Replacement Variety",
                400.0f, 700.0f, configPanel, (label) -> {});
        ModMinMaxSlider varietySlider = new ModMinMaxSlider(
                "", 450.0f, 650.0f, 1, 3, getVariety(), "%.0f",
                configPanel, (slider) -> {
                    int newVariety = Math.round(slider.getValue());
                    if ( newVariety != getVariety() ) {
                        config.put(CONFIG_VARIETY, String.valueOf(newVariety));
                        saveConfig();
                    }
                }
        );
        configPanel.addUIElement(varietyLabel);
        configPanel.addUIElement(varietySlider);
        BaseMod.registerModBadge(badgeTexture, "Evolution", "Henning Koehler",
                "Defeat bosses for persistent improvements.", configPanel);
    }

    @Override
    public void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass pc, CardGroup cg) {
        AbstractPlayer p = AbstractDungeon.player;
        int ep = Evolution.getEvolutionTotal(p.chosenClass, AbstractDungeon.ascensionLevel);
        AbstractRelic relic = new Axolotl();
        relic.counter = ep;
        relic.instantObtain();
        int leftover = ep - CardReplacer.replaceBasicCards(ep);
        if ( leftover > 0 )
            p.increaseMaxHp(leftover, true);
        logger.info("added evolution relic");
    }

}
