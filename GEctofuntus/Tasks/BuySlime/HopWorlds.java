package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Store;
import org.powbot.api.rt4.World;
import org.powbot.api.rt4.Worlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HopWorlds extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public static int worldIndex = 0;

    public HopWorlds(GEctofuntus main) {
        super();
        super.name = "HopWorlds";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GEctofuntus.needToHop && !Store.opened();
    }

    @Override
    public void execute() {
        World hopWorld = Worlds.stream().id(Constants.WORLD_LIST[worldIndex]).first();
        if (hopWorld.getNumber() == Worlds.current().getNumber()) {
            worldIndex++;
            return;
        }
        GEctofuntus.currentState = Util.state("Hopping worlds: " +hopWorld.getNumber());
        if (hopWorld.valid()) {
            if (hopWorld.hop()) {
                Condition.wait(() -> Worlds.current().getNumber() == Constants.WORLD_LIST[worldIndex], 200, 20);
                GEctofuntus.needToHop = false;
                worldIndex++;
                if (worldIndex > Constants.WORLD_LIST.length-1) {
                    // Start at the beginning of our world list once we reach the end.
                    worldIndex = 0;
                }
            }
        }
    }
}
