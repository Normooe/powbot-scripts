package GEctofuntus.Tasks.Common;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

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
        switch (GEctofuntus.currentTask) {
            case "CrushBones":
                return Inventory.stream().name(GEctofuntus.boneType).count() == 13
                        && Inventory.stream().name("Pot").count() == 13
                        && !Bank.opened()
                        && !Constants.ALTAR_BOT_FLOOR.contains(c.p().tile())
                        && !Constants.ALTAR_TOP_FLOOR.contains(c.p().tile());
            case "OfferBones":
                return Inventory.stream().name("Bucket of slime").count() == 13
                        && Inventory.stream().name(GEctofuntus.bonemealType).count() == 13
                        && !Bank.opened()
                        && !Constants.ALTAR_BOT_FLOOR.contains(c.p().tile());
        }
        return false;
    }

    @Override
    public void execute() {
        Util.useEctophial();
    }
}
