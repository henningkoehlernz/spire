package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
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
        public static SpireReturn<Void> Prefix(MawBank __instance, AbstractRoom room) {
            if (BossedRelics.isDisabled(MawBank.ID))
                return SpireReturn.Continue();
            if ( __instance.counter < 0 ) {
                __instance.flash();
                AbstractDungeon.player.gainGold(12);
            } else if ( __instance.counter == 1 )
                __instance.counter = -1;
            else
                __instance.counter -= 1;
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = MawBank.class,
            method = "onSpendGold",
            paramtypez = {}
    )
    public static class OnSpendGold {
        public static SpireReturn<Void> Prefix(MawBank __instance, AbstractRoom room) {
            if (BossedRelics.isDisabled(MawBank.ID))
                return SpireReturn.Continue();
            __instance.flash();
            __instance.counter = 3;
            return SpireReturn.Return();
        }
    }

}
