package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Astrolabe;

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

    /*
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfCombatPreDrawLogic",
            paramtypez = {}
    )
    public static class ApplyStartOfCombatPreDrawLogic {
        static final int COUNT = 3;
        public static void Postfix(AbstractPlayer __instance) {
            AbstractRelic relic = __instance.getRelic(Astrolabe.ID);
            if ( relic != null ) {
                relic.flash();
                // find candidates - preferably ones that can both be upgraded and cost-reduced
                ArrayList<AbstractCard> candidates = new ArrayList<AbstractCard>();
                for ( AbstractCard card : __instance.drawPile.group )
                    if ( card.cost > 0 && card.canUpgrade() )
                        candidates.add(card);
                if ( candidates.size() < COUNT ) {
                    for ( AbstractCard card : __instance.drawPile.group )
                        if ( card.cost > 0 && !card.canUpgrade() )
                            candidates.add(card);
                }
                if ( candidates.size() < COUNT ) {
                    for ( AbstractCard card : __instance.drawPile.group )
                        if ( card.cost <= 0 && card.canUpgrade() )
                            candidates.add(card);
                }
                // reduce cost of up to 3 cards to 0
                for ( int i = 0; i < COUNT && candidates.size() > 0; i++ ) {
                    int pick = AbstractDungeon.cardRandomRng.random(0, candidates.size() - 1);
                    AbstractCard card = candidates.remove(pick);
                    card.upgrade();
                    card.modifyCostForCombat(-1);
                }
            }
        }
    }
    */

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStartPostDraw",
            paramtypez = {}
    )
    public static class AtTurnStartPostDraw {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof Astrolabe) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new SuperTransformAction());
            }
        }
    }

}
