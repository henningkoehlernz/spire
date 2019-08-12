import com.megacrit.cardcrawl.cards.AbstractCard;
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

    private static void addRedCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.IRONCLAD_RAGER.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.IRONCLAD_BERSERKER.name(), card.cardID);
    }

    private static void addGreenCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.SILENT_POISONER.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.SILENT_ASSASSIN.name(), card.cardID);
    }

    private static void addBlueCard(AbstractCard card) {
        BaseMod.addCard(card);
        UnlockPatch.unlockCardWithReward(NeowPatch.DEFECT_WARDEN.name(), card.cardID);
        UnlockPatch.unlockCardWithReward(NeowPatch.DEFECT_STORMLORD.name(), card.cardID);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new RageStrike());
        BaseMod.addCard(new ShieldBash());
        BaseMod.addCard(new VenomStrike());
        BaseMod.addCard(new Dodge());
        BaseMod.addCard(new ProbingStrike());
        BaseMod.addCard(new AutoDefend());

        addRedCard(new Frenzy());
        addRedCard(new PowerAttack());
        addRedCard(new VitalStrike());

        addGreenCard(new Cloudkill());
        addGreenCard(new Paralyse());
        addGreenCard(new Vanish());

        addBlueCard(new ChainLightning());
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
