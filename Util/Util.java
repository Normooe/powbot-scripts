package Util;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.powbot.mobile.script.ScriptManager;


public class Util {

    public static String state(String s) {
        System.out.println(s);
        return s;
    }

    public static void cameraCheck() {
//        System.out.println("Zoom: " +Camera.getZoom());
        if (Camera.getZoom() >= 10) {
            state("Zooming camera out");
            Camera.moveZoomSlider(9);
        }
        if (Camera.pitch() < 90) {
            state("Changing camera angle");
            Camera.pitch(true);
        }
    }

    public static void changeGameTab(Game.Tab tabToUse) {
        if (!Game.tab(tabToUse)) {
            Game.tab(tabToUse);
            Condition.wait(() -> Game.tab(tabToUse), 100, 10);
        }
    }

    public static void endScript(String exitMsg) {
        Util.state(exitMsg);
        if (Game.logout()) {
            Condition.wait(() -> !Game.loggedIn(), 500, 20);
            ScriptManager.INSTANCE.stop();
        }
    }

    public static void turnTo(Npc npc) {
        if (!npc.inViewport()) {
            Camera.turnTo(npc);
            Condition.wait(npc::inViewport, 100, 10);
        }
    }

    public static void turnTo(GameObject gameobject) {
        if (!gameobject.inViewport()) {
            Camera.turnTo(gameobject);
            Condition.wait(gameobject::inViewport, 100, 10);
        }
    }

    public static void turnTo(GroundItem groundItem) {
        if (!groundItem.inViewport()) {
            Camera.turnTo(groundItem);
            Condition.wait(groundItem::inViewport, 100, 10);
        }
    }
}

