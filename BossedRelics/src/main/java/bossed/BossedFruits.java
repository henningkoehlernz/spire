package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Strawberry;
import com.megacrit.cardcrawl.relics.Pear;
import com.megacrit.cardcrawl.relics.Mango;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossedFruits {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onVictory",
            paramtypez = {}
    )
    public static class OnVictory {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof Strawberry || __instance instanceof Pear || __instance instanceof Mango ) {
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
