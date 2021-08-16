package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
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
        public static void Replace(OldCoin __instance) {
            CardCrawlGame.sound.play("GOLD_GAIN");
            AbstractDungeon.player.gainGold(250);
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
            if ( __instance instanceof OldCoin ) {
                __instance.flash();
                CardCrawlGame.goldGained += BONUS_GOLD;
                AbstractDungeon.player.gold += BONUS_GOLD;
                __instance.counter = Math.max(0, __instance.counter) + BONUS_GOLD;
            }
        }
    }

}
