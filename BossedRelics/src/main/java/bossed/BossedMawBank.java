package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MawBank;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedMawBank {

    @SpirePatch(
            clz = MawBank.class,
            method = "onEnterRoom",
            paramtypez = {AbstractRoom.class}
    )
    public static class OnEnterRoom {
        public static void Replace(MawBank __instance, AbstractRoom room) {
            if ( __instance.counter < 0 ) {
                __instance.flash();
                AbstractDungeon.player.gainGold(12);
            } else if ( __instance.counter == 1 )
                __instance.counter = -1;
            else
                __instance.counter -= 1;
        }
    }

    @SpirePatch(
            clz = MawBank.class,
            method = "onSpendGold",
            paramtypez = {}
    )
    public static class OnSpendGold {
        public static void Replace(MawBank __instance, AbstractRoom room) {
            __instance.flash();
            __instance.counter = 3;
        }
    }

}
