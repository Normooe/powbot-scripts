package Public.GCastleWars.Tasks.PlayCastleWars.GetIntoGame;

import Public.GCastleWars.Constants;
import Public.GCastleWars.GCastleWars;
import Public.GCastleWars.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Chat;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;

public class AcceptGameInvite extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public AcceptGameInvite(GCastleWars main) {
        super();
        super.name = "AcceptGameInvite";
        this.main = main;
    }
    @Override
    public boolean activate() {
        Component chat = Components.stream().widget(Constants.FREE_SPACE_WIDGET_ID)
                .textContains("There's a free space").viewable().first();
        return chat.valid();
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Accepting early game invite");
        if (Chat.continueChat("Yes please!")) {
            Condition.wait(() -> !Chat.chatting(), 50, 50);
        }
    }
}
