package GEctofuntus.Tasks.CrushBones;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Camera;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Objects;

public class GetGrinderStatus extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public GetGrinderStatus(GEctofuntus main) {
        super();
        super.name = "GetGrinderStatus";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return !GEctofuntus.needToCollectBones
                && !GEctofuntus.needToLoadBones
                && !GEctofuntus.needToWindGrinder
                && Constants.ALTAR_TOP_FLOOR.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Getting grinder status");
        GameObject boneGrinder = Objects.stream(10).type(GameObject.Type.INTERACTIVE).name("Bone grinder").nearest().first();
        if (!boneGrinder.inViewport()) {
            Camera.turnTo(boneGrinder);
            Condition.wait(boneGrinder::inViewport, 150, 20);
        } else if (boneGrinder.valid() && boneGrinder.interact("Status")) {
            Condition.wait(() -> GEctofuntus.needToCollectBones || GEctofuntus.needToLoadBones || GEctofuntus.needToWindGrinder, 150, 30);
        }
    }
}
