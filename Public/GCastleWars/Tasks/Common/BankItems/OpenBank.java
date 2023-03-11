package Public.GCastleWars.Tasks.Common.BankItems;

import Public.GCastleWars.Constants;
import Public.GCastleWars.GCastleWars;
import Public.GCastleWars.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class OpenBank extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public OpenBank(GCastleWars main) {
        super();
        super.name = "OpenBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !Inventory.stream().allMatch(i -> i.name().equals("Castle wars ticket"))
                && Bank.present()
                && !Bank.opened()
                && (c.p().movementAnimation() == c.p().idleAnimation());
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Opening bank");
        if (Bank.inViewport() && Bank.open()) {
            Condition.wait(Bank::opened, 50, 50);
        } else {
            GameObject bankChest = Objects.stream().name("Bank chest").nearest().first();
            if (bankChest.valid()) {
                GCastleWars.currentState = Util.state("Turning camera to bank");
                Camera.turnTo(bankChest);
            }
        }
    }
}
