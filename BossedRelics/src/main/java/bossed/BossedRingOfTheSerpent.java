package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.RingOfTheSerpent;

public class BossedRingOfTheSerpent {

    @SpirePatch(
            clz = RingOfTheSerpent.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Prefix(RingOfTheSerpent __instance) {
            ++AbstractDungeon.player.masterHandSize;
        }
    }

    @SpirePatch(
            clz = RingOfTheSerpent.class,
            method = "onUnequip",
            paramtypez = {}
    )
    public static class OnUnequip {
        public static void Prefix(RingOfTheSerpent __instance) {
            --AbstractDungeon.player.masterHandSize;
        }
    }

}
