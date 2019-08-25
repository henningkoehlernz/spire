package icr;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;

import basemod.BaseMod;
import basemod.interfaces.*;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;

import java.util.Properties;

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

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "loc/eng/ICR-CardStrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "loc/eng/ICR-PowerStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        String[] bloodied = { "bloodied" };
        BaseMod.addKeyword("Bloodied", bloodied, "At or below 50% health.");
        String[] concealment = { "concealment" };
        BaseMod.addKeyword("Concealment", concealment, "Reduces all damage taken for one turn.");
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
