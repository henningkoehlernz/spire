package nimble;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class NeowPatcher {

    private static final Logger logger = LogManager.getLogger(NeowPatcher.class.getName());

    @SpireEnum
    public static NeowReward.NeowRewardType NIMBLE_RELIC;

    @SpirePatch(
            clz = NeowEvent.class,
            method = "miniBlessing",
            paramtypez = {}
    )
    public static class MiniBlessing {
        public static void Postfix(NeowEvent __instance) {
            NeowReward newReward = new NeowReward(false);
            newReward.type = NIMBLE_RELIC;
            newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("nimble:NeowReward").TEXT[0];
            ArrayList<NeowReward> rewards = (ArrayList<NeowReward>)Reflection.get(__instance, NeowEvent.class, "rewards");
            rewards.add(newReward);
            __instance.roomEventText.addDialogOption(newReward.optionLabel);
            logger.info("added NIMBLE_RELIC mini blessing");
        }
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "activate",
            paramtypez = {}
    )
    public static class ActivatePatch {
        public static void Prefix(NeowReward __instance) {
            if (__instance.type == NIMBLE_RELIC)
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new LoadedDice());
        }
    }

}
