package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Objects;

public class GoToCrusher extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public GoToCrusher(GEctofuntus main) {
        super();
        super.name = "GoToCrusher";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Constants.ALTAR_BOT_FLOOR.contains(c.p().tile()) && Inventory.stream().name(GEctofuntus.boneType).count() == 13;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to crusher");
        GameObject staircase = Objects.stream().name("Staircase").within(10).nearest().first();
        if (staircase.valid()) {
            if (staircase.interact("Climb-up")) {
                Constants.ALTAR_TOP_FLOOR.contains(c.p().tile());
            }
        }
    }
}
