package GFossilIsland.Tasks.FarmSeaweed;

import GFossilIsland.Constants;
import GFossilIsland.GFossilIsland;
import Util.Util;
import GFossilIsland.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class LootSpore extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public LootSpore(GFossilIsland main) {
        super();
        super.name = "LootSpore";
        this.main = main;
    }

    @Override
    public boolean activate() {
        GroundItem spore = GroundItems.stream().name("Seaweed spore").nearest().first();
        return spore.valid() && (Inventory.stream().name("Seaweed spore").isNotEmpty() || !Inventory.isFull());
    }

    @Override
    public void execute() {
        Util.state("Looting seaweed spores");
        GroundItem spore = GroundItems.stream().name("Seaweed spore").nearest().first();
        if (spore.valid()) {
            Util.turnTo(spore);
            if (spore.interact("Take")) {
                Condition.wait(() -> !spore.valid(), 150, 20);
            }
        }
    }
}
