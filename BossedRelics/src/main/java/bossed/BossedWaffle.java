package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.blights.Muzzle;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Waffle;

public class BossedWaffle {

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "increaseMaxHp",
            paramtypez = {int.class, boolean.class}
    )
    public static class OnRest {
        public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
            if ( amount > 0 && __instance == AbstractDungeon.player ) {
                AbstractRelic waffle = AbstractDungeon.player.getRelic(Waffle.ID);
                boolean muzzled = Settings.isEndless && AbstractDungeon.player.hasBlight(Muzzle.ID);
                if ( waffle != null && !muzzled ) {
                    waffle.flash();
                    __instance.heal(amount, true);
                }
            }
        }
    }

}
