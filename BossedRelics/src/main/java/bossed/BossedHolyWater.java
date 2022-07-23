package bossed;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HolyWater;

public class BossedHolyWater {

    @SpirePatch(
            clz = Miracle.class,
            method = "use",
            paramtypez = {AbstractPlayer.class, AbstractMonster.class}
    )
    public static class UseMiracle {
        public static void Postfix(Miracle __instance, AbstractPlayer p, AbstractMonster m) {
            AbstractRelic relic = p.getRelic(HolyWater.ID);
            if (relic != null && !BossedRelics.isDisabled(HolyWater.ID)) {
                relic.flash();
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
            }
        }
    }

}
