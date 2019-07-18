import com.megacrit.cardcrawl.localization.CardStrings;

import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import basemod.BaseMod;

@SpireInitializer
public class IroncladRager implements EditCardsSubscriber, EditStringsSubscriber {

    public IroncladRager() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new IroncladRager();
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new RageStrike());
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class,"loc/eng/IroncladRager-Strings.json");
    }

}
