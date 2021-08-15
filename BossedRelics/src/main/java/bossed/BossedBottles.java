package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;

public class BossedBottles {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfCombatPreDrawLogic",
            paramtypez = {}
    )
    public static class ApplyStartOfCombatPreDrawLogic {
        public static void Postfix(AbstractPlayer __instance) {
            for ( AbstractCard card : __instance.drawPile.group )
                if ( card.inBottleFlame || card.inBottleLightning || card.inBottleTornado )
                    card.setCostForTurn(card.costForTurn - 1);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onEnterRestRoom",
            paramtypez = {}
    )
    public static class OnEnterRestRoom {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof BottledFlame || __instance instanceof BottledLightning || __instance instanceof BottledTornado ) {
                AbstractCard card = __instance instanceof BottledFlame ? ((BottledFlame)__instance).card
                        : __instance instanceof BottledLightning ? ((BottledLightning)__instance).card
                        : ((BottledTornado)__instance).card;
                if ( AbstractDungeon.player.masterDeck.getSpecificCard(card) == null )
                    __instance.onEquip();
            }
        }
    }

}
