package Public.GFossilIsland;

import Public.GFossilIsland.Tasks.Common.BankItems;
import Public.GFossilIsland.Tasks.Common.CloseBank;
import Public.GFossilIsland.Tasks.Common.GoToBank;
import Public.GFossilIsland.Tasks.Common.OpenBank;
import Public.GFossilIsland.Tasks.CutTrees.CutTree;
import Public.GFossilIsland.Tasks.CutTrees.GoToTrees;
import Public.GFossilIsland.Tasks.CutTrees.PickupBirdsNest;
import Util.Util;

import org.powbot.api.Condition;
import org.powbot.api.rt4.Players;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.AbstractScript;
import org.powbot.api.script.ScriptManifest;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.api.script.paint.TrackInventoryOption;
import org.powbot.mobile.service.ScriptUploader;

import java.util.ArrayList;

@ScriptManifest(
        name = "GFossilIsland",
        description = "Does multiple fossil island activities (simultaneously.)",
        version = "0.0.1",
        author = "Gavin101"
)

public class GFossilIsland extends AbstractScript {
    private ArrayList<Task> farmSeaweedTasks = new ArrayList<>();
    private ArrayList<Task> cutTreesTasks = new ArrayList<>();
    private final Constants c = new Constants();
    // Script vars
    public static String currentState = "null";

    // Script uploader
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("GFossilIsland", "roary gim", "127.0.0.1:5615", true, false);
    }
    @Override
    public void poll() {
        for (Task task : getTaskList()) {
            if (task.activate()) {
                task.execute();
            }
        }
    }

    public ArrayList<Task> getTaskList() {
        return cutTreesTasks;
    }

    public void onStart() {
        currentState = Util.state("Starting Public.GFossilIsland - Gavin101...");
        Condition.wait(() -> Players.local().valid(), 500, 50);
        currentState = Util.state("Checking Camera");
        Util.cameraCheck();

        // Farm Seaweed Tasks
//        farmSeaweedTasks.add(new PickSeaweed(this));
//        farmSeaweedTasks.add(new NoteSeaweed(this));
//        farmSeaweedTasks.add(new LootSpore(this));
        // Cut Trees Tasks
        cutTreesTasks.add(new PickupBirdsNest(this));
        cutTreesTasks.add(new CutTree(this));
        cutTreesTasks.add(new GoToTrees(this));
        cutTreesTasks.add(new GoToBank(this));
        cutTreesTasks.add(new OpenBank(this));
        cutTreesTasks.add(new BankItems(this));
        cutTreesTasks.add(new CloseBank(this));


        Paint paint = new PaintBuilder()
                .addString(() -> "Current State: "      +currentState)
                .trackSkill(Skill.Woodcutting)
                .trackInventoryItem(Constants.TEAK_LOG_ID, "Teak Logs", TrackInventoryOption.QuantityChange)
                .build();
        addPaint(paint);
    }
}
