package nimble;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import downfall.events.HeartEvent;
import downfall.util.HeartReward;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * applies NeowPatcher fixes to Heart events from Downfall
 */
public class DownfallPatcher {

    private static final Logger logger = LogManager.getLogger(DownfallPatcher.class.getName());

    @SpireEnum
    public static HeartReward.NeowRewardType NIMBLE_RELIC;

    @SpirePatch(
            clz = HeartEvent.class,
            method = "miniBlessing",
            paramtypez = {},
            requiredModId = "downfall"
    )
    public static class MiniBlessing {
        public static void Postfix(HeartEvent __instance) {
            HeartReward newReward = new HeartReward(false);
            newReward.type = NIMBLE_RELIC;
            newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("nimble:NeowReward").TEXT[0];
            ArrayList<HeartReward> rewards = (ArrayList<HeartReward>)Reflection.get(__instance, HeartEvent.class, "rewards");
            rewards.add(newReward);
            __instance.roomEventText.addDialogOption(newReward.optionLabel);
            logger.info("added NIMBLE_RELIC mini blessing");
        }
    }

    @SpirePatch(
            clz = HeartReward.class,
            method = "activate",
            paramtypez = {},
            requiredModId = "downfall"
    )
    public static class ActivatePatch {
        public static void Prefix(HeartReward __instance) {
            if (__instance.type == NIMBLE_RELIC)
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new LoadedDice());
        }
    }

}
