package Public.GEctofuntus.Tasks.OfferBones;

import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
import Public.GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class OfferBones extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public OfferBones(GEctofuntus main) {
        super();
        super.name = "OfferBones";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !GEctofuntus.needToCollectTokens
                && Inventory.stream().name("Bucket of slime").count() > 0
                && Inventory.stream().name(GEctofuntus.bonemealType).count() > 0
                && Constants.ALTAR_BOT_FLOOR.contains(c.p().tile());
    }

    @Override
    public void execute() {
        if (Widgets.widget(Constants.altarFullDialogueID).component(1).text().contains("room to put any more in")) {
            GEctofuntus.currentState = Util.state("Setting needToCollectTokens = true");
            GEctofuntus.needToCollectTokens = true;
        } else {
            GEctofuntus.currentState = Util.state("Offering bones");
            offerBones();
        }
    }

    public void offerBones() {
        Game.tab(Game.Tab.INVENTORY);
        GameObject ectofuntus = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Ectofuntus").nearest().first();
        long bucketCount = Inventory.stream().name("Bucket of slime").count();
        if (ectofuntus.valid()) {
            if (ectofuntus.inViewport() && ectofuntus.interact("Worship")) {
                Condition.wait(() -> Inventory.stream().name("Bucket of slime").count() < bucketCount, 100, 50);
            } else {
                System.out.println("Turning camera to ectofuntus");
                Camera.turnTo(ectofuntus);
            }
        }
    }
}
