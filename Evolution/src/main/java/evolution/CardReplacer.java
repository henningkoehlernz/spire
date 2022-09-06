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
        for (AbstractCard card : AbstractDungeon.commonCardPool.group) {
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

    private static boolean strikeTagUsed(ArrayList<AbstractCard> cards) {
        for ( AbstractCard card : cards ) {
            if (card.isStarterStrike() || card.hasTag(AbstractCard.CardTags.STARTER_STRIKE))
                return true;
        }
        return false;
    }

    private static boolean defendTagUsed(ArrayList<AbstractCard> cards) {
        for ( AbstractCard card : cards ) {
            if (card.isStarterDefend())
                return true;
        }
        return false;
    }

    // returns number of cards replaced
    public static int replaceBasicStrikes(int maxReplace) {
        int variety = Evolution.getVariety() > 2 ? 99 : Evolution.getVariety();
        ArrayList<AbstractCard> strikeReplacements = getReplacements(true, variety);
        ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
        String basicStrikeID = strikeTagUsed(cards) ? null : getBasicStrikeID(cards);
        int next = 0, strikeReplaced = 0;
        while ( next < cards.size() && strikeReplaced < maxReplace ) {
            AbstractCard card = cards.get(next);
            // isStarterStrike checks for STRIKE, not STARTER_STRIKE
            if ( (card.isStarterStrike() || card.hasTag(AbstractCard.CardTags.STARTER_STRIKE) || card.cardID.equals(basicStrikeID))
                    && !strikeReplacements.isEmpty() ) {
                cards.set(next, strikeReplacements.get(strikeReplaced % strikeReplacements.size()).makeCopy());
                strikeReplaced++;
            } else if ( Loader.DEBUG )
                logger.info("replaceBasicStrikes: skipping " + card.cardID);
            next++;
        }
        return strikeReplaced;
    }

    // returns number of cards replaced
    public static int replaceBasicDefends(int maxReplace) {
        int variety = Evolution.getVariety() > 2 ? 99 : Evolution.getVariety();
        ArrayList<AbstractCard> defendReplacements = getReplacements(false, variety);
        ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
        String basicDefendID = defendTagUsed(cards) ? null : getBasicDefendID(cards);
        int next = 0, defendReplaced = 0;
        while ( next < cards.size() && defendReplaced < maxReplace ) {
            AbstractCard card = cards.get(next);
            if ( (card.isStarterDefend() || card.cardID.equals(basicDefendID)) && !defendReplacements.isEmpty() ) {
                cards.set(next, defendReplacements.get(defendReplaced % defendReplacements.size()).makeCopy());
                defendReplaced++;
            } else if ( Loader.DEBUG )
                logger.info("replaceBasicDefends: skipping " + card.cardID);
            next++;
        }
        return defendReplaced;
    }

}
