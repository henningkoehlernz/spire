package bossed;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.CallingBell;

public class BossedCallingBell {

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onCardDraw",
            paramtypez = {AbstractCard.class}
    )
    public static class OnCardDraw {
        public static void Postfix(AbstractRelic __instance, AbstractCard c) {
            if (__instance instanceof CallingBell && !BossedRelics.isDisabled(CallingBell.ID) && c.type == AbstractCard.CardType.CURSE) {
                __instance.flash();
                CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(3, true),
                        DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.POISON));
            }
        }
    }

}
