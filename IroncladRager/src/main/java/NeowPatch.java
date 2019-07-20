package ironclad_rager;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowReward.NeowRewardDef;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.lang.reflect.Field;
import java.util.*;

public class NeowPatch {
    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_RAGER;

    @SpirePatch(
            clz = NeowEvent.class,
            method = "miniBlessing",
            paramtypez = {}
    )
    public static class RagerReward {
        private static final String[] optionStrings = { "Rager", "Poisoner" };
        public static void Postfix(NeowEvent __instance) {
            NeowReward newReward = new NeowReward(false);
            switch ( AbstractDungeon.player.chosenClass )
            {
                case IRONCLAD:
                    newReward.type = IRONCLAD_RAGER;
                    newReward.optionLabel = optionStrings[0];
                    break;
                default:
                    return;
            }
            // NeowEvent.reward is private - circumvent via reflection
            try {
                Field rewardField = NeowEvent.class.getDeclaredField("rewards");
                rewardField.setAccessible(true);
                ArrayList<NeowReward> rewards = (ArrayList<NeowReward>)rewardField.get(__instance);
                rewards.add(newReward);
            } catch (Exception e) {
                System.out.println(e);
            }
            __instance.roomEventText.addDialogOption("[ #gPlay #gas #g" + newReward.optionLabel + " ]");
        }
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "activate",
            paramtypez = {}
    )
    public static class ActivatePatch {
        public static void Prefix(NeowReward __instance) {
            if (__instance.type == IRONCLAD_RAGER) {
                // replace old strikes
                Iterator<AbstractCard> it = AbstractDungeon.player.masterDeck.group.iterator();
                while ( it.hasNext() ) {
                    AbstractCard e = (AbstractCard)it.next();
                    if ( e instanceof com.megacrit.cardcrawl.cards.red.Strike_Red ) {
                        it.remove();
                        RageStrike newStrike = new RageStrike();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(newStrike, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    }
                    else if ( e instanceof com.megacrit.cardcrawl.cards.red.Defend_Red ) {
                        it.remove();
                        ShieldBash newDefend = new ShieldBash();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(newDefend, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    }
                }
            }
        }
    }
}