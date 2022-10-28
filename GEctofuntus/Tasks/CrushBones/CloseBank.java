package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class CloseBank extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public CloseBank(GEctofuntus main) {
        super();
        super.name = "CloseBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened()
                && Inventory.stream().name(GEctofuntus.bonemealType).count() == 13
                && Inventory.stream().name("Pot").count() == 13;
    }

    @Override
    public void execute() {
        if (Bank.close()) {
            Condition.wait(() -> !Bank.opened(), 150, 20);
        }
    }
}
