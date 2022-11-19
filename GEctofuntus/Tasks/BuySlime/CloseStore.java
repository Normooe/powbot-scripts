package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Store;

public class CloseStore extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public CloseStore(GEctofuntus main) {
        super();
        super.name = "CloseStore";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Store.opened() && GEctofuntus.needToHop;
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Closing store");
        if (Store.close()) {
            Condition.wait(() -> !Store.opened(), 100, 20);
        }
    }
}
