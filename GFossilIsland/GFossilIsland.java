package GFossilIsland;

import Util.Util;

import GFossilIsland.Tasks.FarmSeaweed.LootSpore;
import GFossilIsland.Tasks.FarmSeaweed.NoteSeaweed;
import GFossilIsland.Tasks.FarmSeaweed.PickSeaweed;

import org.powbot.api.Condition;
import org.powbot.api.rt4.Players;
import org.powbot.api.script.AbstractScript;
import org.powbot.mobile.service.ScriptUploader;

import java.util.ArrayList;

public class GFossilIsland extends AbstractScript {
    private ArrayList<Task> farmSeaweedTasks = new ArrayList<>();
    private final Constants c = new Constants();
    // Script vars
    public static String currentState = "null";

    // Script uploader
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("GFossilIsland", "roary", "127.0.0.1:5615", true, true);
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
        return farmSeaweedTasks;
    }

    public void onStart() {
        currentState = Util.state("Starting GFossilIsland - Gavin101...");
        Condition.wait(() -> Players.local().valid(), 500, 50);
        currentState = Util.state("Checking Camera");
        Util.cameraCheck();

        // Farm Seaweed Tasks
        farmSeaweedTasks.add(new PickSeaweed(this));
        farmSeaweedTasks.add(new NoteSeaweed(this));
        farmSeaweedTasks.add(new LootSpore(this));
    }
}
