package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class GoToBank extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public GoToBank(GEctofuntus main) {
        super();
        super.name = "GoToBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return (Inventory.isFull() || Inventory.stream().name("Coins").first().stackSize() < 1000) && !Constants.BANK_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to bank");
        Movement.walkTo(Constants.BANK_AREA.getRandomTile());
    }
}
