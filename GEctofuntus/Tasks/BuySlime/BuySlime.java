package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Constants;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class BuySlime extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;
    public BuySlime(GEctofuntus main) {
        super();
        super.name = "BuySlime";
        this.main = main;
    }

    public static int worldIndex = 0;
    public static boolean needToHop = false;
    @Override
    public boolean activate() {
        return GEctofuntus.slimeCount < (GEctofuntus.bonemealCount + GEctofuntus.boneCount);
    }

    @Override
    public void execute() {
        if (Inventory.isFull()) {
            bankItems();
        } else if (!Constants.CHARTER_CREW_AREA.contains(c.p().tile())) {
            GEctofuntus.currentState = Util.state("Going to charter crew");
            Movement.walkTo(Constants.CHARTER_CREW_AREA.getRandomTile());
        } else if (!Store.opened() && !needToHop) {
            GEctofuntus.currentState = Util.state("Trading charter crew");
            Npc charterCrew = Npcs.stream().name("Trader Crewmember").nearest().first();
            if (charterCrew.valid()) {
                Util.turnTo(charterCrew);
                if (charterCrew.interact("Trade")) {
                    Condition.wait(Store::opened, 200, 20);
                }
            }
        } else if (Store.opened() && Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize() > 0) {
            GEctofuntus.currentState = Util.state("Buying slime");
            if (Store.buy(Constants.BUCKET_OF_SLIME_ID, 10)) {
                Condition.wait(() -> Store.getItem(Constants.BUCKET_OF_SLIME_ID).itemStackSize() == 0, 100, 10);
            }
        } else {
            needToHop = true;
            GEctofuntus.currentState = Util.state("World hopping");
            if (Store.opened()) {
                if (Store.close()) {
                    Condition.wait(() -> !Store.opened(), 100, 20);
                }
            } else {
                World hopWorld = Worlds.stream().id(Constants.WORLD_LIST[worldIndex]).first();
                if (hopWorld.valid()) {
                    if (hopWorld.hop()) {
                        Condition.wait(() -> Worlds.current().getNumber() == Constants.WORLD_LIST[worldIndex], 200, 20);
                        needToHop = false;
                        worldIndex++;
                        if (worldIndex > Constants.WORLD_LIST.length-1) {
                            // Start at the beginning of our world list once we reach the end.
                            worldIndex = 0;
                        }
                    }
                }
            }
        }
    }

    public void bankItems() {
        if (!Constants.BANK_AREA.contains(c.p().tile())) {
            GEctofuntus.currentState = Util.state("Running to bank");
            Movement.walkTo(Constants.BANK_AREA.getRandomTile());
        } else {
            if (!Bank.opened()) {
                GEctofuntus.currentState = Util.state("Opening bank");
                GameObject bankBooth = Objects.stream().name("Bank booth").nearest().first();
                if (bankBooth.valid()) {
                    Util.turnTo(bankBooth);
                    if (bankBooth.interact("Bank")) {
                        Condition.wait(Bank::opened, 150, 20);
                    }
                }
            } else {
                GEctofuntus.currentState = Util.state("Depositing items");
                if (Bank.depositAllExcept("Coins")) {
                    Condition.wait(() -> Inventory.items().length == 1, 100, 10);
                }
            }
        }
    }
}
