package Public.GFossilIsland.Tasks.CutTrees;

import Public.GEctofuntus.GEctofuntus;
import Util.Util;
import Public.GFossilIsland.Constants;
import Public.GFossilIsland.GFossilIsland;
import Public.GFossilIsland.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class PickupBirdsNest extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public PickupBirdsNest(GFossilIsland main) {
        super();
        super.name = "PickupBirdsNest";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GroundItems.stream().name("Bird nest").nearest().first().valid();
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Picking up bird nest");
        GroundItem birdsNest = GroundItems.stream().name("Bird nest").nearest().first();
        if (Game.tab(Game.Tab.INVENTORY) && Inventory.isFull()) {
            dropItem();
        } else if (birdsNest.inViewport() && birdsNest.interact("Take")) {
            Condition.wait(() -> !birdsNest.valid(), 150, 20);
        }
    }

    public void dropItem() {
        if (Inventory.stream().name("Teak logs").isNotEmpty()) {
            GEctofuntus.currentState = Util.state("Dropping logs to make room for bird's nest.");
            if (!Inventory.selectedItem().name().equals("Teak logs")) {
                if (Inventory.stream().name("Teak logs").first().interact("Drop")) {
                    Condition.wait(() -> !Inventory.isFull(), 100, 20);
                }
            }
        }
    }
}
