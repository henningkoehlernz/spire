import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.helpers.BaseModCardTags;

import java.lang.reflect.Field;
import java.util.*;

import icr.*;

public class NeowPatch {

    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_RAGER;
    @SpireEnum
    public static NeowReward.NeowRewardType SILENT_POISONER;
    @SpireEnum
    public static NeowReward.NeowRewardType DEFECT_WARDEN;

    @SpireEnum
    public static NeowReward.NeowRewardType IRONCLAD_BERSERKER;
    @SpireEnum
    public static NeowReward.NeowRewardType SILENT_ASSASSIN;
    @SpireEnum
    public static NeowReward.NeowRewardType DEFECT_STORMLORD;

    @SpirePatch(
            clz = NeowEvent.class,
            method = "miniBlessing",
            paramtypez = {}
    )
    public static class MiniBlessing {
        private static final String[] optionStrings = { "Rager", "Poisoner", "Warden" };
        public static void Postfix(NeowEvent __instance) {
            NeowReward newReward = new NeowReward(false);
            switch ( AbstractDungeon.player.chosenClass )
            {
                case IRONCLAD:
                    newReward.type = IRONCLAD_RAGER;
                    newReward.optionLabel = optionStrings[0];
                    break;
                case THE_SILENT:
                    newReward.type = SILENT_POISONER;
                    newReward.optionLabel = optionStrings[1];
                    break;
                case DEFECT:
                    newReward.type = DEFECT_WARDEN;
                    newReward.optionLabel = optionStrings[2];
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
            __instance.roomEventText.addDialogOption("[ #gPlay #gas #g" + newReward.optionLabel + " ]");
        }
    }

    @SpirePatch(
            clz = NeowReward.class,
            method = "getRewardOptions",
            paramtypez = {int.class}
    )
    public static class Blessing {
        private static final String[] optionStrings = { "Berserker", "Assassin", "Stormlord" };
        public static ArrayList<NeowReward.NeowRewardDef> Postfix(ArrayList<NeowReward.NeowRewardDef> __result, NeowReward __instance, int category) {
            if ( category == 3 ) {
                NeowReward.NeowRewardDef newReward = null;
                switch (AbstractDungeon.player.chosenClass) {
                    case IRONCLAD:
                        newReward = new NeowReward.NeowRewardDef(IRONCLAD_BERSERKER, optionStrings[0]);
                        break;
                    case THE_SILENT:
                        newReward = new NeowReward.NeowRewardDef(SILENT_ASSASSIN, optionStrings[1]);
                        break;
                    case DEFECT:
                        newReward = new NeowReward.NeowRewardDef(DEFECT_STORMLORD, optionStrings[2]);
                        break;
                    default:
                        return __result;
                }
                newReward.desc = "[ #gPlay #gas #g" + newReward.desc + " ]";
                __result.add(newReward);
            }
            return __result;
        }
    }

    // helper functions
    public static void giveCard(AbstractCard card) {
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }
    public static void giveBasicStrike(AbstractCard card) {
        card.tags.add(BaseModCardTags.BASIC_STRIKE);
        giveCard(card);
    }
    public static void giveBasicDefend(AbstractCard card) {
        card.tags.add(BaseModCardTags.BASIC_DEFEND);
        giveCard(card);
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
                || __instance.type == DEFECT_WARDEN ) {
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
                    }
                }
            }
            // dito for "advanced" classes
            if ( __instance.type == IRONCLAD_BERSERKER
                    || __instance.type == SILENT_ASSASSIN
                    || __instance.type == DEFECT_STORMLORD ) {
                Iterator<AbstractCard> it = AbstractDungeon.player.masterDeck.group.iterator();
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
                        giveBasicStrike(new com.megacrit.cardcrawl.cards.blue.BallLightning());
                    } else if ( e instanceof com.megacrit.cardcrawl.cards.blue.Defend_Blue ) {
                        it.remove();
                        giveBasicDefend(new com.megacrit.cardcrawl.cards.blue.Leap());
                    }
                }
                // lose gold and health as penalty
                AbstractPlayer p = AbstractDungeon.player;
                p.loseGold(p.gold);
                p.decreaseMaxHealth(p.maxHealth / 10);
                p.damage(new DamageInfo(null, p.currentHealth / 10 * 3, DamageInfo.DamageType.HP_LOSS));
            }
        }
    }
}