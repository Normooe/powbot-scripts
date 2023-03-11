package GCastleWars.Tasks.Common.BankItems;

import GCastleWars.Constants;
import GCastleWars.GCastleWars;
import GCastleWars.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

import Util.Util;

public class BankItems extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public BankItems(GCastleWars main) {
        super();
        super.name = "DepositItems";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened() && !Inventory.stream().allMatch(i -> i.name().equals("Castle wars ticket"));
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Banking items besides tickets");
        if (Bank.depositAllExcept("Castle wars ticket")) {
            Condition.wait(() -> Inventory.occupiedSlotCount() <= 1, 50, 50);
        }
    }
}
