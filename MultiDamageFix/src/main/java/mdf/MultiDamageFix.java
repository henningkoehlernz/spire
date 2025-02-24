package mdf;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiDamageFix {

    private static final Logger logger = LogManager.getLogger(MultiDamageFix.class.getName());

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class, CardType.class,
                CardColor.class, CardRarity.class, CardTarget.class, DamageType.class}
    )
    public static class AbstractCardConstructor {
        public static void Postfix(AbstractCard instance, String id, String name, String imgUrl, int cost, String rawDescription, CardType type,
                                   CardColor color, CardRarity rarity, CardTarget target, DamageType dType) {
            if (target == CardTarget.ALL_ENEMY) {
                Reflection.set(instance, AbstractCard.class, "isMultiDamage", true);
                logger.info("set isMultiDamage=true for " + instance.cardID);
            }
        }
    }

}
