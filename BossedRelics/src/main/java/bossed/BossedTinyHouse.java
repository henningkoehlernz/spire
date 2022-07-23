package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.PotionSlot;
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

    @SpirePatch(
            clz = DamageInfo.class,
            method = "applyPowers",
            paramtypez = {AbstractCreature.class, AbstractCreature.class}
    )
    public static class ApplyPowers {
        public static void Postfix(DamageInfo __instance, AbstractCreature __owner, AbstractCreature target) {
            if (target != AbstractDungeon.player)
                return;
            if (!BossedRelics.isDisabled(TinyHouse.ID) && AbstractDungeon.player.hasRelic(TinyHouse.ID)) {
                if (__instance.output > 0) {
                    __instance.output -= 1;
                    __instance.isModified = true;
                }
            }
        }
    }

}
