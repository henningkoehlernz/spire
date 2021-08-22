package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BossedOmamori {

    @SpirePatch(
        clz = AbstractRelic.class,
        method = "atBattleStart",
        paramtypez = {}
    )
    public static class AtBattleStart {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof Omamori )
                __instance.grayscale = false;
        }
    }

    @SpirePatch(
        clz = Omamori.class,
        method = "setCounter",
        paramtypez = {int.class}
    )
    public static class SetCounter {
        public static void Replace(Omamori __instance, int setCounter) {
            __instance.counter = setCounter;
            if (setCounter == 0) {
                __instance.description = __instance.DESCRIPTIONS[0];
            } else if (setCounter == 1) {
                __instance.description = __instance.DESCRIPTIONS[1];
            }
        }
    }

    @SpirePatch(
        clz = AbstractRelic.class,
        method = "onCardDraw",
        paramtypez = {AbstractCard.class}
    )
    public static class OnCardDraw {
        public static void Postfix(AbstractRelic __instance, AbstractCard drawnCard) {
            if ( __instance instanceof Omamori && !__instance.grayscale && drawnCard.type == AbstractCard.CardType.CURSE ) {
                __instance.grayscale = true;
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(drawnCard, AbstractDungeon.player.hand));
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onExhaust",
            paramtypez = {AbstractCard.class}
    )
    public static class OnExhaust {
        public static void Postfix(AbstractRelic __instance, AbstractCard card) {
            if ( __instance instanceof Omamori && card.type == AbstractCard.CardType.CURSE ) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
            }
        }
    }

    // disable old effect negating cards obtained

    @SpirePatch(
            clz = Omamori.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {}
    )
    public static class Constructor {
        public static void Postfix(Omamori __instance) {
            __instance.counter = -1;
        }
    }

    @SpirePatch(
            clz = Omamori.class,
            method = "use",
            paramtypez = {}
    )
    public static class Use {
        public static void Replace(Omamori __instance) { }
    }

    @SpirePatch(
        clz = FastCardObtainEffect.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCard.class, float.class, float.class}
    )
    public static class ConstructorFastCardObtainEffect {
        public static void Postfix(FastCardObtainEffect __instance, AbstractCard card, float x, float y) {
            if ( card.type == AbstractCard.CardType.CURSE )
                __instance.isDone = false;
        }
    }

    @SpirePatch(
            clz = ShowCardAndObtainEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class, boolean.class}
    )
    public static class ConstructorShowCardAndObtainEffect {
        public static void Postfix(ShowCardAndObtainEffect __instance, AbstractCard card, float x, float y, boolean convergeCards) {
            if ( card.type == AbstractCard.CardType.CURSE )
                __instance.isDone = false;
        }
    }

}
