package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Npc;
import org.powbot.api.rt4.Npcs;
import org.powbot.api.rt4.Store;

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
        return Constants.CHARTER_CREW_AREA.contains(c.p().tile()) && !Store.opened() && !Inventory.isFull() && !GEctofuntus.needToHop;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Trading charter crew");
        Npc charterCrew = Npcs.stream().within(10).name("Trader Crewmember").nearest().first();
        if (charterCrew.valid()) {
            Util.turnTo(charterCrew);
            if (charterCrew.interact("Trade")) {
                Condition.wait(Store::opened, 100, 20);
            }
        }
    }
}
