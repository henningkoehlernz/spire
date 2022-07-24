package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SacredBark;

public class BossedSacredBark {

    public static int potionCount(AbstractPlayer p) {
        int count = 0;
        for ( AbstractPotion potion : p.potions ) {
            if ( !(potion instanceof PotionSlot) )
                count++;
        }
        return count;
    }

    public static int emptyPotionSlots() {
        int count = 0;
        for ( AbstractPotion potion : AbstractDungeon.player.potions ) {
            if ( potion instanceof PotionSlot )
                count++;
        }
        return count;
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onLoseHpLast",
            paramtypez = {int.class}
    )
    public static class OnLoseHpLast {
        public static int Postfix(int damageAmount, AbstractRelic __instance, int __originalDamageAmount) {
            if (__instance instanceof SacredBark && damageAmount > 0 && !BossedRelics.isDisabled(SacredBark.ID)) {
                int dr = Math.min(2, emptyPotionSlots());
                if ( dr > 0 ) {
                    __instance.flash();
                    return Math.max(0, damageAmount - dr);
                }
            }
            return damageAmount;
        }
    }

}
