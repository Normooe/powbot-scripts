package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class BankItems extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public BankItems(GEctofuntus main) {
        super();
        super.name = "BankItems";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened();
    }

    @Override
    public void execute() {
        Util.state("Banking items");
        if (Inventory.stream().name("Coins").isNotEmpty() && Inventory.stream().name("Coins").first().stackSize() > 1000) {
            if (Bank.depositAllExcept("Coins")) {
                Condition.wait(() -> Inventory.stream().count() == 1, 100, 20);
            }
        } else if (Bank.stream().name("Coins").first().stackSize() > 1000) {
            if (Bank.withdraw("Coins", Bank.stream().name("Coins").first().stackSize())) {
                Condition.wait(() -> Inventory.stream().name("Coins").first().stackSize() > 1000, 150, 20);
            }
        } else {
            Util.state("No coins in inventory or bank to buy slime, ending script.");
            Util.endScript();
        }
    }
}
