package GEctofuntus.Tasks.Common;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class OpenBank extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public OpenBank(GEctofuntus main) {
        super();
        super.name = "OpenBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Constants.BANK_AREA.contains(c.p().tile()) && !Bank.opened() && Inventory.isFull();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = "Opening bank";
        if (Bank.present()) {
            if (Bank.open()) {
                Condition.wait(Bank::opened, 100, 20);
            }
        }
    }
}
