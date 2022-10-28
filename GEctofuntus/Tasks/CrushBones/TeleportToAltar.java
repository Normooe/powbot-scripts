package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;

public class TeleportToAltar extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public TeleportToAltar(GEctofuntus main) {
        super();
        super.name = "TeleportToAltar";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Inventory.stream().name(GEctofuntus.boneType).count() == 13
                && Inventory.stream().name("Pot").count() == 13
                && !Bank.opened()
                && !Constants.ALTAR_BOT_FLOOR.contains(c.p().tile())
                && !Constants.ALTAR_TOP_FLOOR.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Using ectophial");
        Game.tab(Game.Tab.INVENTORY);
        Item ectophial = Inventory.stream().name("Ectophial").first();
        if (ectophial.valid()) {
            if (ectophial.interact("Empty")) {
                Condition.wait(() -> Constants.ALTAR_BOT_FLOOR.contains(c.p().tile()), 100, 20);
            }
        } else {
            Util.endScript("No ectophial in inventory. Ending script");
        }
    }
}
