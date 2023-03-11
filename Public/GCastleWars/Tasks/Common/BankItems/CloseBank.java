package Public.GCastleWars.Tasks.Common.BankItems;

import Public.GCastleWars.Constants;
import Public.GCastleWars.GCastleWars;
import Public.GCastleWars.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

import Util.Util;

public class CloseBank extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public CloseBank(GCastleWars main) {
        super();
        super.name = "CloseBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened() && Inventory.occupiedSlotCount() <= 1;
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Closing bank");
        if (Bank.close()) {
            Condition.wait(() -> !Bank.opened(), 50, 50);
        }
    }
}
