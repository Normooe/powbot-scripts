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
        if (Camera.getZoom() > 1) {
            state("Zooming camera out");
            if (Game.tab(Game.Tab.SETTINGS)) {
                Camera.moveZoomSlider(0);
            }
            if (Camera.pitch() < 90) {
                state("Changing camera angle");
                Camera.pitch(true);
            }
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
            }
        }
        ScriptManager.INSTANCE.stop();
    }

    public static boolean useEctophial() {
        GEctofuntus.currentState = Util.state("Using ectophial");
        Game.tab(Game.Tab.INVENTORY);
        Item ectophial = Inventory.stream().name("Ectophial").first();
        if (ectophial.valid() && ectophial.interact("Empty") && Condition.wait(() -> !ectophial.actions().contains("Empty"), 100, 40)) {
            return true;
        } else {
            Util.endScript("No ectophial in inventory. Ending script");
            return false;
        }
    }
}

