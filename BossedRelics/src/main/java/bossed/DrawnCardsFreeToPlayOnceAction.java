package bossed;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class DrawnCardsFreeToPlayOnceAction extends AbstractGameAction {

    @Override
    public void update() {
        for ( AbstractCard card : DrawCardAction.drawnCards )
            card.freeToPlayOnce = true;
        this.isDone = true;
    }

}
