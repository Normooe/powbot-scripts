package GEctofuntus.Tasks.Common;

import GEctofuntus.Constants;
import GEctofuntus.GEctofuntus;
import GEctofuntus.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.stream.item.InventoryItemStream;
import org.powbot.api.rt4.stream.item.ItemStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BankItems extends Task {
    private final Constants c = new Constants();
    GEctofuntus main;

    public BankItems(GEctofuntus main) {
        super();
        super.name = "BankItems";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return Bank.opened();
    }

    @Override
    public void execute() {
        GEctofuntus.currentState = Util.state("Banking items: "+GEctofuntus.currentTask);
        bankItems(GEctofuntus.requiredItems);
        if (Objects.equals(GEctofuntus.currentTask, "BuySlime")) {
            GEctofuntus.slimeCount = Bank.stream().name("Bucket of slime").first().stackSize() + Inventory.stream().name("Bucket of slime").count();
            if (GEctofuntus.slimeCount >= (GEctofuntus.boneCount + GEctofuntus.bonemealCount)) {
                System.out.println("We have enough slime. Setting needSlime = false");
                GEctofuntus.needSlime = false;
            }
        } else if (Objects.equals(GEctofuntus.currentTask, "CrushBones")) {
            GEctofuntus.bonemealCount = Bank.stream().name(GEctofuntus.bonemealType).first().stackSize() + Inventory.stream().name(GEctofuntus.bonemealType).count();
            if (GEctofuntus.bonemealCount >= GEctofuntus.slimeCount) {
                System.out.println("We have enough bonemeal. Setting needBonemeal = false");
                GEctofuntus.needBonemeal = false;
            }
        } else if (Objects.equals(GEctofuntus.currentTask, "OfferBones")) {
            GEctofuntus.slimeCount = Bank.stream().name("Bucket of slime").first().stackSize();
            GEctofuntus.bonemealCount = Bank.stream().name(GEctofuntus.bonemealType).first().stackSize();
            if (GEctofuntus.bonemealCount < 13) {
                Util.endScript("Less than 13 bonemeal left. Ending script");
            } else if (GEctofuntus.slimeCount < 13) {
                Util.endScript("Less than 13 buckets of slime left. Ending script");
            }
        }
        bankItems(GEctofuntus.requiredItems);
    }

    public void bankItems(Map<String, Integer> requiredItems) {
        System.out.println("Required Items: " +requiredItems);
        depositAnyExcept(requiredItems);
        GEctofuntus.currentState = Util.state("Withdrawing items");
        for (Map.Entry<String,Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int numOfItem = entry.getValue();
            // Withdraw needed item
            long invyItemCount = Inventory.stream().name(itemName).count();
            if (invyItemCount < numOfItem && Inventory.stream().name(itemName).first().stackSize() < numOfItem) {
                System.out.println(invyItemCount +" is less than required number: " +numOfItem);
                int amountToWithdraw = (int) (numOfItem - invyItemCount);
                if (Bank.stream().name(itemName).first().stackSize() >= amountToWithdraw) {
                    System.out.println("Withdrawing "+amountToWithdraw +" " +itemName);
                    if (Bank.withdraw(itemName, amountToWithdraw)) {
                        Condition.wait(() -> Inventory.stream().name(itemName).count() == numOfItem
                                ||  Inventory.stream().name(itemName).first().stackSize() == numOfItem, 150, 20);
                    }
                } else {
                    if (Bank.opened()) {
                        if (Bank.close()) {
                            Condition.wait(() -> !Bank.opened(), 150, 30);
                            Util.endScript("Couldn't find " +amountToWithdraw +" of required item: " +itemName +",ending script..");
                        }
                    }
                }
            }
        }
    }

    public void depositAnyExcept(Map<String, Integer> requiredItems) {
        GEctofuntus.currentState = Util.state("Depositing items");
        Item[] items = Inventory.items();
        for (Item item : items) {
            if (!requiredItems.containsKey(item.name())) {
                if (item.interact("Deposit-All")) {
                    Condition.wait(() -> Inventory.stream().name(item.name()).isEmpty(), 150, 20);
                }
            }
        }
    }

    public void slimeBanking() {
        GEctofuntus.slimeCount = Bank.stream().name("Bucket of slime").first().stackSize() + Inventory.stream().name("Bucket of slime").count();
        if (GEctofuntus.slimeCount >= (GEctofuntus.boneCount + GEctofuntus.bonemealCount)) {
            System.out.println("We have enough slime. Setting needSlime = false");
            GEctofuntus.needSlime = false;
            return;
        }
        Item coinsInvy = Inventory.stream().name("Coins").first();
        Item coinsBank = Bank.stream().name("Coins").first();
        if (coinsInvy.stackSize() < 1000 & coinsBank.stackSize() < 1000) {
            Util.endScript("No coins in inventory or bank to buy slime, ending script.");
        }
        if (Inventory.occupiedSlotCount() > 1) {
            if (Bank.depositAllExcept("Coins")) {
                Condition.wait(() -> Inventory.stream().count() == 1, 100, 20);
            }
        }
        if (coinsInvy.valid() && coinsInvy.stackSize() > 1000) {
            if (Bank.depositAllExcept("Coins")) {
                Condition.wait(() -> Inventory.stream().count() == 1, 100, 20);
            }
        } else if (coinsBank.stackSize() > 1000) {
            if (Bank.withdraw("Coins", coinsBank.stackSize())) {
                Condition.wait(() -> Inventory.stream().name("Coins").first().stackSize() > 1000, 150, 20);
            }
        } else {
            Util.endScript("No coins in inventory or bank to buy slime, ending script.");
        }
    }

    public void bonemealBanking() {
        GEctofuntus.bonemealCount = Bank.stream().name(GEctofuntus.bonemealType).first().stackSize() + Inventory.stream().name(GEctofuntus.bonemealType).count();
        if (GEctofuntus.bonemealCount >= GEctofuntus.slimeCount) {
            System.out.println("We have enough bonemeal. Setting needBonemeal = false");
            GEctofuntus.needBonemeal = false;
            return;
        }
        if (Inventory.stream().name(GEctofuntus.boneType).count() == 13)
        if (Inventory.stream().name(GEctofuntus.bonemealType).isNotEmpty()) {
            if (Bank.depositAllExcept("Ectophial")) {
                Condition.wait(() -> Inventory.emptySlotCount() == 27, 150, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.boneType).isEmpty()) {
            if (Bank.stream().name(GEctofuntus.boneType).first().stackSize() >= 13) {
                if (Bank.withdraw(GEctofuntus.boneType, 13)) {
                    Condition.wait(() -> Inventory.stream().name(GEctofuntus.boneType).count() == 13, 150, 20);
                }
            } else {
                Util.endScript("No bones in inventory or bank to crush, ending script.");
            }
        } else if (Inventory.stream().name("Pot").isEmpty()) {
            if (Bank.stream().name("Pot").first().stackSize() >= 13) {
                if (Bank.withdraw("Pot", 13)) {
                    Condition.wait(() -> Inventory.stream().name("Pot").count() == 13, 150, 20);
                }
            } else {
                Util.endScript("No pots in inventory or bank to crush, ending script.");
            }
        }
    }
    
    public static void offerBonesBanking() {
        GEctofuntus.slimeCount = Bank.stream().name("Bucket of slime").first().stackSize();
        GEctofuntus.bonemealCount = Bank.stream().name(GEctofuntus.bonemealType).first().stackSize();
        if (GEctofuntus.bonemealCount < 13) {
            Util.endScript("Less than 13 bonemeal left. Ending script");
        } else if (GEctofuntus.slimeCount < 13) {
            Util.endScript("Less than 13 buckets of slime left. Ending script");
        }
        if (Inventory.stream().name("Bucket").isNotEmpty() || Inventory.stream().name("Pot").isNotEmpty()) {
            if (Bank.depositAllExcept("Ectophial")) {
                Condition.wait(() -> Inventory.emptySlotCount() == 27, 150, 20);
            }
        } else if (Inventory.stream().name(GEctofuntus.bonemealType).isEmpty()) {
            if (Bank.withdraw(GEctofuntus.bonemealType, 13)) {
                Condition.wait(() -> Inventory.stream().name(GEctofuntus.bonemealType).count() == 13, 150, 20);
            }
        } else if (Inventory.stream().name("Bucket of slime").isEmpty()) {
            if (Bank.withdraw("Bucket of slime", 13)) {
                Condition.wait(() -> Inventory.stream().name("Bucket of slime").count() == 13, 150, 20);
            }
        }
    }
}
