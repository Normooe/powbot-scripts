package Public.GEctofuntus.Tasks.Common;

import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
import Public.GEctofuntus.Constants;
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
         switch (GEctofuntus.currentTask) {
            case "BuySlime":
                    return (Inventory.isFull() || Inventory.stream().name("Coins").first().stackSize() < 1_000) && !Constants.BANK_AREA.contains(c.p().tile());
            case "CrushBones":
                    return Inventory.stream().name("Pot").isEmpty() && !Constants.BANK_AREA.contains(c.p().tile());
            case "OfferBones":
                return !Constants.BANK_AREA.contains(c.p().tile())
                    && (Inventory.stream().name(GEctofuntus.bonemealType).isEmpty() || Inventory.stream().name("Bucket of slime").isEmpty());
            case "GetItemCounts":
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
        if (Constants.PORT_PHASMATYS.contains(c.p().tile())) {
            Movement.walkTo(Constants.BANK_AREA.getRandomTile());
        } else if (!Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile())) {
            Movement.walkTo(Constants.BARRIER_ALTAR_SIDE.getRandomTile());
        } else {
            enterBarrier();
        }
    }

    public void enterBarrier() {
        GameObject barrier = org.powbot.api.rt4.Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Energy Barrier").nearest().first();
        GEctofuntus.currentState = Util.state("Interacting with barrier");
        if (barrier.valid()) {
            if (barrier.inViewport() && barrier.interact("Pass") && Condition.wait(Chat::canContinue, 150, 20)) {
                GEctofuntus.currentState = Util.state("Clicking continue on barrier dialog");
                if (Chat.clickContinue()) {
                    GEctofuntus.currentState = Util.state("Waiting to get inside");
                    Condition.wait(() -> Players.local().tile().equals(Constants.BARRIER_TILE_WEST) || Players.local().tile().equals(Constants.BARRIER_TILE_EAST), 150, 20);
                }
            } else {
                System.out.println("Turning camera to barrier");
                Camera.turnTo(barrier);
            }
        }
    }
}
