package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.GainGoldTextEffect;

public class CursedGold extends CustomRelic {

    public static final String ID = "TH:CursedGold";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "cursed_gold.png";
    private static final int GOLD = 5;

    public CursedGold() {
        super(ID, new Texture(IMG_PATH), RelicTier.SHOP, LandingSound.CLINK);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (drawnCard.type == TreasurePatch.TREASURE) {
            flash();
            // apply vulnerable to everyone
            AbstractCreature p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new VulnerablePower(p, 1, false)));
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new VulnerablePower(m, 1, false)));
            }
        }
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.type == TreasurePatch.TREASURE) {
            flash();
            setCounter(counter < 0 ? GOLD : counter + GOLD);
            AbstractDungeon.player.gainGold(GOLD);
            AbstractDungeon.effectList.add(new GainGoldTextEffect(GOLD));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0].replace("!M!", Integer.toString(GOLD));
    }

}
