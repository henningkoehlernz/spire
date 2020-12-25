package evo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CardReplacer {
    private static final Logger logger = LogManager.getLogger(CardReplacer.class.getName());

    private static boolean isStrikeLike(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0
                && card.cost == 1 && !card.exhaust;
    }

    private static boolean isDefendLike(AbstractCard card) {
        return card.type == AbstractCard.CardType.SKILL && card.baseBlock > 0
                && card.cost == 1 && !card.exhaust;
    }

    private static AbstractCard getReplacement(boolean strike) {
        ArrayList<AbstractCard> candidates = new ArrayList<AbstractCard>();
        Iterator<AbstractCard> it = AbstractDungeon.commonCardPool.group.iterator();
        while ( it.hasNext() ) {
            AbstractCard card = it.next();
            if ( strike ? isStrikeLike(card) : isDefendLike(card) )
                candidates.add(card);
        }
        return candidates.isEmpty() ? null : candidates.get(AbstractDungeon.cardRng.random(candidates.size() - 1));
    }

    // replace strikes & defends
    // returns number of cards replaced
    public static int replaceBasicCards(int maxReplace) {
        AbstractCard strikeReplacement = getReplacement(true);
        AbstractCard defendReplacement = getReplacement(false);
        ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
        int next = 0, replaced = 0;
        while ( next < cards.size() && replaced < maxReplace ) {
            AbstractCard card = cards.get(next);
            if ( card.isStarterStrike() && strikeReplacement != null ) {
                cards.set(next, strikeReplacement.makeCopy());
                replaced++;
            } else if ( card.isStarterDefend() && defendReplacement != null ) {
                cards.set(next, defendReplacement.makeCopy());
                replaced++;
            }
            next++;
        }
        logger.debug("replaced basic strike & defend cards");
        return replaced;
    }

}
