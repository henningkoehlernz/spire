package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.OldCoin;

public class BossedOldCoin {

    @SpirePatch(
            clz = OldCoin.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static SpireReturn<Void> Prefix(OldCoin __instance) {
            if (BossedRelics.isDisabled(OldCoin.ID))
                return SpireReturn.Continue();
            CardCrawlGame.sound.play("GOLD_GAIN");
            AbstractDungeon.player.gainGold(250);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onGainGold",
            paramtypez = {}
    )
    public static class OnGainGold {
        private static final int BONUS_GOLD = 5;
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof OldCoin && !BossedRelics.isDisabled(OldCoin.ID)) {
                __instance.flash();
                CardCrawlGame.goldGained += BONUS_GOLD;
                AbstractDungeon.player.gold += BONUS_GOLD;
                __instance.counter = Math.max(0, __instance.counter) + BONUS_GOLD;
            }
        }
    }

}
