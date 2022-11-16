package Util;

import GEctofuntus.GEctofuntus;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.powbot.mobile.script.ScriptManager;


public class Util {

    public static String state(String s) {
        System.out.println(s);
        return s;
    }

    public static void cameraCheck() {
        Game.tab(Game.Tab.SETTINGS);
        if (Camera.getZoom() > 1) {
            state("Zooming camera out");
            Camera.moveZoomSlider(0);
        }
        if (Camera.pitch() < 90) {
            state("Changing camera angle");
            Camera.pitch(true);
        }
    }

    public static void changeGameTab(Game.Tab tabToUse) {
        if (!Game.tab(tabToUse)) {
            Game.tab(tabToUse);
            Condition.wait(() -> Game.tab(tabToUse), 150, 20);
        }
    }

    public static void endScript(String exitMsg) {
        Util.state(exitMsg);
        if (Bank.opened()) {
            if (Bank.close()) {
                Condition.wait(() -> !Bank.opened(), 250, 50);
            }
        }
        if (Game.loggedIn()) {
            if (Game.logout()) {
                Condition.wait(() -> !Game.loggedIn(), 250, 50);
                ScriptManager.INSTANCE.stop();
            }
        }
    }

    public static boolean turnTo(Npc npc) {
        if (!npc.inViewport()) {
            Camera.turnTo(npc);
            Condition.wait(npc::inViewport, 150, 20);
            return true;
        }
        return false;
    }

    public static boolean turnTo(GameObject gameobject) {
        if (!gameobject.inViewport()) {
            Camera.turnTo(gameobject);
            Condition.wait(gameobject::inViewport, 150, 20);
            return true;
        }
        return false;
    }

    public static boolean turnTo(GroundItem groundItem) {
        if (!groundItem.inViewport()) {
            Camera.turnTo(groundItem);
            Condition.wait(groundItem::inViewport, 150, 20);
            return true;
        }
        return false;
    }

    public static boolean useEctophial() {
        GEctofuntus.currentState = Util.state("Using ectophial");
        Game.tab(Game.Tab.INVENTORY);
        Item ectophial = Inventory.stream().name("Ectophial").first();
        if (ectophial.valid()) {
            if (ectophial.interact("Empty")) {
                Condition.wait(() -> !ectophial.actions().contains("Empty"), 100, 40);
                return true;
            } else {
                return false;
            }
        } else {
            Util.endScript("No ectophial in inventory. Ending script");
        }
        return false;
    }
}

