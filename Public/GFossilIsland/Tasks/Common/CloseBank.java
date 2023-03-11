package Public.GFossilIsland.Tasks.Common;

import Util.Util;
import Public.GFossilIsland.Constants;
import Public.GFossilIsland.GFossilIsland;
import Public.GFossilIsland.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class CloseBank extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public CloseBank(GFossilIsland main) {
        super();
        super.name = "CloseBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened() && (Inventory.isEmpty() || Inventory.stream().name("Teak logs").isEmpty());
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Closing bank");
        if (Bank.close()) {
            Condition.wait(() -> !Bank.opened(), 100, 20);
        }
    }
}
