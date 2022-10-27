package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Store;

public class BuySlime extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public BuySlime(GEctofuntus main) {
        super();
        super.name = "BuySlime";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Store.opened();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Buying slime");
        if (Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize() > 0) {
            if (Store.buy(Constants.BUCKET_OF_SLIME_ID, 10)) {
                Condition.wait(() -> Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize() == 0, 100, 10);
            }
        } else {
            GEctofuntus.needToHop = true;
        }
    }
}