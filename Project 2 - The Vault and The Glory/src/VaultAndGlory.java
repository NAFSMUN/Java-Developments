
import java.util.*; // Imported core Java utilities (List, Map, Collections, etc.)


// ENUM: Rarity

// Represents item rarity levels
enum Rarity {
    COMMON,       // Lowest rarity
    RARE,         // Medium rarity
    LEGENDARY     // Highest rarity
}

// ABSTRACT CLASS: Item

// Base abstraction for all item types
abstract class Item {

    // Encapsulated fields
    private String name;        // Item name
    private double value;       // Gold value of the item
    private Rarity rarity;      // Rarity of the item

    // Constructor for initializing item data
    public Item(String name, double value, Rarity rarity) {
        this.name = name;       // Set item name
        this.value = value;     // Set item value
        this.rarity = rarity;   // Set item rarity
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for value
    public double getValue() {
        return value;
    }

    // Getter for rarity
    public Rarity getRarity() {
        return rarity;
    }

    // Abstract behavior (forces polymorphism)
    public abstract void useItem(); // Each item defines its own usage

    // Overriding toString() for display purposes
    @Override
    public String toString() {
        return name + " (" + rarity + ")"; // Example: Excalibur (LEGENDARY)
    }
}

// -------------------------------
// CLASS: Potion
// -------------------------------
// Healing item implementation
class Potion extends Item {

    // Constructor calls parent constructor
    public Potion(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }

    // Specific use behavior
    @Override
    public void useItem() {
        System.out.println("Drank potion, +50 HP"); // Potion effect
    }
}

// -------------------------------
// CLASS: Weapon
// -------------------------------
// Weapon item implementation
class Weapon extends Item {

    // Constructor
    public Weapon(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }

    // Weapon-specific behavior
    @Override
    public void useItem() {
        System.out.println("Equipped sword, +10 Atk"); // Weapon effect
    }
}

// -------------------------------
// CLASS: Trash
// -------------------------------
// Useless item implementation
class Trash extends Item {

    // Constructor
    public Trash(String name, double value, Rarity rarity) {
        super(name, value, rarity);
    }

    // Trash cannot be used
    @Override
    public void useItem() {
        System.out.println("You cannot use this."); // Trash logic
    }
}

// -------------------------------
// CUSTOM EXCEPTION: StashFullException
// -------------------------------
class StashFullException extends Exception {

    // Custom exception message
    public StashFullException(String message) {
        super(message);
    }
}

// -------------------------------
// CLASS: Stash
// -------------------------------
// Represents player's inventory system
class Stash {

    private String owner;               // Owner of the stash
    private List<Item> slots;           // List of stored items
    private final int maxCapacity = 20; // Maximum stash capacity

    // Constructor
    public Stash(String owner) {
        this.owner = owner;              // Assign owner
        this.slots = new ArrayList<>();  // Initialize item list
    }

    // Add item to stash
    public void addItem(Item item) throws StashFullException {
        if (slots.size() >= maxCapacity) {           // Capacity check
            throw new StashFullException("Stash is full!"); // Throw custom exception
        }
        slots.add(item);                              // Add item
    }

    // Sort stash by rarity then value
    public void sortStash() {
        Collections.sort(slots, (a, b) -> {
            int rarityCompare = b.getRarity().ordinal() - a.getRarity().ordinal(); // LEGENDARY first
            if (rarityCompare == 0) {
                return Double.compare(b.getValue(), a.getValue()); // Higher value first
            }
            return rarityCompare;
        });
    }

    // Liquidate all trash items
    public double liquidateStash() {
        double gold = 0; // Total gold earned

        Iterator<Item> iterator = slots.iterator(); // Safe removal iterator
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item instanceof Trash) {             // Identify trash items
                gold += item.getValue();             // Add trash value
                iterator.remove();                   // Remove from stash
            }
        }
        return gold; // Return total gold
    }

    // Display stash contents
    public void displayStash() {
        System.out.print("Stash Content: ");
        for (int i = 0; i < slots.size(); i++) {
            System.out.print("[" + (i + 1) + "] " + slots.get(i)); // Indexed output
            if (i < slots.size() - 1) System.out.print(", ");
        }
        System.out.println();
    }
}

// -------------------------------
// CLASS: Leaderboard
// -------------------------------
// Handles ranking systems
class Leaderboard {

    private String type; // Leaderboard category name
    private LinkedHashMap<Integer, String> rankings; // Rank -> Player

    // Constructor
    public Leaderboard(String type) {
        this.type = type;                       // Set leaderboard type
        this.rankings = new LinkedHashMap<>();  // Maintain insertion order
    }

    // Update leaderboard logic
    public void updateLeaderboard(String player, int score) {
        if (rankings.size() < 10) { // If leaderboard not full
            rankings.put(rankings.size() + 1, player + " (" + score + " pts)");
        } else {
            // Replace last rank if score is high (simplified logic)
            rankings.remove(10);
            rankings.put(10, player + " (" + score + " pts)");
        }
    }

    // Display leaderboard
    public void displayTop10() {
        System.out.println("=== Leaderboard: " + type + " ===");
        for (Map.Entry<Integer, String> entry : rankings.entrySet()) {
            System.out.println("Rank " + entry.getKey() + ": " + entry.getValue());
        }
    }
}

// -------------------------------
// MAIN CLASS
// -------------------------------
public class VaultAndGlory {

    public static void main(String[] args) {

        // Create stash
        Stash stash = new Stash("Nafees");

        try {
            stash.addItem(new Weapon("Excalibur", 5000, Rarity.LEGENDARY)); // Legendary weapon
            stash.addItem(new Trash("Broken Bone", 5, Rarity.COMMON));      // Trash item
            stash.addItem(new Potion("Health Potion", 50, Rarity.RARE));   // Healing potion
        } catch (StashFullException e) {
            System.out.println(e.getMessage()); // Handle stash overflow
        }

        // Display stash
        stash.displayStash();

        // Sort stash
        System.out.println("Sorting...");
        stash.sortStash();
        stash.displayStash();

        // Liquidate trash
        double gold = stash.liquidateStash();
        System.out.println("Trash liquidated for " + gold + " gold");
        stash.displayStash();

        // Create leaderboards
        Leaderboard kills = new Leaderboard("Enemies Killed");
        Leaderboard bosses = new Leaderboard("Bosses Killed");

        // Update leaderboards
        kills.updateLeaderboard("Nafees", 9999);
        kills.updateLeaderboard("Azizul", 5000);

        bosses.updateLeaderboard("Nafees", 120);
        bosses.updateLeaderboard("Rahim", 80);

        // Display leaderboards
        kills.displayTop10();
        bosses.displayTop10();
    }
}
