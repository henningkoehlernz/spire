package bossed;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.TransformCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class SuperTransformAction extends AbstractGameAction {

    public static AbstractCard getCardOfRarity(ArrayList<AbstractCard> cards, AbstractCard.CardRarity rarity) {
        for (AbstractCard card : cards) {
            if (card.rarity == rarity)
                return card;
        }
        return null;
    }

    public static AbstractCard getMinRarityCard(ArrayList<AbstractCard> cards) {
        AbstractCard card = getCardOfRarity(cards, AbstractCard.CardRarity.BASIC);
        if (card == null)
            card = getCardOfRarity(cards, AbstractCard.CardRarity.COMMON);
        if (card == null)
            card = getCardOfRarity(cards, AbstractCard.CardRarity.UNCOMMON);
        if (card == null)
            card = getCardOfRarity(cards, AbstractCard.CardRarity.RARE);
        return card;
    }

    /*
    public static boolean rarityLessThan(AbstractCard.CardRarity a, AbstractCard.CardRarity b) {
        if (a == AbstractCard.CardRarity.BASIC)
            return b == AbstractCard.CardRarity.COMMON || b == AbstractCard.CardRarity.UNCOMMON || b == AbstractCard.CardRarity.RARE;
        if (a == AbstractCard.CardRarity.COMMON)
            return b == AbstractCard.CardRarity.UNCOMMON || b == AbstractCard.CardRarity.RARE;
        if (a == AbstractCard.CardRarity.UNCOMMON)
            return b == AbstractCard.CardRarity.RARE;
        return false;
    }

    public static AbstractCard randomCardOfHigherRarityInCombat(AbstractCard.CardRarity rarity) {
        ArrayList<AbstractCard> list = new ArrayList();
        if (rarityLessThan(rarity, AbstractCard.CardRarity.COMMON)) {
            for (AbstractCard c : AbstractDungeon.srcCommonCardPool.group) {
                if (!c.hasTag(AbstractCard.CardTags.HEALING))
                    list.add(c);
            }
        }
        if (rarityLessThan(rarity, AbstractCard.CardRarity.UNCOMMON)) {
            for (AbstractCard c : AbstractDungeon.srcUncommonCardPool.group) {
                if (!c.hasTag(AbstractCard.CardTags.HEALING))
                    list.add(c);
            }
        }
        for (AbstractCard c : AbstractDungeon.srcRareCardPool.group) {
            if (!c.hasTag(AbstractCard.CardTags.HEALING))
                list.add(c);
        }
        return (AbstractCard)list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }
    */

    @Override
    public void update() {
        AbstractCard oldCard = getMinRarityCard(AbstractDungeon.player.hand.group);
        if (oldCard != null) {
            AbstractCard newCard = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
            newCard.upgrade();
            // replace, preserving position
            int index = AbstractDungeon.player.hand.group.indexOf(oldCard);
            AbstractDungeon.actionManager.addToTop(new TransformCardInHandAction(index, newCard));
        }
        this.isDone = true;
    }

}
