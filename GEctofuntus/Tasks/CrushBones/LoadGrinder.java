package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class LoadGrinder extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public LoadGrinder(GEctofuntus main) {
        super();
        super.name = "LoadGrinder";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return GEctofuntus.needToLoadBones
                && Constants.ALTAR_TOP_FLOOR.contains(c.p().tile())
                && Inventory.stream().name(GEctofuntus.boneType).isNotEmpty();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Loading bones");
        Game.tab(Game.Tab.INVENTORY);
        Item bones = Inventory.stream().name(GEctofuntus.boneType).first();
        if (bones.valid()) {
            if (!Inventory.selectedItem().name().equals(GEctofuntus.boneType)) {
                if (bones.interact("Use")) {
                    Condition.wait(() -> Inventory.selectedItem().name().equals(GEctofuntus.boneType), 150, 20);
                }
            } else {
                GameObject loader = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Loader").nearest().first();
                if (loader.valid()) {
                    Util.turnTo(loader);
                    if (loader.interact("Use", false)) {
                        GEctofuntus.needToLoadBones = false;
                        Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 13, 150, 800);
                    }
                }
            }
        }
    }
}
