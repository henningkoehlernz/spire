package icr;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import basemod.helpers.BaseModCardTags;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventPatch {
    private static final Logger logger = LogManager.getLogger(EventPatch.class.getName());

    @SpirePatch(
            clz = BackToBasics.class,
            method = "upgradeStrikeAndDefends",
            paramtypez = {}
    )
    public static class UpgradeStrikeAndDefends {
        public static void Prefix(BackToBasics __instance) {
            AbstractPlayer p = AbstractDungeon.player;
            for ( AbstractCard c : p.masterDeck.group ) {
                if ( (c.hasTag(BaseModCardTags.BASIC_STRIKE) || c.hasTag(BaseModCardTags.BASIC_DEFEND)) && c.canUpgrade() ) {
                    c.upgrade();
                    p.bottledCardUpgradeCheck(c);
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(),
                            MathUtils.random(0.1F, 0.9F) * Settings.WIDTH,
                            MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT));
                    // BackToBasics.cardsUpgraded is private - circumvent via reflection
                    try {
                        Field cardsUpgradedField = BackToBasics.class.getDeclaredField("cardsUpgraded");
                        cardsUpgradedField.setAccessible(true);
                        List<String> cardsUpgraded = (List<String>)cardsUpgradedField.get(__instance);
                        cardsUpgraded.add(c.cardID);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            logger.info("upgraded basic strike and defend cards");
        }
    }

}