package evolution;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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

    private static ArrayList<AbstractCard> getReplacements(boolean strike, int max) {
        ArrayList<AbstractCard> candidates = new ArrayList<AbstractCard>();
        Iterator<AbstractCard> it = AbstractDungeon.commonCardPool.group.iterator();
        while ( it.hasNext() ) {
            AbstractCard card = it.next();
            if ( strike ? isStrikeLike(card) : isDefendLike(card) )
                candidates.add(card);
        }
        Collections.shuffle(candidates);
        while ( candidates.size() > max )
            candidates.remove(candidates.size() - 1);
        return candidates;
    }

    // returns cardID of the "basic strike/defend" card in the given deck
    // used for mods that don't tag their cards right

    private static String getBasicStrikeID(ArrayList<AbstractCard> cards) {
        for ( AbstractCard card : cards ) {
            if ( isStrikeLike(card) )
                return card.cardID;
        }
        return null;
    }

    private static String getBasicDefendID(ArrayList<AbstractCard> cards) {
        for ( AbstractCard card : cards ) {
            if ( isDefendLike(card) )
                return card.cardID;
        }
        return null;
    }

    // replace strikes & defends
    // returns number of cards replaced
    public static int replaceBasicCards(int maxReplace, boolean strictTags) {
        int variety = Evolution.getVariety() > 2 ? 99 : Evolution.getVariety();
        ArrayList<AbstractCard> strikeReplacements = getReplacements(true, variety);
        ArrayList<AbstractCard> defendReplacements = getReplacements(false, variety);
        ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
        String basicStrikeID = strictTags ? null : getBasicStrikeID(cards);
        String basicDefendID = strictTags ? null : getBasicDefendID(cards);
        int next = 0, strikeReplaced = 0, defendReplaced = 0;
        while ( next < cards.size() && strikeReplaced + defendReplaced < maxReplace ) {
            AbstractCard card = cards.get(next);
            // isStarterStrike checks for STRIKE, not STARTER_STRIKE
            if ( (card.isStarterStrike() || card.hasTag(AbstractCard.CardTags.STARTER_STRIKE) || card.cardID.equals(basicStrikeID))
                    && !strikeReplacements.isEmpty() ) {
                cards.set(next, strikeReplacements.get(strikeReplaced % strikeReplacements.size()).makeCopy());
                strikeReplaced++;
            } else if ( (card.isStarterDefend() || card.cardID.equals(basicDefendID)) && !defendReplacements.isEmpty()  ) {
                cards.set(next, defendReplacements.get(defendReplaced % defendReplacements.size()).makeCopy());
                defendReplaced++;
            } else if ( Loader.DEBUG )
                logger.info("skipping " + card.cardID);
            next++;
        }
        return strikeReplaced + defendReplaced;
    }

}
