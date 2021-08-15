package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedLizardTail {

    @SpirePatch(
            clz = LizardTail.class,
            method = "setCounter",
            paramtypez = {int.class}
    )
    public static class SetCounter {
        public static void Replace(LizardTail __instance, int setCounter) {
            __instance.counter = setCounter;
            __instance.grayscale = (setCounter != -1);
        }
    }

    @SpirePatch(
            clz = LizardTail.class,
            method = "onTrigger",
            paramtypez = {}
    )
    public static class OnTrigger {
        public static void Replace(LizardTail __instance) {
            __instance.flash();
            AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
            int healAmt = Math.max(1, AbstractDungeon.player.maxHealth * 3/10);
            AbstractDungeon.player.heal(healAmt, true);
            __instance.setCounter(5);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onEnterRoom",
            paramtypez = {AbstractRoom.class}
    )
    public static class OnEnterRoom {
        public static void Postfix(AbstractRelic __instance, AbstractRoom room) {
            int counter = __instance.counter;
            if ( __instance instanceof LizardTail ) {
                if ( counter > 1 )
                    __instance.setCounter(counter - 1);
                else if ( counter != -1 )
                    __instance.setCounter(-1);
            }
        }
    }

}
