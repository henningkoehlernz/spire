package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyHouse;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedTinyHouse {

    static final int BLOCK = 3;
    static final int REPEATS = 5;

    @SpirePatch(
            clz = TinyHouse.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Replace(TinyHouse __instance) {
        }
    }

    @SpirePatch(
            clz = TinyHouse.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescription {
        public static String Replace(TinyHouse __instance) {
            if ( __instance.DESCRIPTIONS.length > 2 )
                return __instance.DESCRIPTIONS[0] + REPEATS + __instance.DESCRIPTIONS[1] + BLOCK + __instance.DESCRIPTIONS[2];
            else // legacy localization
                return __instance.DESCRIPTIONS[0] + BLOCK + __instance.DESCRIPTIONS[1] + " (x" + REPEATS + ")";
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atBattleStart",
            paramtypez = {}
    )
    public static class AtBattleStart {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof TinyHouse )
                __instance.counter = REPEATS;
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "wasHPLost",
            paramtypez = {int.class}
    )
    public static class WasHPLost {
        public static void Postfix(AbstractRelic __instance, int damageAmount) {
            if ( __instance instanceof TinyHouse ) {
                if ( AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && damageAmount > 0 && __instance.counter > 0 ) {
                    __instance.counter--;
                    __instance.flash();
                    AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MetallicizePower(AbstractDungeon.player, BLOCK), BLOCK));
                }
            }
        }
    }

}
