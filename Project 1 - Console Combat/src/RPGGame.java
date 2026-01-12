import java.util.*;              // Random, Scanner, List
import java.io.*;                // File handling

public class RPGGame {

    // Global scanner for user input
    static Scanner sc = new Scanner(System.in);

    // Global random object
    static Random rand = new Random();

    public static void main(String[] args) {

        // Game loop
        while (true) {
            System.out.println("\n===== RPG GAME SYSTEM =====");
            System.out.println("1. Generate Random Character");
            System.out.println("2. Generate Random Enemy");
            System.out.println("3. Character vs Enemy Fight");
            System.out.println("4. Team vs Enemy Fight");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> generateCharacter();
                case 2 -> generateEnemy();
                case 3 -> singleFight();
                case 4 -> teamFight();
                case 5 -> {
                    System.out.println("Exiting game...");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /* ========================= CHARACTER ========================= */

    /**
     * Base Character class
     * Demonstrates encapsulation & abstraction
     */
    static class Character {
        private int level;
        private int currentHP, maxHP;
        private int currentMana, maxMana;
        private int currentEXP, expToLevel;
        private String charClass;
        private String weapon;
        private int speed;
        private int baseDamage;

        // Constructor
        public Character(String charClass) {
            this.charClass = charClass;
            this.level = rand.nextInt(10) + 1; // Random level 1–10
            this.maxHP = rand.nextInt(6) + 15; // 15–20
            this.currentHP = maxHP;
            this.maxMana = rand.nextInt(6) + 10; // 10–15
            this.currentMana = maxMana;
            this.currentEXP = 0;
            this.expToLevel = 100;

            // Assign stats based on class
            switch (charClass) {
                case "Barbarian" -> {
                    weapon = "Sword";
                    speed = 3;
                    baseDamage = 8;
                }
                case "Wizard" -> {
                    weapon = "Staff";
                    speed = 4;
                    baseDamage = 7;
                }
                case "Archer" -> {
                    weapon = "Bow";
                    speed = 5;
                    baseDamage = 6;
                }
                case "Bard" -> {
                    weapon = "Lute";
                    speed = 4;
                    baseDamage = 5;
                }
            }
        }

        /* ================= GETTERS ================= */
        public int getHP() { return currentHP; }
        public boolean isAlive() { return currentHP > 0; }
        public String getName() { return "Level " + level + " " + charClass; }

        /* ================= COMBAT ================= */

        // Damage range: (X-3) to X
        public int attack() {
            return rand.nextInt(4) + (baseDamage - 3);
        }

        // 10% dodge chance
        public boolean dodge() {
            return rand.nextInt(100) < 10;
        }

        // 5% flee chance
        public boolean flee() {
            return rand.nextInt(100) < 5;
        }

        public void takeDamage(int dmg) {
            currentHP -= dmg;
            if (currentHP < 0) currentHP = 0;
        }

        @Override
        public String toString() {
            return getName() +
                    "\nWeapon: " + weapon +
                    "\nHP: " + currentHP + "/" + maxHP +
                    "\nMana: " + currentMana + "/" + maxMana +
                    "\nSpeed: " + speed;
        }
    }

    /* ========================= ENEMY ========================= */

    static class Enemy {
        private int level;
        private int currentHP, maxHP;
        private String type;
        private int damage;

        public Enemy() {
            level = rand.nextInt(10) + 1;
            maxHP = rand.nextInt(11) + 10; // 10–20
            currentHP = maxHP;

            String[] types = {"Boar", "Orc", "Undead", "Demon"};
            type = types[rand.nextInt(types.length)];

            damage = switch (type) {
                case "Boar" -> 3;
                case "Orc" -> 4;
                case "Undead" -> 3;
                default -> 5;
            };
        }

        public boolean isAlive() { return currentHP > 0; }
        public int attack() { return damage; }
        public void takeDamage(int dmg) { currentHP -= dmg; }

        public String getName() {
            return type + " (Lvl " + level + ")";
        }

        @Override
        public String toString() {
            return getName() + "\nHP: " + currentHP + "/" + maxHP +
                    "\nDMG: " + damage;
        }
    }

    /* ========================= FEATURES ========================= */

    // Generate and export character
    static void generateCharacter() {
        String[] classes = {"Barbarian", "Wizard", "Archer", "Bard"};
        Character c = new Character(classes[rand.nextInt(classes.length)]);
        exportToFile("Character.txt", c.toString());
        System.out.println(c);
    }

    // Generate and export enemy
    static void generateEnemy() {
        Enemy e = new Enemy();
        exportToFile("Enemy.txt", e.toString());
        System.out.println(e);
    }

    // 1v1 Fight
    static void singleFight() {
        Character c = new Character("Barbarian");
        Enemy e = new Enemy();
        List<String> log = new ArrayList<>();

        while (c.isAlive() && e.isAlive()) {
            log.add(c.getName() + " attacks " + e.getName());

            if (!e.isAlive()) break;

            int dmg = c.attack();
            e.takeDamage(dmg);
            log.add(e.getName() + " loses " + dmg + " HP");

            if (!e.isAlive()) {
                log.add(e.getName() + " has been slain");
                break;
            }

            log.add(e.getName() + " charges at " + c.getName());

            if (c.dodge()) {
                log.add("Barbarian dodges!");
                continue;
            }

            c.takeDamage(e.attack());

            if (c.flee()) {
                log.add("Barbarian flees!");
                break;
            }
        }

        log.add(c.isAlive() ? "Barbarian Wins" : "Enemy Wins");
        exportLog("FightLog.txt", log);
        log.forEach(System.out::println);
    }

    // Team vs Enemy
    static void teamFight() {
        List<Character> team = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            team.add(new Character("Archer"));

        Enemy enemy = new Enemy();
        List<String> log = new ArrayList<>();

        while (enemy.isAlive() && !team.isEmpty()) {
            for (Iterator<Character> it = team.iterator(); it.hasNext(); ) {
                Character c = it.next();

                int dmg = c.attack();
                enemy.takeDamage(dmg);
                log.add(c.getName() + " hits " + enemy.getName() + " for " + dmg);

                if (!enemy.isAlive()) break;

                if (!c.dodge()) {
                    c.takeDamage(enemy.attack());
                }

                if (!c.isAlive()) {
                    log.add(c.getName() + " has fallen");
                    it.remove();
                }
            }
        }

        log.add(enemy.isAlive() ? "Enemy Wins" : "Team Wins");
        exportLog("TeamFight.txt", log);
        log.forEach(System.out::println);
    }

    /* ========================= FILE EXPORT ========================= */

    static void exportToFile(String fileName, String data) {
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println(data);
        } catch (Exception e) {
            System.out.println("File error!");
        }
    }

    static void exportLog(String fileName, List<String> log) {
        try (PrintWriter pw = new PrintWriter(fileName)) {
            for (String s : log) pw.println(s);
        } catch (Exception e) {
            System.out.println("Log export failed!");
        }
    }
}
