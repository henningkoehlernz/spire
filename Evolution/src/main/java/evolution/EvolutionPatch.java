package evolution;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EvolutionPatch {
    private static final Logger logger = LogManager.getLogger(EvolutionPatch.class.getName());

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition",
            paramtypez = {SaveFile.class}
    )
    public static class NextRoomTransition {
        public static void Postfix(AbstractDungeon __instance, SaveFile saveFile) {
            if ( AbstractDungeon.floorNum <= 1 ) {
                int ep = Evolution.getEvolutionTotal(AbstractDungeon.player.chosenClass, AbstractDungeon.ascensionLevel);
                AbstractRelic relic = new Axolotl();
                relic.counter = ep;
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                        (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                replaceBasicCards(ep);
                logger.info("added evolution relic");
            }
        }
    }

    // helper functions
    public static void giveCard(AbstractCard card) {
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }

    public static boolean isStrikeLike(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0
                && card.cost == 1 && !card.exhaust;
    }

    public static boolean isDefendLike(AbstractCard card) {
        return card.type == AbstractCard.CardType.SKILL && card.baseBlock > 0
                && card.cost == 1 && !card.exhaust;
    }

    public static AbstractCard getReplacement(boolean strike) {
        ArrayList<AbstractCard> candidates = new ArrayList<AbstractCard>();
        Iterator<AbstractCard> it = AbstractDungeon.commonCardPool.group.iterator();
        while ( it.hasNext() ) {
            AbstractCard card = it.next();
            if ( strike ? isStrikeLike(card) : isDefendLike(card) )
                candidates.add(card);
        }
        return candidates.isEmpty() ? null : candidates.get(AbstractDungeon.cardRng.random(candidates.size()));
    }

    // replace strikes & defends
    public static void replaceBasicCards(int maxReplace) {
        AbstractCard strikeReplacement = getReplacement(true);
        AbstractCard defendReplacement = getReplacement(false);
        Iterator<AbstractCard> it = AbstractDungeon.player.masterDeck.group.iterator();
        int replaced = 0;
        while ( it.hasNext() && replaced < maxReplace ) {
            AbstractCard card = it.next();
            if ( card.isStarterStrike() && strikeReplacement != null ) {
                it.remove();
                giveCard(strikeReplacement.makeCopy());
                replaced++;
            } else if ( card.isStarterDefend() && defendReplacement != null ) {
                it.remove();
                giveCard(defendReplacement.makeCopy());
                replaced++;
            }
        }
        logger.debug("replaced basic strike & defend cards");
    }

}
