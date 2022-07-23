package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Strawberry;
import com.megacrit.cardcrawl.relics.Pear;
import com.megacrit.cardcrawl.relics.Mango;

public class BossedFruits {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onVictory",
            paramtypez = {}
    )
    public static class OnVictory {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof Strawberry && !BossedRelics.isDisabled(Strawberry.ID)
                    || __instance instanceof Pear && !BossedRelics.isDisabled(Pear.ID)
                    || __instance instanceof Mango && !BossedRelics.isDisabled(Mango.ID)) {
                if ( AbstractDungeon.player.currentHealth > 0 ) {
                    __instance.flash();
                    AbstractDungeon.player.heal(1);
                }
            }
        }
    }

    @SpirePatch(
            clz = Strawberry.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescriptionStrawberry {
        public static String Postfix(String description, Strawberry __instance) {
            if ( __instance.DESCRIPTIONS.length > 1 )
                description += ' ' + __instance.DESCRIPTIONS[1];
            return description;
        }
    }

    @SpirePatch(
            clz = Pear.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescriptionPear {
        public static String Postfix(String description, Pear __instance) {
            if ( __instance.DESCRIPTIONS.length > 1 )
                description += ' ' + __instance.DESCRIPTIONS[1];
            return description;
        }
    }

    @SpirePatch(
            clz = Mango.class,
            method = "getUpdatedDescription",
            paramtypez = {}
    )
    public static class GetUpdatedDescriptionMango {
        public static String Postfix(String description, Mango __instance) {
            if ( __instance.DESCRIPTIONS.length > 1 )
                description += ' ' + __instance.DESCRIPTIONS[1];
            return description;
        }
    }

}
