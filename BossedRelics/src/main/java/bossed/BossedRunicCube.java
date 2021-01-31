package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.RunicCube;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedRunicCube {

    @SpirePatch(
            clz = RunicCube.class,
            method = "wasHPLost",
            paramtypez = {int.class}
    )
    public static class WasHPLost {
        public static void Replace(RunicCube __instance, int damageAmount) {
            if ( AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && damageAmount > 0 ) {
                __instance.flash();
                AbstractDungeon.actionManager.addToTop(new DrawCardAction(1, new DrawnCardsFreeToPlayOnceAction()));
            }
        }
    }

}
