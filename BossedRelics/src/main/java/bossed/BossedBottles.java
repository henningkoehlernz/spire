package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.*;

public class BossedBottles {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyPreCombatLogic",
            paramtypez = {}
    )
    public static class ApplyPreCombatLogic {
        public static void Postfix(AbstractPlayer __instance) {
            for ( AbstractCard card : __instance.drawPile.group )
                if ( card.inBottleFlame && !BossedRelics.isDisabled(BottledFlame.ID)
                        || card.inBottleLightning && !BossedRelics.isDisabled(BottledLightning.ID)
                        || card.inBottleTornado && !BossedRelics.isDisabled(BottledTornado.ID) )
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
            if ( __instance instanceof BottledFlame && !BossedRelics.isDisabled(BottledFlame.ID)
                    || __instance instanceof BottledLightning && !BossedRelics.isDisabled(BottledLightning.ID)
                    || __instance instanceof BottledTornado && !BossedRelics.isDisabled(BottledTornado.ID) ) {
                AbstractCard card = __instance instanceof BottledFlame ? ((BottledFlame)__instance).card
                        : __instance instanceof BottledLightning ? ((BottledLightning)__instance).card
                        : ((BottledTornado)__instance).card;
                if ( AbstractDungeon.player.masterDeck.getSpecificCard(card) == null )
                    __instance.onEquip();
            }
        }
    }

}
