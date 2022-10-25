package GEctofuntus.Tasks.Common;

import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import GEctofuntus.Constants;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class GoToBank extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public GoToBank(GEctofuntus main) {
        super();
        super.name = "GoToBank";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public void execute() {
    }
}
