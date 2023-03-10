package GFossilIsland.Tasks.CutTrees;

import Util.Util;
import GFossilIsland.Constants;
import GFossilIsland.GFossilIsland;
import GFossilIsland.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.Players;

public class CutTree extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public CutTree(GFossilIsland main) {
        super();
        super.name = "CutTree";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !Inventory.isFull()
                && !(Players.local().animation() == Constants.CUTTING_ANIM)
                && Constants.TREE_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Cutting tree");
        GameObject tree = Objects.stream(10).type(GameObject.Type.INTERACTIVE).nearest().name("Teak Tree").nearest().first();
        if (tree.valid() && tree.interact("Chop down")) {
            Condition.wait(() ->  c.p().animation() == Constants.CUTTING_ANIM, 150, 20);
        }
    }
}
