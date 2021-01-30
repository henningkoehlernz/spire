package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
        public static void Replace(PandorasBox __instance) {
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "removeStrikeTip",
            paramtypez = {}
    )
    public static class RemoveStrikeTip {
        public static void Replace(PandorasBox __instance) {
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
            if ( relic != null ) {
                relic.flash();
                AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(target, false));
            }
        }
    }

}
