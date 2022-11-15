package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Store;

public class BuyItem extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public BuyItem(GEctofuntus main) {
        super();
        super.name = "BuySlime";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Store.opened() && !Inventory.isFull();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Buying slime");
        int slimeInStore = Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize();
        if (slimeInStore > 0) {
            if (Store.buy(Constants.BUCKET_OF_SLIME_ID, 10)) {
                int newSlimeInStore = Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize();
                Condition.wait(() -> newSlimeInStore != slimeInStore, 100, 10);
                GEctofuntus.slimeCounter += slimeInStore - newSlimeInStore;
                if (newSlimeInStore == 0) {
                    GEctofuntus.needToHop = true;
                }
            }
        } else {
            GEctofuntus.needToHop = true;
        }
    }
}