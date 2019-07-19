package ironclad_rager;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowReward.NeowRewardDef;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.*;

public class NeowPatch {
    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_RAGER;

    @SpirePatch(
            clz = NeowReward.class,
            method = "getRewardOptions",
            paramtypez = {int.class}
    )
    public static class RagerReward {
        public static ArrayList<NeowRewardDef> Postfix(ArrayList<NeowRewardDef> __result, NeowReward __instance, final int category) {
            if (category == 3)
                __result.add(new NeowRewardDef(IRONCLAD_RAGER, "[ Channel your inner Rage ]"));
            return __result;
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
                }
            }
        }
    }
}