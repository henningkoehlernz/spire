package th;

import com.megacrit.cardcrawl.cards.AbstractCard;
import basemod.abstracts.CustomCard;

public abstract class AbstractTreasure extends CustomCard {

    AbstractTreasure(String id, String name, String img, int cost, String rawDescription, CardTarget target) {
        super(id, name, img, cost, rawDescription, TreasurePatch.TREASURE, CardColor.COLORLESS, CardRarity.SPECIAL, target);
        this.exhaust = true;
    }

}
