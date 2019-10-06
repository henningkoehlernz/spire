package icr;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
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
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;

import java.util.Properties;
import java.nio.charset.StandardCharsets;

@SpireInitializer
public class IroncladRager implements EditCardsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber, PostInitializeSubscriber {

    // mod config variables
    private static final String MODNAME = "ICR";
    private static final String CONFIG_GUARANTEE_SUBCLASS = "guaranteeSubclass";
    private static Properties defaultConfig = new Properties();
    public static boolean guaranteeSubclass = false;

    public IroncladRager() {
        BaseMod.subscribe(this);
        defaultConfig.setProperty(CONFIG_GUARANTEE_SUBCLASS, "FALSE");
        try {
            SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
            config.load();
            guaranteeSubclass = config.getBool(CONFIG_GUARANTEE_SUBCLASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        new IroncladRager();
    }

    private static void addRedCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.IRONCLAD_RAGER.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.IRONCLAD_BERSERKER.name(), card.cardID);
    }

    private static void addGreenCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.SILENT_POISONER.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.SILENT_ASSASSIN.name(), card.cardID);
    }

    private static void addBlueCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.DEFECT_WARDEN.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.DEFECT_STORMLORD.name(), card.cardID);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new RageStrike());
        BaseMod.addCard(new ShieldBash());
        BaseMod.addCard(new VenomStrike());
        BaseMod.addCard(new Dodge());
        BaseMod.addCard(new ProbingStrike());
        BaseMod.addCard(new AutoDefend());

        addRedCard(new Frenzy());
        addRedCard(new PowerAttack());
        addRedCard(new VitalStrike());

        addGreenCard(new Cloudkill());
        addGreenCard(new Paralyse());
        addGreenCard(new Vanish());

        addBlueCard(new ChainLightning());
        addBlueCard(new Cyclone());
        addBlueCard(new FrostFall());
    }

    private static String languagePath() {
        if ( Settings.language == Settings.GameLanguage.ZHS )
            return "zhs";
        return "eng";
    }

    @Override
    public void receiveEditStrings() {
        String basePath = "loc/" + languagePath() + "/";
        BaseMod.loadCustomStringsFile(CardStrings.class, basePath + "ICR-CardStrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, basePath + "ICR-PowerStrings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class, basePath + "ICR-CharacterStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        // read keywords from json
        String keywordPath = "loc/" + languagePath() + "/ICR-Keywords.json";
        String jsonString = Gdx.files.internal(keywordPath).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = (Keyword[])(new Gson()).fromJson(jsonString, Keyword[].class);
        // register with the game
        for ( Keyword k : keywords )
            BaseMod.addKeyword(k.NAMES, k.DESCRIPTION);
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("badge.png");
        ModPanel configPanel = new ModPanel();
        ModLabeledToggleButton guaranteeSubclassButton = new ModLabeledToggleButton(
                "Guarantee advanced subclass blessing.", 350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                guaranteeSubclass, configPanel, (label) -> {}, (button) -> {
                    guaranteeSubclass = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig(MODNAME, "config", defaultConfig);
                        config.setBool(CONFIG_GUARANTEE_SUBCLASS, guaranteeSubclass);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        configPanel.addUIElement(guaranteeSubclassButton);
        BaseMod.registerModBadge(badgeTexture, "Ironclad Rager", "Henning Koehler",
                "Adds subclasses with alternative starting decks.", configPanel);
    }

}
