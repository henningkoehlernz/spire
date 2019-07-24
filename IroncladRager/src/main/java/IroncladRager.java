import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.BaseMod;

import icr.*;

@SpireInitializer
public class IroncladRager implements EditCardsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber {

    public IroncladRager() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new IroncladRager();
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new RageStrike());
        BaseMod.addCard(new ShieldBash());
        BaseMod.addCard(new VenomStrike());
        BaseMod.addCard(new Dodge());
        BaseMod.addCard(new ProbingStrike());
        BaseMod.addCard(new AutoDefend());
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "loc/eng/ICR-CardStrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "loc/eng/ICR-PowerStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        String[] bloodied = { "bloodied" };
        BaseMod.addKeyword("Bloodied", bloodied, "At or below 50% health.");
        String[] concealment = { "concealment" };
        BaseMod.addKeyword("Concealment", concealment, "Reduces all damage taken for one turn.");
    }

}
