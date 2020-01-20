package th;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.GainGoldTextEffect;
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

    //----------------------- mechanics -----------------------------

    public static int getTreasureCount() {
        int treasureCount = 0;
        for ( AbstractCard card : AbstractDungeon.player.masterDeck.group ) {
            if ( card.type == TREASURE )
                treasureCount++;
        }
        return treasureCount;
    }

    private static int getTreasureScore(int treasures) {
        // cap effective treasures (e.g. infinite spire)
        treasures = Math.min(treasures, 20);
        return treasures * treasures;
    }

    /*
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
    */

    // update total treasures collected
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave",
            paramtypez = {AbstractPlayer.class}
    )
    public static class DeleteSave {
        public static void Prefix(AbstractPlayer p) {
            TreasureHunter.addTreasure(p.chosenClass, AbstractDungeon.ascensionLevel, getTreasureScore(getTreasureCount()));
        }
    }

    // grant bonus gold for new games
    @SpirePatch(
            clz = NeowReward.class,
            method = "activate",
            paramtypez = {}
    )
    public static class NeowRewardActivate {
        public static void Postfix(NeowReward __instance) {
            int bonusGold = TreasureHunter.getTreasureTotal(AbstractDungeon.player.chosenClass, AbstractDungeon.ascensionLevel);
            AbstractDungeon.player.gainGold(bonusGold);
            AbstractDungeon.effectList.add(new GainGoldTextEffect(bonusGold));
        }
    }

    private static RewardItem getTreasureReward() {
        int numCards = 2;
        for ( AbstractRelic r : AbstractDungeon.player.relics ) {
            numCards = r.changeNumberOfCardsInReward(numCards);
        }
        // ensure busted crown doesn't reduce to zero
        if ( numCards < 1 )
            numCards = 1;
        RewardItem reward = new RewardItem();
        reward.cards.clear();
        // pick cards
        int attempts = 0;
        while ( reward.cards.size() < numCards && attempts++ < 1000 ) {
            AbstractCard treasure = TreasureHunter.getRandomTreasure();
            boolean duplicate = false;
            for ( AbstractCard c : reward.cards )
                if ( c.getClass() == treasure.getClass() ) {
                    duplicate = true;
                    break;
                }
            if ( !duplicate )
                reward.cards.add(treasure.makeCopy());
        }
        return reward;
    }

    // chests contain treasures
    @SpirePatch(
            clz = AbstractChest.class,
            method = "open",
            paramtypez = {boolean.class}
    )
    public static class OpenChest {
        public static void Prefix(AbstractChest __instance, boolean bossChest) {
            AbstractDungeon.getCurrRoom().addCardReward(getTreasureReward());
        }
    }

    // boss & elite rooms contain treasures
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward",
            paramtypez = {}
    )
    public static class SetupItemReward {
        @SpireInsertPatch(
                rloc=3
        )
        public static void Insert(CombatRewardScreen __instance) {
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            if ( room instanceof MonsterRoomBoss || room instanceof MonsterRoomElite )
                __instance.rewards.add(getTreasureReward());
        }
    }

    //----------------------- feedback ------------------------------

    private static String[] getTreasureText() {
        return CardCrawlGame.languagePack.getUIString("TH:Treasure").TEXT;
    }

    private static GameOverStat getTreasureStat() {
        int treasures = getTreasureCount();
        int score = getTreasureScore(treasures);
        return new GameOverStat(getTreasureText()[1] + " (" + treasures + ")", null, "+" + score + " gold");
    }

    // show number of treasures collected as part of score
    @SpirePatch(
            clz = DeathScreen.class,
            method = "createGameOverStats",
            paramtypez = {}
    )
    public static class Death_CreateGameOverStats {
        public static void Postfix(DeathScreen __instance) {
            __instance.stats.add(__instance.stats.size() - 2, getTreasureStat());
        }
    }

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "createGameOverStats",
            paramtypez = {}
    )
    public static class Victory_CreateGameOverStats {
        public static void Postfix(VictoryScreen __instance) {
            __instance.stats.add(__instance.stats.size() - 2, getTreasureStat());
        }
    }

    // add treasure keyword to treasures
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
                __instance.keywords.add(getTreasureText()[0].toLowerCase());
        }
    }

    // show bonus gold in character select screen
    @SpirePatch(
            clz = CharacterOption.class,
            method = "renderInfo",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderInfo {
        @SpireInsertPatch(
                rloc = 1
        )
        public static void Insert(CharacterOption __instance, SpriteBatch sb) {
            int ascension = CardCrawlGame.mainMenuScreen.charSelectScreen.isAscensionMode ?
                    CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel : 0;
            int bonusGold = TreasureHunter.getTreasureTotal(__instance.c.chosenClass, ascension);
            // access private variables
            float infoX = (Float) Reflection.get(__instance, null,"infoX");
            float infoY = (Float) Reflection.get(__instance, null, "infoY");
            int gold = (Integer) Reflection.get(__instance, null, "gold");
            // paint over original rendering
            String goldText = CharacterOption.TEXT[5] + gold + "+" + bonusGold;
            FontHelper.renderSmartText(sb, FontHelper.tipHeaderFont, goldText,
                    infoX + 220.0F * Settings.scale, infoY + 102.0F * Settings.scale, 10000.0F, 10000.0F, Settings.GOLD_COLOR);
        }
    }

    //----------------------- card rendering fixes ------------------

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

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardBg",
            paramtypez = {SpriteBatch.class, float.class, float.class}
    )
    public static class RenderCardBg {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
            if ( __instance.type == TREASURE )
                Reflection.invoke(__instance, AbstractCard.class, "renderSkillBg", sb, x, y);
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
                Reflection.invoke(__instance, AbstractCard.class, "renderSkillPortrait", sb, x, y);
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
                text[0] = getTreasureText()[0];
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
            AbstractCard card = (AbstractCard) Reflection.get(__instance, null, "card");
            // now the actual code
            if ( card.type == TREASURE )
                label[0] = getTreasureText()[0];
        }
    }

}
