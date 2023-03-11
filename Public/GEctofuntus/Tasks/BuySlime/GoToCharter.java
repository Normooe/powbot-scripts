package Public.GEctofuntus.Tasks.BuySlime;

import Public.GEctofuntus.Constants;
import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
import Util.Util;
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
        return GEctofuntus.needSlime
                && !GEctofuntus.needToHop
                && !Inventory.isFull()
                && !Constants.CHARTER_CREW_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to charter");
        Movement.walkTo(Constants.CHARTER_CREW_AREA.getRandomTile());
    }
}
