package th;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
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
                return false;
            return __result;
        }
    }

    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave",
            paramtypez = {AbstractPlayer.class}
    )
    public static class DeleteSave {
        public static void Prefix(AbstractPlayer p) {
            int treasureCount = 0;
            for ( AbstractCard card : p.masterDeck.group ) {
                if ( card.type == TREASURE )
                    treasureCount++;
            }
            TreasureHunter.addTreasure(treasureCount);
        }
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "activate",
            paramtypez = {}
    )
    public static class NeowRewardActivate {
        public static void Postfix(NeowReward __instance) {
            AbstractDungeon.player.gainGold(TreasureHunter.treasure);
        }
    }

    @SpirePatch(
            clz = AbstractChest.class,
            method = "open",
            paramtypez = {boolean.class}
    )
    public static class OpenChest {
        public static void Prefix(AbstractChest __instance, boolean bossChest) {
            RewardItem treasureReward = new RewardItem();
            treasureReward.cards.clear();
            treasureReward.cards.add(TreasureHunter.randomTreasure());
            AbstractDungeon.getCurrRoom().addCardReward(treasureReward);
        }
    }

    //----------------------- Visuals -------------------------------

    private static String getTreasureText() {
        return CardCrawlGame.languagePack.getUIString("TH:Treasure").TEXT[0];
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescription",
            paramtypez = {}
    )
    public static class InitializeDescription {
        @SpireInsertPatch(
                rloc=1
        )
        public static void Insert(AbstractCard __instance) {
            if ( __instance.type == TREASURE )
                __instance.keywords.add(getTreasureText().toLowerCase());
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
    private static void invoke(AbstractCard obj, String methodName, Object... args) {
        Class<?>[] classArray = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Class c = args[i].getClass();
            // we'll make a guess here that the method uses int/float rather than Integer/Float
            classArray[i] =
                    (c == Integer.class) ? int.class :
                    (c == Float.class) ? float.class : c;
        }
        try {
            Method m = AbstractCard.class.getDeclaredMethod(methodName, classArray);
            m.setAccessible(true);
            m.invoke(obj, args);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardBg",
            paramtypez = {SpriteBatch.class, float.class, float.class}
    )
    public static class RenderCardBg {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
            if ( __instance.type == TREASURE )
                invoke(__instance, "renderSkillBg", sb, x, y);
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
                text[0] = getTreasureText();
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderCardTypeText",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderCardTypeText {
        @SpireInsertPatch(
                rloc=1,
                localvars={"label"}
        )
        public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, @ByRef(type="String") Object[] label) {
            try {
                // access __instance.card via reflection
                Field cardField = SingleCardViewPopup.class.getDeclaredField("card");
                cardField.setAccessible(true);
                AbstractCard card = (AbstractCard) cardField.get(__instance);
                // now the actual code
                if (card.type == TREASURE)
                    label[0] = getTreasureText();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

}
