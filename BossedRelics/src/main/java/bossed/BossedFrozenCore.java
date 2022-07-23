package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.relics.FrozenCore;

public class BossedFrozenCore {

    @SpirePatch(
            clz = FrozenCore.class,
            method = "onPlayerEndTurn",
            paramtypez = {}
    )
    public static class OnPlayerEndTurn {
        public static SpireReturn<Void> Prefix(FrozenCore __instance) {
            if (BossedRelics.isDisabled(Ectoplasm.ID))
                return SpireReturn.Continue();
            __instance.flash();
            AbstractDungeon.player.channelOrb(new Frost());
            return SpireReturn.Return();
        }
    }

}
