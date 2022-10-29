package GEctofuntus.Tasks.OfferBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class CollectTokens extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public CollectTokens(GEctofuntus main) {
        super();
        super.name = "CollectTokens";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GEctofuntus.needToCollectTokens;
    }

    @Override
    public void execute() {
        collectTokens();
    }

    public void collectTokens() {
        Npc ghost = Npcs.stream().within(10).name("Ghost disciple").nearest().first();
        if (!ghost.valid()) {
            return;
        }
        // Drop an item to make room for ecto-tokens if needed
        if (Inventory.isFull() && Inventory.stream().name("Ecto-token").isEmpty()) {
            GEctofuntus.currentState = Util.state("Dropping an item for ecto-tokens");
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
            GEctofuntus.currentState = Util.state("Talking to ghost");
            if (ghost.interact("Talk-to")) {
                Condition.wait(() -> Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid(), 200, 40);
            }
        } else if (Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid()) {
            GEctofuntus.currentState = Util.state("Continuing chat");
            if (Chat.completeChat()) {
                Condition.wait(() -> !Chat.chatting(), 200, 20);
                GEctofuntus.needToCollectTokens = false;
            }
        }
    }
}
