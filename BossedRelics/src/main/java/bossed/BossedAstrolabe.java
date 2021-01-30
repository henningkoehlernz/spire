package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Astrolabe;

import java.util.ArrayList;
import java.util.Iterator;

public class BossedAstrolabe {

    @SpirePatch(
            clz = Astrolabe.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Replace(Astrolabe __instance) {
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfCombatPreDrawLogic",
            paramtypez = {}
    )
    public static class ApplyStartOfCombatPreDrawLogic {
        public static void Postfix(AbstractPlayer __instance) {
            AbstractRelic relic = __instance.getRelic(Astrolabe.ID);
            if ( relic != null ) {
                relic.flash();
                // find candidates
                ArrayList<AbstractCard> candidates = new ArrayList();
                for ( AbstractCard card : __instance.drawPile.group )
                    if ( card.cost > 0 || card.canUpgrade() ) {
                        candidates.add(card);
                    }
                // reduce cost of up to 3 cards to 0
                for ( int i = 0; i < 3 && candidates.size() > 0; i++ ) {
                    int pick = AbstractDungeon.cardRandomRng.random(0, candidates.size() - 1);
                    AbstractCard card = candidates.remove(pick);
                    card.modifyCostForCombat(-1);
                    card.upgrade();
                }
            }
        }
    }

}
