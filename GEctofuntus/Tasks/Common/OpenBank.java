package GEctofuntus.Tasks.Common;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

import java.util.Map;
import java.util.Objects;

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
        if (Bank.present() && !Bank.opened() && Constants.BANK_AREA.contains(c.p().tile())) {
            for (Map.Entry<String,Integer> entry : GEctofuntus.requiredItems.entrySet()) {
                String itemName = entry.getKey();
                int numOfItem = entry.getValue();
                long invyCount = Inventory.stream().name(itemName).count();
                if (invyCount < numOfItem) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Opening bank");
        if (Bank.open()) {
            Condition.wait(Bank::opened, 100, 20);
        }
    }
}
