package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Astrolabe;

public class BossedAstrolabe {

    @SpirePatch(
            clz = Astrolabe.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Replace(Astrolabe __instance) {
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStartPostDraw",
            paramtypez = {}
    )
    public static class AtTurnStartPostDraw {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof Astrolabe) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new SuperTransformAction());
            }
        }
    }

}
