package Public.GFossilIsland.Tasks.CutTrees;

import Util.Util;
import Public.GFossilIsland.Constants;
import Public.GFossilIsland.GFossilIsland;
import Public.GFossilIsland.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class GoToTrees extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public GoToTrees(GFossilIsland main) {
        super();
        super.name = "GoToTrees";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !Inventory.isFull()
                && !Constants.TREE_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Going to trees");
        Movement.moveTo(Constants.OUTSIDE_HOLE_BANK_SIDE.getRandomTile());
        GameObject hole = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Hole").nearest().first();
        if (hole.valid()) {
            if (hole.inViewport() && hole.interact("Climb through")) {
                Condition.wait(() -> Constants.TREE_AREA.contains(c.p().tile()), 150, 20);
            } else {
                System.out.println("Turning camera to hole");
                Camera.turnTo(hole);
            }
        }
//        Movement.moveTo(Constants.TREE_AREA.getRandomTile());
    }
}
