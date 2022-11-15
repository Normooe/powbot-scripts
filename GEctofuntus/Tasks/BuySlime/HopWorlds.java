package GEctofuntus.Tasks.BuySlime;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Game;
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
//        World world = Worlds.stream().filtered(w->w.getNumber() > Worlds.current().getNumber()
//                && w.type().equals(World.Type.MEMBERS)
//                && !w.type().equals(World.Type.PVP)
//                && !w.specialty().equals(World.Specialty.PVP)
//                && !w.specialty().equals(World.Specialty.HIGH_RISK)
//                && !w.specialty().equals(World.Specialty.BOUNTY_HUNTER)
//                && !w.specialty().equals(World.Specialty.DEAD_MAN)
//                && !w.specialty().equals(World.Specialty.LEAGUE)
//                && !w.specialty().equals(World.Specialty.TWISTED_LEAGUE)
//                && !w.specialty().equals(World.Specialty.SKILL_REQUIREMENT)).first();
//        Game.tab(Game.Tab.LOGOUT);
//        if (world.valid()) {
//            GEctofuntus.currentState = Util.state("Hopping worlds: " +world.getNumber());
//            if (world.hop()) {
//                Condition.wait(() -> Worlds.current().getNumber() == world.getNumber(), 200, 30);
//                GEctofuntus.needToHop = false;
//            }
//        }

        World hopWorld = Worlds.stream().id(Constants.WORLD_LIST[worldIndex]).first();
        if (hopWorld.getNumber() == Worlds.current().getNumber()) {
            worldIndex++;
            return;
        }
        GEctofuntus.currentState = Util.state("Hopping worlds: " +hopWorld.getNumber());
        Game.tab(Game.Tab.LOGOUT);
        if (hopWorld.valid()) {
            System.out.println("hopWorld is valid. hopping");
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
