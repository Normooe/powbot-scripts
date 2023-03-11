package Public.GEctofuntus.Tasks.BuySlime;

import Public.GEctofuntus.Constants;
import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class TradeCharter extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public TradeCharter(GEctofuntus main) {
        super();
        super.name = "TradeCharter";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !GEctofuntus.needToHop
                && !Store.opened()
                && !Inventory.isFull()
                && Constants.CHARTER_CREW_AREA.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Trading charter crew");
        Npc charterCrew = Npcs.stream().within(10).name("Trader Crewmember").nearest().first();
        if (charterCrew.valid()) {
            if (charterCrew.inViewport() && charterCrew.interact("Trade")) {
                Condition.wait(Store::opened, 100, 20);
            } else {
                System.out.println("Turning camera to charterCrew");
                Camera.turnTo(charterCrew);
            }
        }
    }
}