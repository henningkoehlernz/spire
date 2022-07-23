package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Orrery;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;

public class BossedOrrery {

    public static void upgradeRandomCard() {
        ArrayList<AbstractCard> upgradableCards = new ArrayList();
        for ( AbstractCard card : AbstractDungeon.player.masterDeck.group ) {
            if ( card.canUpgrade() )
                upgradableCards.add(card);
        }
        if ( upgradableCards.size() > 0 ) {
            AbstractCard card = upgradableCards.get(AbstractDungeon.miscRng.random(upgradableCards.size() - 1));
            card.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(card);
            // must add to queue as card upgrade might trigger while iterating over topLevelEffects
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        }
    }

    @SpirePatch(
        clz = Orrery.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {}
    )
    public static class Constructor {
        public static void Postfix(Orrery __instance) {
            if (!BossedRelics.isDisabled(Orrery.ID))
                __instance.counter = 0;
        }
    }

    @SpirePatch(
        clz = AbstractRelic.class,
        method = "onObtainCard",
        paramtypez = {AbstractCard.class}
    )
    public static class OnObtainCard {
        public static void Postfix(AbstractRelic __instance, AbstractCard c) {
            if (__instance instanceof Orrery && !BossedRelics.isDisabled(Orrery.ID)) {
                __instance.counter += 1;
                if ( __instance.counter >= 5 ) {
                    __instance.counter = 0;
                    __instance.flash();
                    upgradeRandomCard();
                }
            }
        }
    }

}
