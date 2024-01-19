package th;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TransformAndUpgradeAction extends AbstractGameAction {

    private static final float DURATION = Settings.ACTION_DUR_XFAST;
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("TH:TransformAndUpgradeAction").TEXT;

    public TransformAndUpgradeAction() {
        this.duration = DURATION;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        // select a card
        if (this.duration == DURATION) {
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || p.hand.isEmpty()) {
                this.isDone = true;
            } else {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false);
                AbstractDungeon.player.hand.applyPowers();
                this.tickDuration();
            }
            return;
        }
        // transform and upgrade
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard oldCard : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractCard newCard = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
                newCard.upgrade();
                this.addToBot(new MakeTempCardInHandAction(newCard, 1));
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        this.tickDuration();
    }

}
