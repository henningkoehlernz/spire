package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Astrolabe;

public class BossedAstrolabe {

    @SpirePatch(
            clz = Astrolabe.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static SpireReturn<Void> Prefix(Astrolabe __instance) {
            return BossedRelics.isDisabled(Astrolabe.ID) ? SpireReturn.Continue() : SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = Astrolabe.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescription {
        public static SpireReturn<String> Prefix(Astrolabe __instance) {
            if (BossedRelics.isDisabled(Astrolabe.ID))
                return SpireReturn.Continue();
            RelicStrings strings = BossedRelics.getRelicStrings(Astrolabe.ID);
            return SpireReturn.Return(strings.DESCRIPTIONS[0]);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStartPostDraw",
            paramtypez = {}
    )
    public static class AtTurnStartPostDraw {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof Astrolabe && !BossedRelics.isDisabled(Astrolabe.ID)) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new SuperTransformAction());
            }
        }
    }

}
