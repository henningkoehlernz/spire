package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.EmptyCage;

public class BossedEmptyCage {

    @SpirePatch(
            clz = EmptyDeckShuffleAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {}
    )
    public static class EmptyCageDeckShuffle {
        public static void Postfix(EmptyDeckShuffleAction __instance) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(EmptyCage.ID);
            if ( relic != null && !relic.usedUp ) {
                relic.usedUp = true;
                relic.flash();
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnRelics",
            paramtypez = {}
    )
    public static class ApplyStartOfTurnRelics {
        public static void Postfix(AbstractPlayer __instance) {
            AbstractRelic relic = __instance.getRelic(EmptyCage.ID);
            if ( relic != null ) {
                relic.usedUp = false;
            }
        }
    }

}
