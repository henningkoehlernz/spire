package ironclad_rager;

import basemod.interfaces.EditKeywordsSubscriber;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.KeywordStrings;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.BaseMod;

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
        BaseMod.addCard(new ProbingStrike());
        BaseMod.addCard(new AutoDefend());
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "loc/eng/IroncladRager-CardStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        String[] bloodied = { "bloodied" };
        BaseMod.addKeyword("Bloodied", bloodied, "At or below 50% health.");
    }

}
