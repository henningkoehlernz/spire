package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DollysMirror;

public class BossedDollysMirror {

    @SpirePatch(
            clz = DollysMirror.class,
            method = "onEquip",
            paramtypez = {}
    )
    public static class OnEquip {
        public static void Replace(DollysMirror __instance) {
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onVictory",
            paramtypez = {}
    )
    public static class OnVictory {
        public static void Postfix(AbstractRelic __instance) {
            if ( __instance instanceof DollysMirror )
                __instance.grayscale = false;
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onPlayCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class}
    )
    public static class OnPlayCard {
        public static void Postfix(AbstractRelic __instance, AbstractCard c, AbstractMonster m) {
            if ( __instance instanceof DollysMirror && !__instance.grayscale ) {
                __instance.flash();
                __instance.grayscale = true;
                AbstractCard copy = c.makeSameInstanceOf();
                AbstractDungeon.player.limbo.addToBottom(copy);
                copy.current_x = c.current_x;
                copy.current_y = c.current_y;
                copy.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                copy.target_y = Settings.HEIGHT / 2.0F;
                if ( m != null )
                    copy.calculateCardDamage(m);
                copy.purgeOnUse = true;
                AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(copy, m, c.energyOnUse, true, true), true);
            }
        }
    }

}
