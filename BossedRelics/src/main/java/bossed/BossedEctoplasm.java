package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Ectoplasm;

public class BossedEctoplasm {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "gainGold",
            paramtypez = { int.class }
    )
    public static class GainGold {
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, int amount) {
            if (amount > 0) {
                CardCrawlGame.goldGained += amount;
                __instance.gold += amount;
                for (AbstractRelic relic : __instance.relics)
                    relic.onGainGold();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onLoseHp",
            paramtypez = { int.class }
    )
    public static class OnLoseHp {
        public static void Prefix(AbstractRelic __instance, int damageAmount) {
            if (__instance instanceof Ectoplasm) {
                int goldAmt = Math.min(damageAmount, AbstractDungeon.player.gold);
                if (goldAmt > 0) {
                    AbstractDungeon.player.loseGold(goldAmt);
                    __instance.counter = Math.max(0, __instance.counter) + goldAmt;
                    // some animation & sound
                    __instance.flash();
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                }
            }
        }

    }

    /*
    @SpirePatch(
            clz = RewardItem.class,
            method = "applyGoldBonus",
            paramtypez = { boolean.class }
    )
    public static class ApplyGoldBonus {
        public static void Prefix(RewardItem __instance, boolean theft) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(Ectoplasm.ID);
            if (!theft && relic != null) {
                int reduction = __instance.goldAmt / 2;
                if (reduction > 0) {
                    __instance.goldAmt -= reduction;
                    relic.flash();
                    relic.counter = Math.max(0, relic.counter) + reduction;
                }
            }
        }
    }
    */

}
