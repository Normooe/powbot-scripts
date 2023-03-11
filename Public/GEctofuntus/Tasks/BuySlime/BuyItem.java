package Public.GEctofuntus.Tasks.BuySlime;

import Public.GEctofuntus.Constants;
import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
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
        return Store.opened()
                && !Inventory.isFull()
                && Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize() > 0;
    }

    @Override
    public void execute() {
        int slimeInStore = Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize();
        GEctofuntus.currentState = Util.state("Buying slime");
        if (Store.buy(Constants.BUCKET_OF_SLIME_ID, 10)) {
            int newSlimeInStore = Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize();
            Condition.wait(() -> newSlimeInStore != slimeInStore, 150, 20);
            GEctofuntus.slimeCounter += slimeInStore - newSlimeInStore;
        }
    }
}