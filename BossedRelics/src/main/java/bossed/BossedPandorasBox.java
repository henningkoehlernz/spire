package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BossedPandorasBox {

    private static final Logger logger = LogManager.getLogger(BossedPandorasBox.class.getName());
    private static final String CALLED_TRANSFORM = "calledTransform";

    @SpirePatch(
            clz = PandorasBox.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static SpireReturn<Void> Prefix(PandorasBox __instance) {
            if (BossedRelics.isDisabled(PandorasBox.ID))
                return SpireReturn.Continue();
            Reflection.set(__instance, PandorasBox.class, CALLED_TRANSFORM, false);
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(),10, true, __instance.DESCRIPTIONS[1]);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "update",
            paramtypez = {}
    )
    public static class Update {

        // conditions ensure AbstractRelic.update is called, but other code in PandorasBox.update is not executed
        // needed since AbstractRelic.update triggers OnEquip, among other things
        public static void Prefix(PandorasBox __instance) {
            if (BossedRelics.isDisabled(PandorasBox.ID))
                return;
            boolean calledTransform = (Boolean)Reflection.get(__instance, PandorasBox.class, CALLED_TRANSFORM);
            if (!calledTransform && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
                Reflection.set(__instance, PandorasBox.class, CALLED_TRANSFORM, true);
                ArrayList<AbstractCard> selectedCards = AbstractDungeon.gridSelectScreen.selectedCards;
                __instance.counter = selectedCards.size();
                transformCards(selectedCards);
            }
        }

        private static void transformCards(ArrayList<AbstractCard> cards) {
            logger.info("transforming " + cards.size() + " cards");
            // copied from Astrolabe.giveCards
            float displayCount = 0.0F;
            for (AbstractCard card : cards) {
                card.untip();
                card.unhover();
                AbstractDungeon.player.masterDeck.removeCard(card);
                AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);
                if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && AbstractDungeon.transformedCard != null) {
                    AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(AbstractDungeon.getTransformedCard(), (float) Settings.WIDTH / 3.0F + displayCount, (float)Settings.HEIGHT / 2.0F, false));
                    displayCount += (float)Settings.WIDTH / 6.0F;
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "atTurnStart",
            paramtypez = {}
    )
    public static class AtTurnStart {
        public static void Prefix(AbstractRelic __instance) {
            if (__instance instanceof PandorasBox && !BossedRelics.isDisabled(PandorasBox.ID)) {
                //logger.info("turn = " + GameActionManager.turn);
                if (TurnCounter.turn >= __instance.counter) {
                    __instance.flash();
                    AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(target, false));
                }
            }
        }
    }

}
