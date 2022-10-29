package GEctofuntus.Tasks.Common;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

import java.util.Objects;

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
        if (c.p().movementAnimation() > 808) {
            return false;
        }
        if (Objects.equals(GEctofuntus.currentTask, "BuySlime")) {
            return (Inventory.isFull() || Inventory.stream().name("Coins").first().stackSize() < 1000) && !Constants.BANK_AREA.contains(c.p().tile());
        } else if (Objects.equals(GEctofuntus.currentTask, "CrushBones")) {
            return !Constants.BANK_AREA.contains(c.p().tile()) && Inventory.stream().name("Pot").isEmpty();
        } else if (Objects.equals(GEctofuntus.currentTask, "OfferBones")) {
            return !Constants.BANK_AREA.contains(c.p().tile())
                    && (Inventory.stream().name(GEctofuntus.bonemealType).isEmpty() || Inventory.stream().name("Bucket of slime").isEmpty());
        } else if (Objects.equals(GEctofuntus.currentTask, "GetItemCounts")) {
            return !Constants.BANK_AREA.contains(c.p().tile());
        }
        return false;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Going to bank "+GEctofuntus.currentTask);
        getToBank();
    }

    public void getToBank() {
        if (Constants.ALTAR_TOP_FLOOR.contains(c.p().tile())) {
            Util.useEctophial();
        } else if (Constants.PORT_PHASMATYS.contains(c.p().tile()) || Constants.CHARTER_CREW_AREA.contains(c.p().tile())) {
            Movement.walkTo(Constants.BANK_AREA.getRandomTile());
        } else if (!Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile())) {
            Movement.walkTo(Constants.BARRIER_ALTAR_SIDE.getRandomTile());
        } else {
            enterBarrier();
        }
    }

    public void enterBarrier() {
        GameObject barrier = org.powbot.api.rt4.Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Energy Barrier").nearest().first();
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
