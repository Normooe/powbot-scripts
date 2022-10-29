package GEctofuntus.Tasks.Common;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;

import java.util.Objects;

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
        if (Objects.equals(GEctofuntus.currentTask, "CrushBones")) {
            return Inventory.stream().name(GEctofuntus.boneType).count() == 13
                    && Inventory.stream().name("Pot").count() == 13
                    && !Bank.opened()
                    && !Constants.ALTAR_BOT_FLOOR.contains(c.p().tile())
                    && !Constants.ALTAR_TOP_FLOOR.contains(c.p().tile());
        } else if (Objects.equals(GEctofuntus.currentTask, "OfferBones")) {
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
