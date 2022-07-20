package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PandorasBox;

public class BossedPandorasBox {

    @SpirePatch(
            clz = PandorasBox.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Postfix(PandorasBox __instance) {
            if (BossedRelics.isDisabled(PandorasBox.ID))
                return;
            __instance.counter = (Integer)Reflection.get(__instance, PandorasBox.class, "count");
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescription {
        public static SpireReturn<String> Prefix(PandorasBox __instance) {
            if (BossedRelics.isDisabled(PandorasBox.ID))
                return SpireReturn.Continue();
            RelicStrings strings = BossedRelics.getRelicStrings(PandorasBox.ID);
            return SpireReturn.Return(strings.DESCRIPTIONS[0]);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStart",
            paramtypez = {}
    )
    public static class AtTurnStart {
        public static void Prefix(AbstractRelic __instance) {
            if (__instance instanceof PandorasBox && !BossedRelics.isDisabled(PandorasBox.ID)) {
                if (GameActionManager.turn >= __instance.counter) {
                    __instance.flash();
                    AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(target, false));
                }
            }
        }
    }

}
