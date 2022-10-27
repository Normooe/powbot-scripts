package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Movement;

public class GoToCharter extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public GoToCharter(GEctofuntus main) {
        super();
        super.name = "GoToCharter";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !Inventory.isFull() && !Constants.CHARTER_CREW_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = "Going to charter";
        Movement.walkTo(Constants.CHARTER_CREW_AREA.getRandomTile());
    }
}
