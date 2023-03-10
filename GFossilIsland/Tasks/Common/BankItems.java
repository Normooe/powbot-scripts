package GFossilIsland.Tasks.Common;

import GFossilIsland.Constants;
import GFossilIsland.GFossilIsland;
import GFossilIsland.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class BankItems extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public BankItems(GFossilIsland main) {
        super();
        super.name = "BankItems";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Inventory.isFull() && Bank.opened();
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Banking items");
        if (Bank.depositAllExcept(Constants.AXE_LIST)) {
            // This should probably check inventory for only items in axe list but cant think of the code rn
            Condition.wait(() -> Inventory.isEmpty()
                    || Inventory.stream().name("Teak logs").isEmpty(), 150, 20);
        }
    }
}
