package GEctofuntus.Tasks;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Constants;
import GEctofuntus.Task;
import GEctofuntus.Util;
import org.powbot.api.Area;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import static GEctofuntus.Constants.bankArea;

public class BuySlime extends Task {
    private Constants c = new Constants();
    GEctofuntus main;
    public BuySlime(GEctofuntus main) {
        super();
        super.name = "BuySlime";
        this.main = main;
    }

    Area charterCrewArea = new Area(new Tile(3696, 3506, 0), new Tile(3707, 3495, 0));
    public static final int BUCKET_OF_SLIME_ID = 4286;
    public static final int[] worlds = {350, 351, 352, 354, 355, 356, 357, 358, 359, 360, 367, 368, 369, 370, 374, 375, 376, 377, 378};
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
        } else if (!charterCrewArea.contains(c.p().tile())) {
            GEctofuntus.state("Going to charter crew");
            if (Movement.walkTo(charterCrewArea.getRandomTile())) {
                Condition.wait(() -> charterCrewArea.contains(c.p().tile()), 100, 20);
            }
        } else if (!Store.opened() && !needToHop) {
            GEctofuntus.state("Trading charter crew");
            Npc charterCrew = Npcs.stream().name("Trader Crewmember").nearest().first();
            if (charterCrew.valid()) {
                Util.turnTo(charterCrew);
                if (charterCrew.interact("Trade")) {
                    Condition.wait(Store::opened, 200, 20);
                }
            }
        } else if (Store.opened() && Store.getItem(BUCKET_OF_SLIME_ID).itemStackSize() > 0) {
            GEctofuntus.state("Buying slime");
            if (Store.buy(BUCKET_OF_SLIME_ID, 10)) {
                Condition.wait(() -> Store.getItem(BUCKET_OF_SLIME_ID).itemStackSize() == 0, 100, 10);
            }
        } else {
            needToHop = true;
            GEctofuntus.state("World hopping");
            if (Store.opened()) {
                if (Store.close()) {
                    Condition.wait(() -> !Store.opened(), 100, 20);
                }
            } else {
                World hopWorld = Worlds.stream().id(worlds[worldIndex]).first();
                if (hopWorld.valid()) {
                    if (hopWorld.hop()) {
                        Condition.wait(() -> Worlds.current().getNumber() == worlds[worldIndex], 200, 20);
                        needToHop = false;
                        worldIndex++;
                        if (worldIndex > worlds.length-1) {
                            // Start at the beginning of our world list once we reach the end.
                            worldIndex = 0;
                        }
                    }
                }
            }
        }
    }

    public void bankItems() {
        if (!bankArea.contains(c.p().tile())) {
            GEctofuntus.state("Running to bank");
            if (Movement.walkTo(bankArea.getRandomTile())) {
                Condition.wait(() -> bankArea.contains(c.p().tile()), 200, 20);
            }
        } else {
            if (!Bank.opened()) {
                GEctofuntus.state("Opening bank");
                GameObject bankBooth = Objects.stream().name("Bank booth").nearest().first();
                if (bankBooth.valid()) {
                    Util.turnTo(bankBooth);
                    if (bankBooth.interact("Bank")) {
                        Condition.wait(Bank::opened, 150, 20);
                    }
                }
            } else {
                GEctofuntus.state("Depositing items");
                if (Bank.depositAllExcept("Coins")) {
                    Condition.wait(() -> Inventory.items().length == 1, 100, 10);
                }
            }
        }
    }
}
