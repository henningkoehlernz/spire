package evolution;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import java.util.ArrayList;

public class InfoPatcher {

    private static Texture TP_EVO = new Texture(Evolution.IMG_PATH + "evolution.png");

    @SpirePatch(
            clz = CharSelectInfo.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, int.class, int.class, int.class, int.class, int.class, AbstractPlayer.class, ArrayList.class, ArrayList.class, boolean.class}
    )
    public static class CharSelectInfoConstructor {
        public static void Postfix(CharSelectInfo __instance, String name, String flavorText, int currentHp, int maxHp, int maxOrbs, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, boolean resumeGame) {
            __instance.relics.add(Axolotl.ID);
        }
    }

    @SpirePatch(
            clz = CharacterOption.class,
            method = "renderInfo",
            paramtypez = {SpriteBatch.class}
    )
    public static class CharacterOptionRenderInfo {
        public static void Postfix(CharacterOption __instance, SpriteBatch sb) {
            if ( !__instance.name.equals("") && !Settings.isMobile ) {
                Texture TP_EVO = new Texture(Evolution.IMG_PATH + "evolution.png");
                int ascension = CardCrawlGame.mainMenuScreen.charSelectScreen.isAscensionMode ? __instance.c.getPrefs().getInteger("LAST_ASCENSION_LEVEL") : 0;
                int ep = Evolution.getEvolutionTotal(__instance.c.chosenClass, ascension);
                float infoX = (float) Reflection.get(__instance, CharacterOption.class, "infoX");
                float infoY = (float) Reflection.get(__instance, CharacterOption.class, "infoY");
                sb.draw(TP_EVO, infoX + 390.0F * Settings.scale - 32.0F, infoY + 95.0F * Settings.scale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                FontHelper.renderSmartText(sb, FontHelper.tipHeaderFont, "EP: " + Integer.toString(ep), infoX + 420.0F * Settings.scale, infoY + 102.0F * Settings.scale, 10000.0F, 10000.0F, Settings.BLUE_TEXT_COLOR);
            }
        }
    }
}
