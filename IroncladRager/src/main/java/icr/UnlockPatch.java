package icr;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnlockPatch {
    private static final Logger logger = LogManager.getLogger(UnlockPatch.class.getName());
    // track cards locked by Neow Rewards
    private static Set<String> lockedCards = new TreeSet<String>();
    private static Map<String,Vector<String>> rewardUnlocks = new TreeMap<String,Vector<String>>();

    private static boolean isCardLocked(String cardID) {
        if ( !lockedCards.contains(cardID) )
            return false;
        Vector<String> cards = rewardUnlocks.get(CardCrawlGame.metricData.neowBonus);
        return cards == null || !cards.contains(cardID);
    }

    public static void unlockCardWithReward(String reward, String cardID) {
        logger.debug(reward + " unlocks " + cardID);
        lockedCards.add(cardID);
        Vector<String> cards = rewardUnlocks.get(reward);
        if ( cards == null ) {
            cards = new Vector<String>();
            rewardUnlocks.put(reward, cards);
        }
        cards.add(cardID);
    }

    @SpirePatch(
            clz = UnlockTracker.class,
            method = "isCardLocked",
            paramtypez = {String.class}
    )
    public static class IsCardLocked {
        public static boolean Postfix(boolean __result, String key) {
            if ( isCardLocked(key) )
                __result = true;
            logger.debug("isCardLocked(" + key + ") = " + __result);
            return __result;
        }
    }

}