package th;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class TreasurePatch {

    private static final Logger logger = LogManager.getLogger(TreasurePatch.class.getName());

    @SpireEnum
    public static AbstractCard.CardType TREASURE;

    @SpirePatch(
            clz = AbstractCard.class,
            method = "canUpgrade",
            paramtypez = {}
    )
    public static class CanUpgrade {
        public static boolean Postfix(boolean __result, AbstractCard __instance) {
            if ( __instance.type == TREASURE )
                return true;
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "getCardBgAtlas",
            paramtypez = {}
    )
    public static class GetCardBgAtlas {
        public static TextureAtlas.AtlasRegion Postfix(TextureAtlas.AtlasRegion __result, AbstractCard __instance) {
            if ( __result == null )
                return ImageMaster.CARD_SKILL_BG_SILHOUETTE;
            return __result;
        }
    }

    // gain access to private methods
    private static void invoke(Object obj, String methodName, Object... args) {
        Class<?>[] classArray = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Class c = args[i].getClass();
            // we'll make a guess here that the method uses int/float rather than Integer/Float
            classArray[i] =
                    (c == Integer.class) ? int.class :
                    (c == Float.class) ? float.class : c;
        }
        try {
            Method m = obj.getClass().getDeclaredMethod(methodName, classArray);
            m.setAccessible(true);
            m.invoke(obj, args);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderPortraitFrame",
            paramtypez = {SpriteBatch.class, float.class, float.class}
    )
    public static class RenderPortraitFrame {
        @SpireInsertPatch(
                rloc=2,
                localvars={"tWidth", "tOffset"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, float x, float y,
                                  @ByRef float[] tWidth, @ByRef float[] tOffset) {
            if ( __instance.type == TREASURE ) {
                invoke(__instance,"renderSkillPortrait", sb, x, y);
                tWidth[0] = AbstractCard.typeWidthSkill;
                tOffset[0] = AbstractCard.typeOffsetSkill;
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderType",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderType {
        @SpireInsertPatch(
                rloc=24,
                localvars={"text"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef(type="String") Object[] text) {
            if ( __instance.type == TREASURE )
                text[0] = "Loot";
        }
    }

    private void renderType(SpriteBatch sb) {

    }

}