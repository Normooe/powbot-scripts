package GEctofuntus.Tasks;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Constants;
import GEctofuntus.Task;
import GEctofuntus.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class OfferBones extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public OfferBones(GEctofuntus main) {
        super();
        super.name = "OfferBones";
        this.main = main;
    }
    @Override
    public boolean activate() {
        // if we have bonemeal, and we don't have any bones left, and we have enough slime to use with the bonemeal
        return GEctofuntus.bonemealCount > 0 && GEctofuntus.boneCount == 0 && GEctofuntus.slimeCount >= GEctofuntus.bonemealCount;
    }

    @Override
    public void execute() {
        if (Inventory.stream().name(GEctofuntus.bonemealType).count() == 0 || Inventory.stream().name("Bucket of slime").count() == 0 || Inventory.stream().name("Ecto-token").count() > 0) {
            GEctofuntus.state("Banking items");
            bankItems();
        } else if (c.p().tile().equals(Constants.STUCK_BANK_TILE)) {
            // Webwalker gets stuck here - https://github.com/powbot/issues/issues/142
            // Can deprecate handleStuckBank() once issues are fixed.
            GEctofuntus.state("Getting unstuck");
            handleStuckBank();
        } else if (!Constants.ALTAR_BOT_FLOOR.contains(c.p().tile())) {
            GEctofuntus.state("Going to altar");
            goToAltar();
        } else if (GEctofuntus.needToCollectTokens) {
            GEctofuntus.state("Collecting tokens");
            collectTokens();
        } else if (Widgets.widget(Constants.altarFullDialogueID).component(1).text().contains("room to put any more in")) {
            GEctofuntus.state("Setting needToCollectTokens = true");
            GEctofuntus.needToCollectTokens = true;
        } else if (!GEctofuntus.needToCollectTokens){
            GEctofuntus.state("Offering bones");
            offerBones();
        }
    }

    public void collectTokens() {
        Npc ghost = Npcs.stream().name("Ghost disciple").nearest().first();
        if (!ghost.valid()) {
            return;
        }
        // Drop an item to make room for ecto-tokens if needed
        if (Inventory.isFull() && Inventory.stream().name("Ecto-token").isEmpty()) {
            GEctofuntus.state("Dropping an item for ecto-tokens");
            if (Inventory.stream().name("Bucket").isNotEmpty()) {
                if (!Inventory.selectedItem().name().equals("Bucket")) {
                    if (Inventory.stream().name("Bucket").first().interact("Drop")) {
                        Condition.wait(() -> !Inventory.isFull(), 100, 20);
                    }
                }
            } else if (Inventory.stream().name("Bucket of slime").isNotEmpty()) {
                if (!Inventory.selectedItem().name().equals("Bucket of slime")) {
                    if (Inventory.stream().name("Bucket of slime").first().interact("Drop")) {
                        Condition.wait(() -> !Inventory.isFull(), 100, 20);
                    }
                }
            }
        }
        Util.turnTo(ghost);
        if (!Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid()) {
            GEctofuntus.state("Talking to ghost");
            if (ghost.interact("Talk-to")) {
                Condition.wait(() -> Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid(), 200, 40);
            }
        } else if (Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid()) {
            GEctofuntus.state("Continuing chat");
            if (Chat.completeChat()) {
                Condition.wait(() -> !Chat.chatting(), 200, 20);
                GEctofuntus.needToCollectTokens = false;
            }
        }
    }

    public void offerBones() {
        Game.tab(Game.Tab.INVENTORY);
        GameObject ectofuntus = Objects.stream().name("Ectofuntus").nearest().first();
        if (!ectofuntus.valid()) {
            return;
        }
        Util.turnTo(ectofuntus);
        long bucketCount = Inventory.stream().name("Bucket of slime").count();
        if (ectofuntus.interact("Worship")) {
            Condition.wait(() -> Inventory.stream().name("Bucket of slime").count() < bucketCount, 100, 30);
            GEctofuntus.boneCounter++;
        }
    }

    public void bankItems() {
        if (!Constants.BANK_AREA.contains(c.p().tile())) {
            GEctofuntus.state("Going to bank");
            getToBank();
            Condition.wait(() -> Constants.BANK_AREA.contains(c.p().tile()), 200, 40);
        } else if (!Bank.opened()) {
            GEctofuntus.state("Opening bank");
            if (Bank.open()) {
                Condition.wait(Bank::opened, 100, 20);
            }
        } else if (Inventory.stream().name("Ecto-token").isNotEmpty() || Inventory.stream().name("Pot").isNotEmpty() || Inventory.stream().name("Bucket").isNotEmpty()) {
            GEctofuntus.state("Depositing items");
            if (Bank.depositInventory()) {
                Condition.wait(Inventory::isEmpty, 100, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.bonemealType).isEmpty()) {
            GEctofuntus.state("Withdrawing items");
            if (Bank.withdraw(GEctofuntus.bonemealType, 14)) {
                Condition.wait(() -> Inventory.stream().name(GEctofuntus.boneType).count() == 14, 100, 20);
            }
        } else if (Inventory.stream().name("Bucket of slime").isEmpty()) {
            GEctofuntus.state("Withdrawing items");
            if (Bank.withdraw("Bucket of slime", 14)) {
                Condition.wait(() -> Inventory.stream().name("Bucket of slime").count() == 14, 100, 20);
            }
        }
    }

    public void getToBank() {
        if (Constants.PORT_PHASMATYS.contains(c.p().tile())) {
            if (Movement.walkTo(Constants.BANK_AREA.getRandomTile())) {
                Condition.wait(() -> Constants.BANK_AREA.contains(c.p().tile()), 200, 80);
            }
        } else if (!Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile())) {
            if (Movement.walkTo(Constants.BARRIER_ALTAR_SIDE.getRandomTile())) {
                Condition.wait(() -> c.p().tile() == Constants.BARRIER_TILE_EAST || c.p().tile() == Constants.BARRIER_TILE_WEST, 200, 40);
            }
        } else {
            enterBarrier();
        }
    }

    public void goToAltar() {
        if (Constants.PORT_PHASMATYS.contains(c.p().tile())) {
            if (!c.p().tile().equals(Constants.BARRIER_TILE_EAST)) {
                if (Movement.walkTo(Constants.BARRIER_TILE_EAST)) {
                    Condition.wait(() -> c.p().tile().equals(Constants.BARRIER_TILE_EAST), 100, 10);
                }
            } else {
                GameObject barrier = Objects.stream().name("Energy Barrier").nearest().first();
                if (barrier.valid() && barrier.reachable()) {
                    Util.turnTo(barrier);
                    if (barrier.interact("Pass")) {
                        Condition.wait(() -> Constants.BARRIER_ALTAR_SIDE.contains(c.p().tile()), 100, 40);
                    }
                }
            }
        } else {
            if (Movement.walkTo(Constants.ALTAR_BOT_FLOOR.getRandomTile())) {
                Condition.wait(() -> Constants.ALTAR_BOT_FLOOR.contains(c.p().tile()), 200, 20);
            }
        }
    }

    public void handleStuckBank() {
        Npc ghost = Npcs.stream().name("Ghost villager").nearest().first();
        if (ghost.valid() && ghost.reachable()) {
            Util.turnTo(ghost);
            if (ghost.interact("Talk-to")) {
                Condition.wait(() -> !c.p().tile().equals(Constants.STUCK_BANK_TILE), 100, 20);
            }
        }
    }

    public void enterBarrier() {
        GameObject barrier = Objects.stream().name("Energy Barrier").nearest().first();
        if (!barrier.valid()) {
            return;
        }
        Util.turnTo(barrier);
        GEctofuntus.state("Interacting with barrier");
        if (barrier.interact("Pass")) {
            Condition.wait(Chat::canContinue, 150, 20);
            GEctofuntus.state("Clicking continue on barrier dialog");
            if (Chat.clickContinue()) {
                GEctofuntus.state("Waiting to get inside");
                Condition.wait(() -> Players.local().tile().equals(Constants.BARRIER_TILE_WEST) || Players.local().tile().equals(Constants.BARRIER_TILE_EAST), 150, 20);
            }
        }
    }
}
