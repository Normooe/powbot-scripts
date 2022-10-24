package GEctofuntus;

import GEctofuntus.Tasks.BuySlime;
import GEctofuntus.Tasks.CrushBones;
import GEctofuntus.Tasks.OfferBones;
import com.android.tools.r8.graph.I;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Condition;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.AbstractScript;
import org.powbot.api.script.OptionType;
import org.powbot.api.script.ScriptConfiguration;
import org.powbot.api.script.ScriptManifest;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;

import java.util.ArrayList;
import java.util.HashMap;

import static GEctofuntus.Constants.bankArea;

@ScriptManifest(
        name = "GEctofuntus",
        description = "Purchases slime, crushes bones, and prays at Ectofuntus.",
        version = "0.0.1",
        author = "Gavin101"
)

@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "Buy slime",
                        description = "Buys slime from crewtraders in Port Phasmatys.",
                        optionType = OptionType.BOOLEAN,
                        defaultValue = "true"
                ),
                @ScriptConfiguration(
                        name = "Crush bones",
                        description = "Crushes bones upstairs at the ectofuntus for bonemeal.",
                        optionType = OptionType.BOOLEAN,
                        defaultValue = "true"
                ),
                @ScriptConfiguration(
                        name = "Bones",
                        description = "Type of bones to use",
                        optionType = OptionType.STRING,
                        defaultValue = "Dragon bones",
                        allowedValues = {"Big bones", "Dragon bones", "Lava dragon bones", "Wyvern bones", "Superior dragon bones"}
                ),
                @ScriptConfiguration(
                        name = "Offer bones",
                        description = "Offer bones + slime to the altar.",
                        optionType = OptionType.BOOLEAN,
                        defaultValue = "true"
                )
        }
)

public class GEctofuntus extends AbstractScript {
    private ArrayList<Task> taskList = new ArrayList<Task>();
    private Constants c = new Constants();

    // Script vars
    public static String currentState = "null";
    public static boolean needSlime;
    public static boolean needBonemeal;
    public static boolean needToOffer;
    public static String boneType;
    public static int boneCounter;
    public static int boneCount;
    public static int slimeCount;
    public static String bonemealType;
    public static int bonemealCount;
    public static int potCount;
    public static HashMap<String, String> boneToBonemeal = new HashMap<>();

    // bone grinder status vars
    public static boolean needToLoadBones;
    public static boolean needToWindGrinder;
    public static boolean needToCollectBones;
    public static boolean needToCollectTokens = false;


    // Script uploader
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("GEctofuntus", "main", "127.0.0.1:5585", true, true);
    }

    @Override
    public void poll() {
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
                if (ScriptManager.INSTANCE.isStopping()) {
                    break;
                }
            }
        }
    }

    @Override
    public void onStart() {
        state("Starting GEctofuntus - Gavin101...");
        Condition.wait(() -> Players.local().valid(), 500, 50);
        state("Checking Camera");
        Util.cameraCheck();
        // Get script settings
        needSlime = getOption("Buy slime");
        needBonemeal = getOption("Crush bones");
        needToOffer = getOption("Offer bones");
        boneType = getOption("Bones");
        // Build task list
        if (needSlime) {
            taskList.add(new BuySlime(this));
        }
        if (needBonemeal) {
            taskList.add(new CrushBones(this));
        }
        if (needToOffer) {
            taskList.add(new OfferBones(this));
        }
        createBonemealMap(boneToBonemeal);
        bonemealType = getBonemealType();
        if (java.util.Objects.equals(bonemealType, "")) {
            state("Couldn't find a valid bonemeal mapping.");
            Util.endScript();
        }

        Paint paint = new PaintBuilder()
                .addString(() -> "Current State: " +currentState)
                .addString(() -> "Bones offered: " +boneCounter)
                .trackInventoryItem(Constants.ECTO_TOKEN_ID)
                .trackSkill(Skill.Prayer)
                .build();
        addPaint(paint);

        // Get counts of bones, bonemeal, empty pots and slime
        getItemCounts();
    }

    public static void state(String s) {
        currentState = s;
        System.out.println(s);
    }

    public static void getItemCounts() {
        state("Getting item counts");
        if (!bankArea.contains(Players.local().tile())) {
            GEctofuntus.state("Running to bank");
            if (Movement.walkTo(bankArea.getRandomTile())) {
                Condition.wait(() -> bankArea.contains(Players.local().tile()), 200, 20);
            }
        }
        GameObject bankBooth = Objects.stream().name("Bank booth").nearest().first();
        if (bankBooth.valid()) {
            if (!Bank.opened()) {
                Util.turnTo(bankBooth);
                if (Bank.open()) {
                    Condition.wait(Bank::opened, 100, 20);
                    GEctofuntus.state("Checking bank");
                    boneCount = Bank.stream().name(boneType).first().stackSize();
                    bonemealCount = Bank.stream().name(bonemealType).first().stackSize();
                    slimeCount = Bank.stream().name("Bucket of slime").first().stackSize();
                    potCount = Bank.stream().name("Pot").first().stackSize();
                    System.out.println("Bone count: " +boneCount);
                    System.out.println("Bonemeal count: " +bonemealCount);
                    System.out.println("Slime count: " +slimeCount);
                    System.out.println("Pot count: " +potCount);
                }
            }
        }
    }

    public static void createBonemealMap(HashMap<String, String> bonemealMap) {
        // "Big bones", "Dragon bones", "Lava dragon bones", "Wyvern bones", "Superior dragon bones"
        bonemealMap.put("Big bones", "Big bonemeal");
        bonemealMap.put("Dragon bones", "Dragon bonemeal");
        bonemealMap.put("Lava dragon bones", "Lava dragon bonemeal");
        bonemealMap.put("Wyvern bones", "Wyvern bonemeal");
        bonemealMap.put("Superior dragon bones", "Superior dragon bonemeal");
    }

    public static String getBonemealType() {
        for (HashMap.Entry<String,String> boneMapping : boneToBonemeal.entrySet()) {
            if (java.util.Objects.equals(boneMapping.getKey(), boneType)) {
                return boneMapping.getValue();
            }
        }
        return "";
    }

    @Subscribe
    public void onMessage(MessageEvent e) {
        String text = e.getMessage();
        if (text.contains("The grinder is empty")) {
            needToLoadBones = true;
        } else if (text.contains("There are already")|| text.contains("There are some crushed")) {
            needToCollectBones = true;
        } else if (text.contains("in the hopper")) {
            needToWindGrinder = true;
        }
    }
}
