package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

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
        return Constants.ALTAR_BOT_FLOOR.contains(c.p().tile())
                && Inventory.stream().name(GEctofuntus.boneType).count() == 13
                && c.p().animation() == -1;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to crusher");
        GameObject staircase = Objects.stream().name("Staircase").within(10).nearest().first();
        if (staircase.valid()) {
            if (staircase.inViewport()) {
                if (staircase.interact("Climb-up")) {
                    Condition.wait(() -> Constants.ALTAR_TOP_FLOOR.contains(c.p().tile()), 150, 40);
                }
            } else {
                System.out.println("Turning camera to staircase");
                Camera.turnTo(staircase);
                Condition.wait(staircase::inViewport, 150, 20);
            }
        }
    }
}

//        if (Game.closeOpenTab()) {
//                Condition.wait(() -> Game.ta == null, 150, 20);
//                }