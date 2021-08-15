package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Cauldron;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedCauldron {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onUsePotion",
            paramtypez = {}
    )
    public static class OnUsePotion {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof Cauldron && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT ) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
            }
        }
    }

}
