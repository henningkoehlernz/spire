package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Whetstone;

import java.util.ArrayList;

public class BossedWhetstone {

    @SpirePatch(
            clz = Whetstone.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static SpireReturn<Void> Prefix(Whetstone __instance) {
            return BossedRelics.isDisabled(Whetstone.ID) ? SpireReturn.Continue() : SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfCombatPreDrawLogic",
            paramtypez = {}
    )
    public static class ApplyStartOfCombatPreDrawLogic {
        static final int COUNT = 2;
        public static void Postfix(AbstractPlayer __instance) {
            AbstractRelic relic = __instance.getRelic(Whetstone.ID);
            if (relic != null && !BossedRelics.isDisabled(Whetstone.ID)) {
                relic.flash();
                // find candidates
                ArrayList<AbstractCard> candidates = new ArrayList<AbstractCard>();
                for ( AbstractCard card : __instance.drawPile.group )
                    if ( card.canUpgrade() && card.type.equals(AbstractCard.CardType.ATTACK) )
                        candidates.add(card);
                // upgrade up to two cards
                for ( int i = 0; i < COUNT && candidates.size() > 0; i++ ) {
                    int pick = AbstractDungeon.cardRandomRng.random(0, candidates.size() - 1);
                    AbstractCard card = candidates.remove(pick);
                    card.upgrade();
                }
            }
        }
    }

}
