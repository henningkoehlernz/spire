package icr;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.characters.TheSilent;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.helpers.BaseModCardTags;

import java.lang.reflect.Field;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NeowPatch {
    private static final Logger logger = LogManager.getLogger(NeowPatch.class.getName());
    // for flagging cards as basic in a persistent way (flags won't save)
    private static final int BASIC_STRIKE = 123456;
    private static final int BASIC_DEFEND = 123457;

    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_RAGER;
    @SpireEnum
    public static NeowReward.NeowRewardType SILENT_POISONER;
    @SpireEnum
    public static NeowReward.NeowRewardType DEFECT_WARDEN;
    @SpireEnum
    public static NeowReward.NeowRewardType WATCHER_MONK;


    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_BERSERKER;
    @SpireEnum
    public static NeowReward.NeowRewardType SILENT_ASSASSIN;
    @SpireEnum
    public static NeowReward.NeowRewardType DEFECT_STORMLORD;
    @SpireEnum
    public static NeowReward.NeowRewardType WATCHER_SHAOLIN;

    @SpirePatch(
            clz = NeowEvent.class,
            method = "miniBlessing",
            paramtypez = {}
    )
    public static class MiniBlessing {
        public static void Postfix(NeowEvent __instance) {
            NeowReward newReward = new NeowReward(false);
            switch ( AbstractDungeon.player.chosenClass )
            {
                case IRONCLAD:
                    newReward.type = IRONCLAD_RAGER;
                    newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("ICR:Ironclad").TEXT[0];
                    break;
                case THE_SILENT:
                    newReward.type = SILENT_POISONER;
                    newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("ICR:Silent").TEXT[0];
                    break;
                case DEFECT:
                    newReward.type = DEFECT_WARDEN;
                    newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("ICR:Defect").TEXT[0];
                    break;
                case WATCHER:
                    newReward.type = WATCHER_MONK;
                    newReward.optionLabel = CardCrawlGame.languagePack.getCharacterString("ICR:Watcher").TEXT[0];
                    break;
                default:
                    return;
            }
            // NeowEvent.reward is private - circumvent via reflection
            try {
                Field rewardField = NeowEvent.class.getDeclaredField("rewards");
                rewardField.setAccessible(true);
                ArrayList<NeowReward> rewards = (ArrayList<NeowReward>)rewardField.get(__instance);
                rewards.add(newReward);
            } catch (Exception e) {
                System.out.println(e);
            }
            String prefix = CardCrawlGame.languagePack.getCharacterString("ICR:NeowReward").TEXT[0];
            __instance.roomEventText.addDialogOption("[ " + prefix + newReward.optionLabel + " ]");
            logger.info("added " + newReward.optionLabel + " mini blessing");
        }
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "getRewardOptions",
            paramtypez = {int.class}
    )
    public static class Blessing {
        public static ArrayList<NeowReward.NeowRewardDef> Postfix(ArrayList<NeowReward.NeowRewardDef> __result, NeowReward __instance, int category) {
            if ( category == 3 ) {
                NeowReward.NeowRewardDef newReward = null;
                switch (AbstractDungeon.player.chosenClass) {
                    case IRONCLAD:
                        newReward = new NeowReward.NeowRewardDef(IRONCLAD_BERSERKER, CardCrawlGame.languagePack.getCharacterString("ICR:Ironclad").TEXT[1]);
                        break;
                    case THE_SILENT:
                        newReward = new NeowReward.NeowRewardDef(SILENT_ASSASSIN, CardCrawlGame.languagePack.getCharacterString("ICR:Silent").TEXT[1]);
                        break;
                    case DEFECT:
                        newReward = new NeowReward.NeowRewardDef(DEFECT_STORMLORD, CardCrawlGame.languagePack.getCharacterString("ICR:Defect").TEXT[1]);
                        break;
                    case WATCHER:
                        newReward = new NeowReward.NeowRewardDef(WATCHER_SHAOLIN, CardCrawlGame.languagePack.getCharacterString("ICR:Watcher").TEXT[1]);
                        break;
                    default:
                        return __result;
                }
                if ( IroncladRager.guaranteeSubclass ) {
                    __result.clear();
                    logger.info("removed existing blessings");
                }
                logger.info("added " + newReward.desc + " blessing");
                String prefix = CardCrawlGame.languagePack.getCharacterString("ICR:NeowReward").TEXT[1];
                newReward.desc = "[ " + prefix + newReward.desc + " ]";
                __result.add(newReward);
            }
            return __result;
        }
    }

    // helper functions
    public static void giveCard(AbstractCard card) {
        card.rarity = AbstractCard.CardRarity.BASIC;
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }
    public static void giveBasicStrike(AbstractCard card) {
        card.tags.add(BaseModCardTags.BASIC_STRIKE);
        card.misc = BASIC_STRIKE;
        giveCard(card);
    }
    public static void giveBasicDefend(AbstractCard card) {
        card.tags.add(BaseModCardTags.BASIC_DEFEND);
        card.misc = BASIC_DEFEND;
        giveCard(card);
    }

    // player title is set in constructor and not updated automatically
    public static void fixPlayerTitle() {
        AbstractPlayer p = AbstractDungeon.player;
        p.title = p.getTitle(p.chosenClass);
        AbstractDungeon.topPanel.setPlayerName();
        logger.info("fixed title: '" + CardCrawlGame.metricData.neowBonus + "' -> '" + p.title + "'");
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "activate",
            paramtypez = {}
    )
    public static class ActivatePatch {
        public static void Prefix(NeowReward __instance) {
            // replace old strikes & defends
            if ( __instance.type == IRONCLAD_RAGER
                || __instance.type == SILENT_POISONER
                || __instance.type == DEFECT_WARDEN
                || __instance.type == WATCHER_MONK ) {
                Iterator<AbstractCard> it = AbstractDungeon.player.masterDeck.group.iterator();
                while (it.hasNext()) {
                    AbstractCard e = it.next();
                    if ( e instanceof com.megacrit.cardcrawl.cards.red.Strike_Red ) {
                        it.remove();
                        giveCard(new RageStrike());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.red.Defend_Red ) {
                        it.remove();
                        giveCard(new ShieldBash());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.green.Strike_Green ) {
                        it.remove();
                        giveCard(new VenomStrike());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.green.Defend_Green ) {
                        it.remove();
                        giveCard(new Dodge());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.blue.Strike_Blue ) {
                        it.remove();
                        giveCard(new ProbingStrike());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.blue.Defend_Blue ) {
                        it.remove();
                        giveCard(new AutoDefend());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.purple.Strike_Purple ) {
                        it.remove();
                        giveCard(new DragonStrike());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.purple.Defend_Watcher ) {
                        it.remove();
                        giveCard(new Dodge());
                    }
                }
                logger.debug("replaced basic strike & defend cards");
            }
            // dito for "advanced" classes
            if ( __instance.type == IRONCLAD_BERSERKER
                    || __instance.type == SILENT_ASSASSIN
                    || __instance.type == DEFECT_STORMLORD
                    || __instance.type == WATCHER_SHAOLIN ) {
                Iterator<AbstractCard> it = AbstractDungeon.player.masterDeck.group.iterator();
                boolean frost = false; // alternate Cold Snap & Ball Lightning for Stormlord
                while (it.hasNext()) {
                    AbstractCard e = it.next();
                    if ( e instanceof com.megacrit.cardcrawl.cards.red.Strike_Red ) {
                        it.remove();
                        giveBasicStrike(new com.megacrit.cardcrawl.cards.red.TwinStrike());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.red.Defend_Red ) {
                        it.remove();
                        giveBasicDefend(new com.megacrit.cardcrawl.cards.red.IronWave());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.green.Strike_Green ) {
                        it.remove();
                        giveBasicStrike(new com.megacrit.cardcrawl.cards.green.PoisonedStab());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.green.Defend_Green ) {
                        it.remove();
                        giveBasicDefend(new com.megacrit.cardcrawl.cards.green.DodgeAndRoll());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.blue.Strike_Blue ) {
                        it.remove();
                        if ( frost )
                            giveBasicStrike(new com.megacrit.cardcrawl.cards.blue.ColdSnap());
                        else
                            giveBasicStrike(new com.megacrit.cardcrawl.cards.blue.BallLightning());
                        frost = !frost;
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.blue.Defend_Blue ) {
                        it.remove();
                        giveBasicDefend(new com.megacrit.cardcrawl.cards.blue.Leap());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.purple.Strike_Purple ) {
                        it.remove();
                        giveBasicStrike(new com.megacrit.cardcrawl.cards.purple.FollowUp());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.purple.Defend_Watcher ) {
                        it.remove();
                        giveBasicDefend(new com.megacrit.cardcrawl.cards.purple.ThirdEye());
                    }
                }
                logger.debug("replaced basic strike & defend cards (advanced)");
                // apply ALL drawbacks
                AbstractPlayer p = AbstractDungeon.player;
                __instance.drawback = NeowReward.NeowRewardDrawback.CURSE; // cursed
                p.loseGold(p.gold);
                p.decreaseMaxHealth(p.maxHealth / 10);
                p.damage(new DamageInfo(null, p.currentHealth / 10 * 3, DamageInfo.DamageType.HP_LOSS));
                logger.debug("applied drawbacks");
            }
        }
        public static void Postfix(NeowReward __instance) {
            fixPlayerTitle();
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "loadPlayerSave",
            paramtypez = {AbstractPlayer.class}
    )
    public static class LoadPlayerSave {
        public static void Postfix(CardCrawlGame __instance, AbstractPlayer p) {
            logger.info("restoring card tags");
            // fix BASIC_STRIKE and BASIC_DEFEND tags
            for ( AbstractCard c : p.masterDeck.group ) {
                switch (c.misc) {
                    case BASIC_STRIKE:
                        c.tags.add(BaseModCardTags.BASIC_STRIKE);
                        c.rarity = AbstractCard.CardRarity.BASIC;
                        logger.debug("tagged " + c.name + " as BASIC_STRIKE");
                        break;
                    case BASIC_DEFEND:
                        c.tags.add(BaseModCardTags.BASIC_DEFEND);
                        c.rarity = AbstractCard.CardRarity.BASIC;
                        logger.debug("tagged " + c.name + " as BASIC_DEFEND");
                        break;
                }
            }
            fixPlayerTitle();
        }
    }

    // change class names to subclass names

    @SpirePatch(
            clz = Ironclad.class,
            method = "getTitle",
            paramtypez = {AbstractPlayer.PlayerClass.class}
    )
    public static class IroncladTitle {
        public static String Postfix(String __result, Ironclad __instance, AbstractPlayer.PlayerClass pc) {
            if ( CardCrawlGame.metricData.neowBonus.equals(IRONCLAD_RAGER.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Ironclad").NAMES[0];
            if ( CardCrawlGame.metricData.neowBonus.equals(IRONCLAD_BERSERKER.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Ironclad").NAMES[1];
            return __result;
        }
    }

    @SpirePatch(
            clz = TheSilent.class,
            method = "getTitle",
            paramtypez = {AbstractPlayer.PlayerClass.class}
    )
    public static class TheSilentTitle {
        public static String Postfix(String __result, TheSilent __instance, AbstractPlayer.PlayerClass pc) {
            if ( CardCrawlGame.metricData.neowBonus.equals(SILENT_POISONER.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Silent").NAMES[0];
            if ( CardCrawlGame.metricData.neowBonus.equals(SILENT_ASSASSIN.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Silent").NAMES[1];
            return __result;
        }
    }

    @SpirePatch(
            clz = Defect.class,
            method = "getTitle",
            paramtypez = {AbstractPlayer.PlayerClass.class}
    )
    public static class DefectTitle {
        public static String Postfix(String __result, Defect __instance, AbstractPlayer.PlayerClass pc) {
            if ( CardCrawlGame.metricData.neowBonus.equals(DEFECT_WARDEN.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Defect").NAMES[0];
            if ( CardCrawlGame.metricData.neowBonus.equals(DEFECT_STORMLORD.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Defect").NAMES[1];
            return __result;
        }
    }

    @SpirePatch(
            clz = Watcher.class,
            method = "getTitle",
            paramtypez = {AbstractPlayer.PlayerClass.class}
    )
    public static class WatcherTitle {
        public static String Postfix(String __result, Watcher __instance, AbstractPlayer.PlayerClass pc) {
            if ( CardCrawlGame.metricData.neowBonus.equals(WATCHER_MONK.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Watcher").NAMES[0];
            if ( CardCrawlGame.metricData.neowBonus.equals(WATCHER_SHAOLIN.name()) )
                return CardCrawlGame.languagePack.getCharacterString("ICR:Watcher").NAMES[1];
            return __result;
        }
    }

}