import java.util.*;

/* ==========================================================
   1. CORE DATA STRUCTURES (Enums & Exceptions)
   ========================================================== */

enum Rarity {
    COMMON, RARE, LEGENDARY
}

class StashFullException extends Exception {
    public StashFullException(String message) {
        super(message);
    }
}

/* ==========================================================
   2. ITEM HIERARCHY (Abstract & Concrete Classes)
   ========================================================== */

abstract class Item {
    private String name;
    private double value;
    private Rarity rarity;

    public Item(String name, double value, Rarity rarity) {
        this.name = name;
        this.value = value;
        this.rarity = rarity;
    }

    public String getName() { return name; }
    public double getValue() { return value; }
    public Rarity getRarity() { return rarity; }

    public abstract void useItem();

    @Override
    public String toString() {
        // Formats value to 2 decimal places
        return String.format("%s (%s) - $%.2f", name, rarity, value);
    }
}

class Potion extends Item {
    public Potion(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }
    @Override
    public void useItem() { System.out.println(">> Glug glug! HP Restored."); }
}

class Weapon extends Item {
    public Weapon(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }
    @Override
    public void useItem() { System.out.println(">> Swish! Attack increased."); }
}

class Trash extends Item {
    public Trash(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }
    @Override
    public void useItem() { System.out.println(">> This is useless."); }
}

/* ==========================================================
   3. SYSTEM CLASSES (Stash & Leaderboard)
   ========================================================== */

class Stash {
    private List<Item> slots;
    private final int maxCapacity = 10; // Reduced to 10 for testing full stash easier

    public Stash() {
        this.slots = new ArrayList<>();
    }

    public void addItem(Item item) throws StashFullException {
        if (slots.size() >= maxCapacity) {
            throw new StashFullException(">> Inventory Full! Sell items first.");
        }
        slots.add(item);
        System.out.println(">> Looted: " + item.getName());
    }

    public void sortStash() {
        Collections.sort(slots, (a, b) -> {
            int rarityCompare = b.getRarity().ordinal() - a.getRarity().ordinal();
            if (rarityCompare == 0) {
                return Double.compare(b.getValue(), a.getValue());
            }
            return rarityCompare;
        });
        System.out.println(">> Stash Sorted by Rarity & Value.");
    }

    public double liquidateTrash() {
        double gold = 0;
        Iterator<Item> iterator = slots.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item instanceof Trash) {
                gold += item.getValue();
                iterator.remove();
            }
        }
        return gold;
    }

    public void displayStash() {
        if (slots.isEmpty()) {
            System.out.println(">> Stash is empty.");
            return;
        }
        System.out.println("\n=== YOUR STASH (" + slots.size() + "/" + maxCapacity + ") ===");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + slots.get(i));
        }
    }

    public Item getItem(int index) {
        if(index >= 0 && index < slots.size()) return slots.get(index);
        return null;
    }

    public void removeItem(Item item) {
        slots.remove(item);
    }
}

class Leaderboard {
    private String type;
    private LinkedHashMap<String, Integer> rankings;

    public Leaderboard(String type) {
        this.type = type;
        this.rankings = new LinkedHashMap<>();
        // Mock Data
        rankings.put("Nafees", 9500);
        rankings.put("Azizul", 5000);
        rankings.put("Rahim", 2000);
    }

    public void displayTop() {
        System.out.println("\n=== " + type + " LEADERBOARD ===");
        rankings.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}

/* ==========================================================
   4. GAME ENGINE (Main Class)
   ========================================================== */

public class VaultAndGlory {

    // Instance Variables (State)
    private Scanner scanner;
    private Random random;
    private Stash myStash;
    private Leaderboard killBoard;
    private double currentGold;

    // Constructor (Initializes the game state)
    public VaultAndGlory() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.myStash = new Stash();
        this.killBoard = new Leaderboard("Global Kills");
        this.currentGold = 0.0;
    }

    // CLEAN MAIN: Only starts the application
    public static void main(String[] args) {
        VaultAndGlory game = new VaultAndGlory();
        game.start();
    }

    // MAIN GAME LOOP
    public void start() {
        boolean running = true;
        System.out.println("Welcome, Adventurer Nafees!");

        while (running) {
            printMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1 -> huntForLoot();
                case 2 -> myStash.displayStash();
                case 3 -> useItem();
                case 4 -> organizeStash();
                case 5 -> visitShop();
                case 6 -> killBoard.displayTop();
                case 7 -> {
                    running = false;
                    System.out.println("Exiting Game. Goodbye!");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // === INTERACTIVE PUBLIC METHODS ===

    public void printMenu() {
        System.out.println("\n-----------------------------");
        System.out.println("Gold: $" + String.format("%.2f", currentGold));
        System.out.println("1. Hunt (Find Random Loot)");
        System.out.println("2. Check Stash");
        System.out.println("3. Use an Item");
        System.out.println("4. Sort Stash");
        System.out.println("5. Liquidate Trash (Sell all trash)");
        System.out.println("6. View Leaderboards");
        System.out.println("7. Exit");
        System.out.print("Choose action: ");
    }

    // Generates a random item to simulate gameplay
    public void huntForLoot() {
        System.out.println("Exploring the dungeon...");
        try {
            Item loot = generateRandomItem();
            myStash.addItem(loot);
        } catch (StashFullException e) {
            System.out.println(e.getMessage());
        }
    }

    public void useItem() {
        myStash.displayStash();
        System.out.print("Enter item number to use (0 to cancel): ");
        int index = getIntInput() - 1;

        Item item = myStash.getItem(index);
        if (item != null) {
            item.useItem();
            // Consumables are removed, Weapons stay (Logic simplification: removing potions only)
            if (item instanceof Potion) {
                myStash.removeItem(item);
            }
        } else if (index != -1) {
            System.out.println("Invalid item slot.");
        }
    }

    public void organizeStash() {
        myStash.sortStash();
    }

    public void visitShop() {
        double gained = myStash.liquidateTrash();
        if (gained > 0) {
            currentGold += gained;
            System.out.println(">> Junk trader paid you $" + gained);
        } else {
            System.out.println(">> You have no trash to sell!");
        }
    }

    // === HELPER METHODS ===

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // RNG Item Generator
    private Item generateRandomItem() {
        int roll = random.nextInt(100);

        // 50% Trash, 30% Potion, 20% Weapon
        if (roll < 50) {
            return new Trash("Broken Bone", 1.50, Rarity.COMMON);
        } else if (roll < 80) {
            return new Potion("Health Elixir", 25.0, Rarity.RARE);
        } else {
            // Rare chance for Legendary
            boolean isLegendary = random.nextBoolean();
            return new Weapon(
                    isLegendary ? "Excalibur" : "Iron Sword",
                    isLegendary ? 1000 : 50,
                    isLegendary ? Rarity.LEGENDARY : Rarity.COMMON
            );
        }
    }
}