package GEctofuntus;

import GEctofuntus.Tasks.BuySlime.*;
import GEctofuntus.Tasks.Common.OpenBank;
import GEctofuntus.Tasks.CrushBones.CrushBones;
import GEctofuntus.Tasks.OfferBones.OfferBones;
import Util.Util;
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
    private ArrayList<Task> buySlimeTasks = new ArrayList<>();
    private ArrayList<Task> crushBonesTasks = new ArrayList<>();
    private ArrayList<Task> offerBonesTasks = new ArrayList<>();
    private final Constants c = new Constants();

    // Script vars
    public static String currentState = "null";
    public static boolean needToHop = false;
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
        if (needSlime) {
            for (Task task : buySlimeTasks) {
                if (task.activate()) {
                    task.execute();
                    if (ScriptManager.INSTANCE.isStopping()) {
                        break;
                    }
                }
            }
        }
        if (needBonemeal) {
            for (Task task : crushBonesTasks) {
                if (task.activate()) {
                    task.execute();
                    if (ScriptManager.INSTANCE.isStopping()) {
                        break;
                    }
                }
            }
        }
        if (needToOffer) {
            for (Task task : offerBonesTasks) {
                if (task.activate()) {
                    task.execute();
                    if (ScriptManager.INSTANCE.isStopping()) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        currentState = Util.state("Starting GEctofuntus - Gavin101...");
        Condition.wait(() -> Players.local().valid(), 500, 50);
        currentState = Util.state("Checking Camera");
        Util.cameraCheck();
        // Get script settings
        needSlime = getOption("Buy slime");
        needBonemeal = getOption("Crush bones");
        needToOffer = getOption("Offer bones");
        boneType = getOption("Bones");
        // Build task list
        if (needSlime) {
            buySlimeTasks.add(new GoToBank(this));
            buySlimeTasks.add(new OpenBank(this));
            buySlimeTasks.add(new BankItems(this));
            buySlimeTasks.add(new GoToCharter(this));
            buySlimeTasks.add(new TradeCharter(this));
            buySlimeTasks.add(new BuySlime(this));
            buySlimeTasks.add(new CloseStore(this));
            buySlimeTasks.add(new HopWorlds(this));
        }
//        if (needBonemeal) {
//            crushBonesTasks.add(new CrushBones(this));
//        }
//        if (needToOffer) {
//            offerBonesTasks.add(new OfferBones(this));
//        }
        createBonemealMap(boneToBonemeal);
        bonemealType = getBonemealType();
        if (java.util.Objects.equals(bonemealType, "")) {
            currentState = Util.state("Couldn't find a valid bonemeal mapping.");
            Util.endScript();
        }

        Paint paint = new PaintBuilder()
                .addString(() -> "Current State: " +currentState)
                .addString(() -> "Bones offered: " +boneCounter)
                .trackInventoryItem(Constants.ECTO_TOKEN_ID)
                .trackSkill(Skill.Prayer)
                .build();
        addPaint(paint);

        // Get counts of bones, bonemeal, empty pots and slime.
//        getItemCounts();

//        // For testing.
        bonemealCount = 500;
        slimeCount = 0;
        boneCount = 0;
    }

    public static void getItemCounts() {
        currentState = Util.state("Getting item counts");
        if (!Constants.BANK_AREA.contains(Players.local().tile())) {
            currentState = Util.state("Running to bank");
            Movement.walkTo(Constants.BANK_AREA.getRandomTile());
        }
        GameObject bankBooth = Objects.stream().name("Bank booth").nearest().first();
        if (bankBooth.valid()) {
            if (!Bank.opened()) {
                Util.turnTo(bankBooth);
                if (Bank.open()) {
                    Condition.wait(Bank::opened, 100, 20);
                    currentState = Util.state("Checking bank");
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
