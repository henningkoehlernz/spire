package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DebugPatch {

    /*
    private static final Logger logger = LogManager.getLogger(BossedRelics.class.getName());

    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "open",
            paramtypez = {String.class, int.class, boolean.class, boolean.class}
    )
    public static class HandCardSelectScreenOpen {
        public static void Prefix(HandCardSelectScreen __instance, String msg, int amount, boolean anyNumber, boolean canPickZero) {
            logger.info("HandCardSelectScreen.open(" + msg + ", ...)");
        }
    }

    public static void logQueues() {
        ArrayList<String> actionQueue = AbstractDungeon.actionManager.actions.stream().map(o -> o.getClass().getSimpleName()).collect(Collectors.toCollection(ArrayList::new));
        logger.info("actions queued: " + actionQueue);
        ArrayList<String> cardQueue = AbstractDungeon.actionManager.cardQueue.stream().map(o -> o.card.getClass().getSimpleName()).collect(Collectors.toCollection(ArrayList::new));
        logger.info("cards queued: " + cardQueue);
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "update",
            paramtypez = {}
    )
    public static class GameActionManagerUpdate {
        public static void Prefix(GameActionManager __instance) {
            AbstractGameAction action = __instance.currentAction;
            if (action != null && action.isDone) {
                logger.info("GameActionManager: completed " + action.getClass().getName());
                logQueues();
            }
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction",
            paramtypez = {}
    )
    public static class GameActionManagerGetNextAction {
        public static void Prefix(GameActionManager __instance) {
            if (!__instance.actions.isEmpty() || !__instance.cardQueue.isEmpty()) {
                logger.info("GameActionManager: getting next action");
                logQueues();
            }
        }
    }
    */

}
