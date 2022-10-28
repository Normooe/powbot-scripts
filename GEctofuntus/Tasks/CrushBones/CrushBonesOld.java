package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Constants;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class CrushBonesOld extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public CrushBonesOld(GEctofuntus main) {
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
        } else if (c.p().tile().equals(Constants.STUCK_BANK_TILE)) {
            // Webwalker gets stuck here - https://github.com/powbot/issues/issues/142
            // Can deprecate handleStuckBank() once issues are fixed.
            GEctofuntus.currentState = Util.state("Getting unstuck");
            handleStuckBank();
        } else if (!Constants.LOADER_AREA.contains(c.p().tile())) {
            // Webwalker can get stuck behind barrier inside also - https://github.com/powbot/issues/issues/141#issuecomment-1288236687
            // Can deprecate goToAltar() once issues are fixed.
            GEctofuntus.currentState = Util.state("Going to loader");
            goToAltar();
            Movement.walkTo(Constants.LOADER_AREA.getRandomTile());
        } else {
            crushAllBones();
        }
    }
    
    public void putBonesInLoader(GameObject loader) {
        GEctofuntus.currentState = Util.state("Loading bones");
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
        if (!Constants.BANK_AREA.contains(c.p().tile())) {
            GEctofuntus.currentState = Util.state("Going to bank");
            getToBank();
            Condition.wait(() -> Constants.BANK_AREA.contains(c.p().tile()), 200, 40);
        } else if (!Bank.opened()) {
            GEctofuntus.currentState = Util.state("Opening bank");
            if (Bank.open()) {
                Condition.wait(Bank::opened, 100, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.bonemealType).isNotEmpty()) {
            GEctofuntus.currentState = Util.state("Depositing items");
            if (Bank.depositInventory()) {
                Condition.wait(Inventory::isEmpty, 100, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.boneType).isEmpty()) {
            GEctofuntus.currentState = Util.state("Withdrawing items");
            if (Bank.withdraw(GEctofuntus.boneType, 14)) {
                Condition.wait(() -> Inventory.stream().name(GEctofuntus.boneType).count() == 14, 100, 20);
            }
        } else if (Inventory.stream().name("Pot").isEmpty()) {
            GEctofuntus.currentState = Util.state("Withdrawing items");
            if (Bank.withdraw("Pot", 14)) {
                Condition.wait(() -> Inventory.stream().name("Pot").count() == 14, 100, 20);
            }
        }
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

    public void goToAltar() {
        if (Constants.PORT_PHASMATYS.contains(c.p().tile())) {
            if (!c.p().tile().equals(Constants.BARRIER_TILE_EAST)) {
                Movement.walkTo(Constants.BARRIER_TILE_EAST);
            } else {
                GameObject barrier = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Energy Barrier").nearest().first();
                if (barrier.valid() && barrier.reachable()) {
                    Util.turnTo(barrier);
                    if (barrier.interact("Pass")) {
                        Condition.wait(() -> Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile()), 100, 40);
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
                Condition.wait(() -> !c.p().tile().equals(Constants.STUCK_BANK_TILE), 250, 40);
            }
        }
    }

    public void enterBarrier() {
        GameObject barrier = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Energy Barrier").nearest().first();
        if (!barrier.valid()) {
            return;
        }
        Util.turnTo(barrier);
        GEctofuntus.currentState = Util.state("Interacting with barrier");
        if (barrier.interact("Pass")) {
            Condition.wait(Chat::canContinue, 150, 20);
            GEctofuntus.currentState = Util.state("Clicking continue on barrier dialog");
            if (Chat.clickContinue()) {
                GEctofuntus.currentState = Util.state("Waiting to get inside");
                Condition.wait(() -> Players.local().tile().equals(Constants.BARRIER_TILE_WEST) || Players.local().tile().equals(Constants.BARRIER_TILE_EAST), 150, 20);
            }
        }
    }

    public void crushAllBones() {
        GEctofuntus.currentState = Util.state("Crushing bones");
        GameObject loader = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Loader").nearest().first();
        GameObject boneGrinder = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Bone grinder").nearest().first();
        GameObject bin = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Bin").nearest().first();

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
