package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Camera;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Objects;

public class CollectBonemeal extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public CollectBonemeal(GEctofuntus main) {
        super();
        super.name = "CollectBonemeal";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GEctofuntus.needToCollectBones;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Collecting bonemeal");
        GameObject bin = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Bin").nearest().first();
        if (!bin.inViewport()) {
            Camera.turnTo(bin);
            Condition.wait(bin::inViewport, 150, 20);
        } else if (bin.valid() && bin.interact("Empty") && Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 13, 150, 800)) {
            GEctofuntus.needToCollectBones = false;
        }
    }
}
