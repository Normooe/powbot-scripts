package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class GoToBank extends Task {
    // Once https://github.com/powbot/issues/issues/141 is fixed we can probably make one common GoToBank task
    private final Constants c = new Constants();
    GEctofuntus main;

    public GoToBank(GEctofuntus main) {
        super();
        super.name = "GoToBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !Constants.BANK_AREA.contains(c.p().tile()) && Inventory.stream().name("Pot").isEmpty();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to bank");
        getToBank();
    }

    public void getToBank() {
        if (Constants.PORT_PHASMATYS.contains(c.p().tile())) {
            Movement.walkTo(Constants.BANK_AREA.getRandomTile());
        } else if (!Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile())) {
            Movement.walkTo(Constants.BARRIER_ALTAR_SIDE.getRandomTile());
        } else {
            enterBarrier();
        }
    }

    public void enterBarrier() {
        GameObject barrier = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Energy Barrier").nearest().first();
        if (!barrier.valid()) {
            return;
        }
        Util.turnTo(barrier);
        GEctofuntus.currentState = Util.state("Interacting with barrier");
        if (barrier.interact("Pass")) {
            Condition.wait(Chat::canContinue, 150, 20);
            GEctofuntus.currentState = Util.state("Clicking continue on barrier dialog");
            if (Chat.clickContinue()) {
                GEctofuntus.currentState = Util.state("Waiting to get inside");
                Condition.wait(() -> Players.local().tile().equals(Constants.BARRIER_TILE_WEST) || Players.local().tile().equals(Constants.BARRIER_TILE_EAST), 150, 20);
            }
        }
    }
}
