package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import java.util.Map;

public class RelicStringsPatch {

    // LocalizedStrings.relics is private, create reference here for fast access
    private static Map<String, RelicStrings> relics;

    @SpirePatch(
            clz = LocalizedStrings.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {}
    )
    public static class InitRelics {
        public static void Postfix(LocalizedStrings __instance) {
            relics = (Map<String, RelicStrings>)Reflection.get(null, LocalizedStrings.class, "relics");
        }
    }

    @SpirePatch(
            clz = LocalizedStrings.class,
            method = "getRelicStrings",
            paramtypez = {String.class}
    )
    public static class GetRelicStrings {
        public static SpireReturn<RelicStrings> Prefix(LocalizedStrings __instance, String relicName) {
            if (!BossedRelics.isDisabled(relicName)) {
                RelicStrings bossedRelicStrings = (RelicStrings) relics.get(BossedRelics.MOD_PREFIX + relicName);
                if (bossedRelicStrings != null)
                    return SpireReturn.Return(bossedRelicStrings);
            }
            return SpireReturn.Continue();
        }
    }

}
