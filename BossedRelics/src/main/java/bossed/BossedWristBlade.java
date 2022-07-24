package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WristBlade;

public class BossedWristBlade {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStart",
            paramtypez = {}
    )
    public static class AtTurnStart {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof WristBlade && !BossedRelics.isDisabled(WristBlade.ID))
                __instance.counter = 1;
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onPlayCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class}
    )
    public static class OnPlayCard {
        public static void Postfix(AbstractRelic __instance, AbstractCard c, AbstractMonster m) {
            if (__instance instanceof WristBlade && !BossedRelics.isDisabled(WristBlade.ID)) {
                boolean costZero = c.costForTurn == 0 || (c.freeToPlayOnce && c.cost != -1);
                if ( c.type == AbstractCard.CardType.ATTACK && costZero && __instance.counter > 0 ) {
                    __instance.counter--;
                    __instance.flash();
                    AbstractDungeon.actionManager.addToTop(new DrawCardAction(1));
                }
            }
        }
    }

}
