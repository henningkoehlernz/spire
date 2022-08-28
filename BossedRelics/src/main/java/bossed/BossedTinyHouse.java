package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
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

    // fix intent damage (=damage preview)
    // current implementation does not check for player relics (bug)
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "calculateDamage",
            paramtypez = {int.class}
    )
    public static class CalculateDamage {
        private static final String fieldName = "intentDmg";
        public static void Postfix(AbstractMonster __instance, int __dmg) {
            if (!BossedRelics.isDisabled(TinyHouse.ID) && AbstractDungeon.player.hasRelic(TinyHouse.ID)) {
                int intentDmg = (Integer)Reflection.get(__instance, AbstractMonster.class, fieldName);
                if (intentDmg > 0) {
                    Reflection.set(__instance, AbstractMonster.class, fieldName, intentDmg - 1);
                }
            }
        }
    }

    // fix actual damage received
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onAttackedToChangeDamage",
            paramtypez = {DamageInfo.class, int.class}
    )
    public static class OnAttackedToChangeDamage {
        public static SpireReturn<Integer> Prefix(AbstractRelic __instance, DamageInfo info, int damageAmount) {
            if (__instance instanceof TinyHouse && !BossedRelics.isDisabled(TinyHouse.ID))
                return SpireReturn.Return(Math.max(0, damageAmount - 1));
            return SpireReturn.Continue();
        }
    }

}
