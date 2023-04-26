package bossed;

import basemod.*;
import basemod.interfaces.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.TreeMap;

@SpireInitializer
public class BossedRelics implements EditStringsSubscriber, PostInitializeSubscriber {

    // mod config variables
    public static final String MODNAME = "BossedRelics";
    public static final String MOD_PREFIX = "Bossed:";
    private static final String CONFIG_PATH = "preferences/bossed.cfg";
    private static final String CONFIG_DISABLED = "disabled";
    private static TreeMap<String, HashSet<String>> config = new TreeMap<>();
    private static HashSet<String> disabled = new HashSet<>();
    private static final Logger logger = LogManager.getLogger(BossedRelics.class.getName());

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

    public static UIStrings getUIString(String uiName) {
        return CardCrawlGame.languagePack.getUIString(MOD_PREFIX + uiName);
    }

    private static void saveConfig() {
        String sConfig = (new Gson()).toJson(config);
        Gdx.files.local(CONFIG_PATH).writeString(sConfig, false, String.valueOf(StandardCharsets.UTF_8));
        logger.info("saved config=" + sConfig);
    }

    private static void loadConfig() {
        if (Gdx.files.local(CONFIG_PATH).exists()) {
            String sConfig = Gdx.files.local(CONFIG_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
            logger.info("loaded config=" + sConfig);
            // parsing maps requires Type object to get around type erasure
            Type mapType = new TypeToken<TreeMap<String, HashSet<String>>>(){}.getType();
            config = (new Gson()).fromJson(sConfig, mapType);
            disabled = config.get(CONFIG_DISABLED);
        } else {
            config.put(CONFIG_DISABLED, disabled);
        }
    }

    public static boolean isDisabled(String relicID) {
        return disabled.contains(relicID);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(RelicStrings.class, languagePath() + "BossedRelicStrings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, languagePath() + "BossedUiStrings.json");
    }

    private static float xPos(int index) { return 410f + 400f * (index / 10); }
    private static float yPos(int index) { return 660f - 50f * (index % 10); }
    @Override
    public void receivePostInitialize() {
        loadConfig();
        // setup config panel
        Texture badgeTexture = new Texture(MODNAME + "/badge.png");
        ModPanel configPanel = new ModPanel();
        // disable change for specific artefacts
        String labelText = getUIString("DisableChanges").TEXT[0];
        ModLabel disabledLabel = new ModLabel(labelText, 400f, 730f, configPanel, (label) -> {});
        configPanel.addUIElement(disabledLabel);
        final String[] relicChoices = {
                Astrolabe.ID, CallingBell.ID, Ectoplasm.ID, EmptyCage.ID, FrozenCore.ID, HolyWater.ID, PandorasBox.ID, RingOfTheSerpent.ID,
                RunicCube.ID, SacredBark.ID, TinyHouse.ID, WristBlade.ID,
                BottledFlame.ID, BottledLightning.ID, BottledTornado.ID, Cauldron.ID, DollysMirror.ID, LizardTail.ID, Mango.ID,
                MawBank.ID, OldCoin.ID, Omamori.ID, Orrery.ID, Pear.ID, Strawberry.ID, Waffle.ID, WarPaint.ID, Whetstone.ID, WingBoots.ID
            };
        for (int index = 0; index < relicChoices.length; index++) {
            String relicID = relicChoices[index];
            ModLabeledToggleButton disableButton = new ModLabeledToggleButton(
                CardCrawlGame.languagePack.getRelicStrings(relicID).NAME, xPos(index), yPos(index),
                Settings.CREAM_COLOR, FontHelper.charDescFont, isDisabled(relicID),
                configPanel, (label) -> {}, (button) -> {
                    if (button.enabled)
                        disabled.add(relicID);
                    else
                        disabled.remove(relicID);
                    saveConfig();
                }
            );
            configPanel.addUIElement(disableButton);
        }
        BaseMod.registerModBadge(badgeTexture, "Bossed Relics", "Henning Koehler", getUIString("ModDesc").TEXT[0], configPanel);
    }

}
