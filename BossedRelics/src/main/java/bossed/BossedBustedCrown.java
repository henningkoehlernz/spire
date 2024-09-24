package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.BustedCrown;

public class BossedBustedCrown {

    @SpirePatch(
            clz = BustedCrown.class,
            method = "changeNumberOfCardsInReward",
            paramtypez = {int.class}
    )
    public static class ChangeNumberOfCardsInReward {
        public static SpireReturn<Integer> Prefix(BustedCrown __instance, int numberOfCards) {
            if (BossedRelics.isDisabled(BustedCrown.ID))
                return SpireReturn.Continue();
            return SpireReturn.Return(numberOfCards - 1);
        }
    }

}
