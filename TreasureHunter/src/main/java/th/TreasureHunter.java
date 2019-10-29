package th;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
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

import java.nio.charset.StandardCharsets;

@SpireInitializer
public class TreasureHunter implements EditCardsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber {

    // mod config variables
    private static final String MODNAME = "TreasureHunter";

    public TreasureHunter() {
        BaseMod.subscribe(this);
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
