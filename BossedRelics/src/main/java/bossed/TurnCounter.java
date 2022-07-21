package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;

public class TurnCounter {

    // GameActionManager.turn is 1 for the first two turns during calls to atTurnStart, so need separate turn tracking
    public static int turn = 0;

    @SpirePatch(
            clz = GameActionManager.class,
            method = "clear",
            paramtypez = {}
    )
    public static class Clear {
        public static void Postfix(GameActionManager __instance) {
            turn = 0;
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "endTurn",
            paramtypez = {}
    )
    public static class EndTurn {
        public static void Prefix(GameActionManager __instance) {
            ++turn;
        }
    }

}
