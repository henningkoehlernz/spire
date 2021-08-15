package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Cauldron;
import com.megacrit.cardcrawl.relics.WingBoots;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedWingBoots {

    @SpirePatch(
            clz = WingBoots.class,
            method = "setCounter",
            paramtypez = {int.class}
    )
    public static class SetCounter {
        public static void Replace(WingBoots __instance, int setCounter) {
            __instance.counter = Math.max(0, setCounter);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onRest",
            paramtypez = {}
    )
    public static class OnRest {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof WingBoots ) {
                __instance.flash();
                __instance.setCounter(3);
            }
        }
    }

}
