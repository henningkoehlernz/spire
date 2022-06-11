package th;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GoldenGun extends CustomRelic {

    public static final String ID = "TH:GoldenGun";
    private static final String IMG_PATH = TreasureHunter.IMG_PATH + "golden_gun.png";
    private static final int DAMAGE = 5;

    public GoldenGun() {
        super(ID, new Texture(IMG_PATH), RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.type == TreasurePatch.TREASURE) {
            this.flash();
            AbstractDungeon.actionManager.addToTop(new DamageRandomEnemyAction(
                    new DamageInfo(AbstractDungeon.player, DAMAGE, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAMAGE + DESCRIPTIONS[1];
    }

}
