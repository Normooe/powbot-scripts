package Public.GEctofuntus.Tasks.OfferBones;

import Public.GEctofuntus.Constants;
import Public.GEctofuntus.GEctofuntus;
import Public.GEctofuntus.Task;
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
        // Drop an item to make room for ecto-tokens if needed
        if (Inventory.isFull() && Inventory.stream().name("Ecto-token").isEmpty()) {
            GEctofuntus.currentState = Util.state("Dropping an item for ecto-tokens");
            dropItem();
        } else if (!Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid()) {
            GEctofuntus.currentState = Util.state("Talking to ghost");
            if (ghost.valid()) {
                if (ghost.inViewport() && ghost.interact("Talk-to")) {
                    Condition.wait(() -> Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid(), 200, 40);
                } else {
                    System.out.println("Turning camera to ghost");
                    Camera.turnTo(ghost);
                }
            }
        } else if (Widgets.widget(Constants.GHOST_DIALOGUE_ID).valid()) {
            GEctofuntus.currentState = Util.state("Continuing chat");
            if (Chat.completeChat() && Condition.wait(() -> !Chat.chatting(), 200, 20)) {
                GEctofuntus.needToCollectTokens = false;
            }
        }
    }

    public void dropItem() {
        if (Inventory.stream().name("Bucket").isNotEmpty()) {
            GEctofuntus.currentState = Util.state("Dropping empty bucket to make room for ecto-tokens.");
            if (!Inventory.selectedItem().name().equals("Bucket")) {
                if (Inventory.stream().name("Bucket").first().interact("Drop")) {
                    Condition.wait(() -> !Inventory.isFull(), 100, 20);
                }
            }
        } else if (Inventory.stream().name("Bucket of slime").isNotEmpty()) {
            GEctofuntus.currentState = Util.state("Dropping bucket of slime to make room for ecto-tokens.");
            if (!Inventory.selectedItem().name().equals("Bucket of slime")) {
                if (Inventory.stream().name("Bucket of slime").first().interact("Drop")) {
                    Condition.wait(() -> !Inventory.isFull(), 100, 20);
                }
            }
        }
    }
}