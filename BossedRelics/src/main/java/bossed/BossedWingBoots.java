package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WingBoots;

public class BossedWingBoots {

    @SpirePatch(
            clz = WingBoots.class,
            method = "setCounter",
            paramtypez = {int.class}
    )
    public static class SetCounter {
        public static SpireReturn<Void> Prefix(WingBoots __instance, int setCounter) {
            if (BossedRelics.isDisabled(WingBoots.ID))
                return SpireReturn.Continue();
            __instance.counter = Math.max(0, setCounter);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onRest",
            paramtypez = {}
    )
    public static class OnRest {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof WingBoots && !BossedRelics.isDisabled(WingBoots.ID)) {
                __instance.flash();
                __instance.setCounter(3);
            }
        }
    }

}
