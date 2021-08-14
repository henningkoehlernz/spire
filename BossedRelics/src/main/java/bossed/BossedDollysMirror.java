package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DollysMirror;

public class BossedDollysMirror {

    static final int BLOCK = 3;
    static final int REPEATS = -1; // -1 means infinite repeats

    @SpirePatch(
            clz = DollysMirror.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Replace(DollysMirror __instance) {
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onVictory",
            paramtypez = {}
    )
    public static class OnVictory {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof DollysMirror )
                __instance.grayscale = false;
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onPlayCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class}
    )
    public static class OnPlayCard {
        public static void Postfix(AbstractRelic __instance, AbstractCard c, AbstractMonster m) {
            if ( __instance instanceof DollysMirror && !__instance.grayscale ) {
                __instance.flash();
                __instance.grayscale = true;
                AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
            }
        }
    }

}
