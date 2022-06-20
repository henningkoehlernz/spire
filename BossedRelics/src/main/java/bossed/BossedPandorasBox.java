package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
        public static SpireReturn<Void> Prefix(PandorasBox __instance) {
            return BossedRelics.isDisabled(PandorasBox.ID) ? SpireReturn.Continue() : SpireReturn.Return();
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
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnRelics",
            paramtypez = {}
    )
    public static class ApplyStartOfTurnRelics {
        public static void Postfix(AbstractPlayer __instance) {
            AbstractRelic relic = __instance.getRelic(PandorasBox.ID);
            if (relic != null && !BossedRelics.isDisabled(PandorasBox.ID)) {
                relic.flash();
                AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(target, false));
            }
        }
    }

}
