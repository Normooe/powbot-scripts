package GFossilIsland.Tasks.Common;

import GFossilIsland.Constants;
import GFossilIsland.GFossilIsland;
import GFossilIsland.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class OpenBank extends Task {
    private final Constants c = new Constants();
    GFossilIsland main;

    public OpenBank(GFossilIsland main) {
        super();
        super.name = "OpenBank";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Bank.present()
                && !Bank.opened()
                && Inventory.isFull()
                && (c.p().movementAnimation() == c.p().idleAnimation());
    }

    @Override
    public void execute() {
        GFossilIsland.currentState = Util.state("Opening bank");
        if (Bank.inViewport() && Bank.open()) {
            Condition.wait(Bank::opened, 100, 20);
        } else {
            GameObject bankChest = Objects.stream().name("Bank chest").nearest().first();
            if (bankChest.valid()) {
                Camera.turnTo(bankChest);
            }
        }
    }
}
