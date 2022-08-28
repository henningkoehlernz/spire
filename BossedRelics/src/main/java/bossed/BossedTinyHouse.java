package bossed;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyHouse;

public class BossedTinyHouse {

    @SpirePatch(
            clz = TinyHouse.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Prefix(TinyHouse __instance) {
            if (!BossedRelics.isDisabled(TinyHouse.ID)) {
                AbstractDungeon.player.potionSlots += 1;
                AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
            }
        }
    }

    // decrementBlock is called in AbstractPlayer.damage
    // we reduce damage here to ensure blocked damage is reduced as well
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "decrementBlock",
            paramtypez = {DamageInfo.class, int.class}
    )
    public static class DecrementBlock {
        public static void Prefix(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount) {
            if (__instance instanceof AbstractPlayer && !BossedRelics.isDisabled(TinyHouse.ID)
                    && damageAmount[0] > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                AbstractRelic r = ((AbstractPlayer)__instance).getRelic(TinyHouse.ID);
                if (r != null) {
                    damageAmount[0] -= 1;
                    r.flash();
                }
            }
        }
    }

}
