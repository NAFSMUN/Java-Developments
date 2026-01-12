import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RPGGame {

    private Scanner sc = new Scanner(System.in);
    private Random rand = new Random();

    public static void main(String[] args) {
        new RPGGame().start();
    }

    /* ========================= GAME MENU ========================= */

    public void start() {
        while (true) {
            System.out.println("\n===== RPG GAME SYSTEM =====");
            System.out.println("1. Generate Character");
            System.out.println("2. Generate Enemy");
            System.out.println("3. Character vs Enemy Fight");
            System.out.println("4. NAFSQUAD vs Enemy Fight");
            System.out.println("5. Exit");
            System.out.println("==== Log Export is On ====");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> generateCharacter();
                case 2 -> generateEnemy();
                case 3 -> singleFight();
                case 4 -> teamFight();
                case 5 -> System.exit(0);
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /* ========================= CHARACTER ========================= */

    public class Character {
        private int level;
        private int hp, maxHP;
        private String charClass;
        private int damage;

        public Character() {
            String[] classes = {"Barbarian", "Wizard", "Archer", "Bard"};
            charClass = classes[rand.nextInt(classes.length)];
            level = rand.nextInt(10) + 1;

            // Adjusted: Hero HP slightly increased to make fights last longer
            maxHP = rand.nextInt(15) + 20;
            hp = maxHP;

            damage = switch (charClass) {
                case "Barbarian" -> 9;
                case "Wizard" -> 8;
                case "Archer" -> 7;
                default -> 6;
            };
        }

        public int attack() {
            // Damage is now Damage +/- 3 (Randomized range)
            return rand.nextInt(6) + (damage - 3);
        }

        public boolean dodge() {
            // 15% Dodge chance (Slightly higher)
            return rand.nextInt(100) < 15;
        }

        public boolean isAlive() {
            return hp > 0;
        }

        public void takeDamage(int dmg) {
            hp = Math.max(0, hp - dmg);
        }

        public int getHP() {
            return hp;
        }

        @Override
        public String toString() {
            return "Level " + level + " " + charClass +
                    "\nHP: " + hp + "/" + maxHP +
                    "\nDMG: " + damage;
        }
    }

    /* ========================= ENEMY ========================= */

    public class Enemy {
        private int level;
        private int hp;
        private String type;
        private int damage;

        public Enemy() {
            String[] types = {"Boar", "Orc", "Undead", "Demon"};
            type = types[rand.nextInt(types.length)];
            level = rand.nextInt(10) + 1;

            // SIGNIFICANT CHANGE:
            // Enemy HP is now much wider range (25 to 65).
            // High rolls make them very hard to beat.
            hp = rand.nextInt(40) + 25;

            damage = switch (type) {
                case "Boar", "Undead" -> 5;
                case "Orc" -> 7;
                default -> 9;
            };
        }

        public int attack() {
            // CHANGE: Enemy damage is now randomized!
            // Range: base damage +/- 3. Unpredictable.
            return rand.nextInt(7) + (damage - 3);
        }

        public boolean isAlive() {
            return hp > 0;
        }

        public void takeDamage(int dmg) {
            // Math.max handles standard damage.
            // Negative damage (logic trick) increases HP (used for Bosses).
            hp = hp - dmg;
            if (hp < 0) hp = 0;
        }

        public int getHP() {
            return hp;
        }

        @Override
        public String toString() {
            return type + " (Lvl " + level + ")" +
                    "\nHP: " + hp +
                    "\nDMG: " + damage;
        }
    }

    /* ========================= FEATURES ========================= */

    private void generateCharacter() {
        Character c = new Character();
        exportCharacter(c);
        System.out.println(c);
    }

    private void generateEnemy() {
        Enemy e = new Enemy();
        System.out.println(e);
    }

    /* ========================= 1v1 FIGHT ========================= */

    private void singleFight() {
        Character c = new Character();
        Enemy e = new Enemy();
        List<String> log = new ArrayList<>();

        log.add("FIGHT START: Hero (" + c.getHP() + "HP) vs Enemy (" + e.getHP() + "HP)");

        while (c.isAlive() && e.isAlive()) {
            int dmg = c.attack();
            e.takeDamage(dmg);
            log.add("Hero hits for " + dmg + " | Enemy HP: " + e.getHP());

            if (!e.isAlive()) break;

            if (!c.dodge()) {
                dmg = e.attack(); // Now randomized
                c.takeDamage(dmg);
                log.add("Enemy hits for " + dmg + " | Hero HP: " + c.getHP());
            } else {
                log.add("Hero dodged the attack!");
            }
        }

        log.add(c.isAlive() ? "Hero Wins!" : "Enemy Wins!");
        exportBattleLog("FightLog", log);
        log.forEach(System.out::println);
    }

    /* ========================= TEAM FIGHT ========================= */

    private void teamFight() {
        List<Character> team = new ArrayList<>();
        for (int i = 0; i < 3; i++) team.add(new Character());

        Enemy enemy = new Enemy();

        // BALANCING HACK:
        // 1 Enemy vs 3 Heroes is unfair for the Enemy.
        // We "Heal" (buff) the enemy by passing negative damage to simulate a Boss Fight.
        // This gives the enemy +80 Extra HP so it can survive the team.
        enemy.takeDamage(-80);

        List<String> log = new ArrayList<>();
        log.add("TEAM BATTLE: 3 Heroes vs Boss " + enemy.toString().split("\n")[0] + " (HP: " + enemy.getHP() + ")");

        while (enemy.isAlive() && !team.isEmpty()) {
            for (Iterator<Character> it = team.iterator(); it.hasNext();) {
                Character c = it.next();

                // Hero attacks Enemy
                int heroDmg = c.attack();
                enemy.takeDamage(heroDmg);
                log.add("Hero hits Boss for " + heroDmg + " | Boss HP: " + enemy.getHP());

                if (!enemy.isAlive()) break;

                // Enemy Retaliates (Hits the hero back)
                int enemyDmg = enemy.attack();
                c.takeDamage(enemyDmg);
                log.add("Boss hits Hero for " + enemyDmg + " | Hero HP: " + c.getHP());

                if (!c.isAlive()) {
                    log.add(">>> A HERO HAS DIED! <<<");
                    it.remove();
                }
            }
        }

        log.add(enemy.isAlive() ? "BOSS WINS! The NAFSQUAD was wiped out." : "NAFSQUAD WINS!");
        exportBattleLog("TeamFight", log);
        log.forEach(System.out::println);
    }

    /* ========================= FILE EXPORT ========================= */

    private String timestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    private void exportBattleLog(String name, List<String> log) {
        String fileName = name + "_" + timestamp() + ".txt";
        try (PrintWriter pw = new PrintWriter(fileName)) {
            for (String s : log) pw.println(s);
        } catch (Exception e) {
            System.out.println("Battle log export failed!");
        }
    }

    private void exportCharacter(Character c) {
        String fileName = "CharacterLog_" + timestamp() + ".txt";
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println(c);
        } catch (Exception e) {
            System.out.println("Character log export failed!");
        }
    }
}