package GEctofuntus.Tasks.CrushBones;

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
        if (Inventory.stream().name(GEctofuntus.bonemealType).isNotEmpty()) {
            Bank.depositAllExcept("Ectophial");
        } else if (Inventory.stream().name(GEctofuntus.boneType).isEmpty()) {
            if (Bank.stream().name(GEctofuntus.boneType).first().stackSize() >= 13) {
                if (Bank.withdraw(GEctofuntus.boneType, 13)) {
                    Condition.wait(() -> Inventory.stream().name(GEctofuntus.boneType).count() == 13, 150, 20);
                }
            } else {
                Util.endScript("No bones in inventory or bank to crush, ending script.");
            }
        } else if (Inventory.stream().name("Pot").isEmpty()) {
            if (Bank.stream().name("Pot").first().stackSize() >= 13) {
                if (Bank.withdraw("Pot", 13)) {
                    Condition.wait(() -> Inventory.stream().name("Pot").count() == 13, 150, 20);
                }
            } else {
                Util.endScript("No pots in inventory or bank to crush, ending script.");
            }
        }
    }
}
