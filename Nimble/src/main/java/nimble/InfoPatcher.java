package nimble;

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

    private static Texture TP_EVO = new Texture(Nimble.IMG_PATH + "evolution.png");

    @SpirePatch(
            clz = CharSelectInfo.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, int.class, int.class, int.class, int.class, int.class, AbstractPlayer.class, ArrayList.class, ArrayList.class, boolean.class}
    )
    public static class CharSelectInfoConstructor {
        public static void Postfix(CharSelectInfo __instance, String name, String flavorText, int currentHp, int maxHp, int maxOrbs, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, boolean resumeGame) {
            __instance.relics.add(ShrinkRayGun.ID);
        }
    }

}
