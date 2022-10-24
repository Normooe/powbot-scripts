package GEctofuntus.Tasks;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Constants;
import GEctofuntus.Task;
import GEctofuntus.Util;
import org.powbot.api.Area;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import static GEctofuntus.Constants.*;
import static GEctofuntus.GEctofuntus.state;

public class CrushBones extends Task {
    private Constants c = new Constants();
    GEctofuntus main;

    // task vars
    Area loaderArea = new Area(new Tile(3657, 3526, 1), new Tile(3661, 3524, 1));

    public CrushBones(GEctofuntus main) {
        super();
        super.name = "CrushBones";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GEctofuntus.boneCount > 0 && GEctofuntus.potCount > 0;
    }

    @Override
    public void execute() {
        if (Inventory.stream().name("Pot").isEmpty()) {
            bankItems();
        } else if (c.p().tile().equals(STUCK_BANK_TILE)) {
            // Webwalker gets stuck here - https://github.com/powbot/issues/issues/142
            // Can deprecate handleStuckBank() once issues are fixed.
            state("Getting unstuck");
            handleStuckBank();
        } else if (!loaderArea.contains(c.p().tile())) {
            // Webwalker can get stuck behind barrier inside also - https://github.com/powbot/issues/issues/141#issuecomment-1288236687
            // Can deprecate goToAltar() once issues are fixed.
            state("Going to loader");
            goToAltar();
            if (Movement.walkTo(loaderArea.getRandomTile())) {
                Condition.wait(() -> loaderArea.contains(c.p().tile()), 200, 40);
            }
        } else {
            crushAllBones();
        }
    }
    
    public void putBonesInLoader(GameObject loader) {
        state("Loading bones");
        Game.tab(Game.Tab.INVENTORY);
        Item bones = Inventory.stream().name(GEctofuntus.boneType).first();
        if (bones.valid()) {
            if (!Inventory.selectedItem().name().equals(GEctofuntus.boneType)) {
                if (bones.interact("Use")) {
                    Condition.wait(() -> Inventory.selectedItem().name().equals(GEctofuntus.boneType), 100, 10);
                }
            } else {
                if (loader.click()) {
                    GEctofuntus.needToLoadBones = false;
                    Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 14, 150, 800);
                }
            }
        }
    }


    public void bankItems() {
        if (!Constants.bankArea.contains(c.p().tile())) {
            state("Going to bank");
            getToBank();
            Condition.wait(() -> Constants.bankArea.contains(c.p().tile()), 200, 40);
        } else if (!Bank.opened()) {
            state("Opening bank");
            if (Bank.open()) {
                Condition.wait(Bank::opened, 100, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.bonemealType).isNotEmpty()) {
            state("Depositing items");
            if (Bank.depositInventory()) {
                Condition.wait(Inventory::isEmpty, 100, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.boneType).isEmpty()) {
            state("Withdrawing items");
            if (Bank.withdraw(GEctofuntus.boneType, 14)) {
                Condition.wait(() -> Inventory.stream().name(GEctofuntus.boneType).count() == 14, 100, 20);
            }
        } else if (Inventory.stream().name("Pot").isEmpty()) {
            state("Withdrawing items");
            if (Bank.withdraw("Pot", 14)) {
                Condition.wait(() -> Inventory.stream().name("Pot").count() == 14, 100, 20);
            }
        }
    }

    public void getToBank() {
        if (PORT_PHASMATYS.contains(c.p().tile())) {
            if (Movement.walkTo(Constants.bankArea.getRandomTile())) {
                Condition.wait(() -> Constants.bankArea.contains(c.p().tile()), 200, 40);
            }
        } else if (!BARRIER_ALTAR_SIDE.contains(c.p().tile())) {
            if (Movement.walkTo(BARRIER_ALTAR_SIDE.getRandomTile())) {
                Condition.wait(() -> c.p().tile() == BARRIER_TILE_EAST || c.p().tile() == BARRIER_TILE_WEST, 200, 40);
            }
        } else {
            enterBarrier();
        }
    }

    public void goToAltar() {
        if (PORT_PHASMATYS.contains(c.p().tile())) {
            if (!c.p().tile().equals(BARRIER_TILE_EAST)) {
                if (Movement.walkTo(BARRIER_TILE_EAST)) {
                    Condition.wait(() -> c.p().tile().equals(BARRIER_TILE_EAST), 200, 40);
                }
            } else {
                GameObject barrier = Objects.stream().name("Energy Barrier").nearest().first();
                if (barrier.valid() && barrier.reachable()) {
                    Util.turnTo(barrier);
                    if (barrier.interact("Pass")) {
                        Condition.wait(() -> BARRIER_ALTAR_SIDE.contains(c.p().tile()), 100, 40);
                    }
                }
            }
        }
    }

    public void handleStuckBank() {
        Npc ghost = Npcs.stream().name("Ghost villager").nearest().first();
        if (ghost.valid() && ghost.reachable()) {
            Util.turnTo(ghost);
            if (ghost.interact("Talk-to")) {
                Condition.wait(() -> !c.p().tile().equals(STUCK_BANK_TILE), 250, 40);
            }
        }
    }

    public void enterBarrier() {
        GameObject barrier = Objects.stream().name("Energy Barrier").nearest().first();
        if (!barrier.valid()) {
            return;
        }
        Util.turnTo(barrier);
        state("Interacting with barrier");
        if (barrier.interact("Pass")) {
            Condition.wait(Chat::canContinue, 150, 20);
            state("Clicking continue on barrier dialog");
            if (Chat.clickContinue()) {
                state("Waiting to get inside");
                Condition.wait(() -> Players.local().tile().equals(BARRIER_TILE_WEST) || Players.local().tile().equals(BARRIER_TILE_EAST), 150, 20);
            }
        }
    }

    public void crushAllBones() {
        GEctofuntus.state("Crushing bones");
        GameObject loader = Objects.stream().name("Loader").nearest().first();
        GameObject boneGrinder = Objects.stream().name("Bone grinder").nearest().first();
        GameObject bin = Objects.stream().name("Bin").nearest().first();

        if (boneGrinder.valid()) {
            Util.turnTo(boneGrinder);
            if (GEctofuntus.needToCollectBones) {
                if (bin.interact("Empty")) {
                    GEctofuntus.needToCollectBones = false;
                    Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 14, 150, 800);
                }
            } else if (GEctofuntus.needToWindGrinder) {
                if (boneGrinder.interact("Wind")) {
                    GEctofuntus.needToWindGrinder = false;
                    Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 14, 150, 800);
                }
            } else if (GEctofuntus.needToLoadBones) {
                putBonesInLoader(loader);
            } else {
                if (boneGrinder.interact("Status")) {
                    Condition.wait(() -> GEctofuntus.needToCollectBones || GEctofuntus.needToLoadBones || GEctofuntus.needToWindGrinder, 150, 30);
                }
            }
        }
    }
}
