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
            return rand.nextInt(6) + (damage - 3);
        }

        public boolean dodge() {
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

            hp = rand.nextInt(40) + 25;

            damage = switch (type) {
                case "Boar", "Undead" -> 5;
                case "Orc" -> 7;
                default -> 9;
            };
        }

        public int attack() {
            return rand.nextInt(7) + (damage - 3);
        }

        public boolean isAlive() {
            return hp > 0;
        }

        public void takeDamage(int dmg) {
            hp -= dmg;
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

        Character character = new Character();
        Enemy enemy = new Enemy();

        String characterName = character.toString().split("\n")[0];
        String enemyName = enemy.toString().split("\n")[0];

        List<String> log = new ArrayList<>();

        log.add("FIGHT START: " + characterName + " (" + character.getHP() + "HP) vs "
                + enemyName + " (" + enemy.getHP() + "HP)");

        while (character.isAlive() && enemy.isAlive()) {

            int dmg = character.attack();
            enemy.takeDamage(dmg);
            log.add(characterName + " hits for " + dmg +
                    " | " + enemyName + " HP: " + enemy.getHP());

            if (!enemy.isAlive()) break;

            if (!character.dodge()) {
                dmg = enemy.attack();
                character.takeDamage(dmg);
                log.add(enemyName + " hits for " + dmg +
                        " | " + characterName + " HP: " + character.getHP());
            } else {
                log.add(characterName + " dodged the attack!");
            }
        }

        log.add(character.isAlive()
                ? characterName + " Wins!"
                : enemyName + " Wins!");

        exportBattleLog("FightLog", log);
        log.forEach(System.out::println);
    }

    /* ========================= TEAM FIGHT ========================= */
    private void teamFight() {

        List<Character> team = new ArrayList<>();
        for (int i = 0; i < 3; i++) team.add(new Character());

        Enemy enemy = new Enemy();
        enemy.takeDamage(-80);

        String enemyName = enemy.toString().split("\n")[0];

        // Build team names with Level and HP
        StringBuilder teamNames = new StringBuilder();
        for (int i = 0; i < team.size(); i++) {
            Character c = team.get(i);
            String name = c.toString().split("\n")[0]; // Level X Class
            teamNames.append(name)
                    .append(" (")
                    .append(c.getHP())
                    .append("HP)");
            if (i < team.size() - 1) teamNames.append(", ");
        }

        List<String> log = new ArrayList<>();
        log.add("TEAM BATTLE: " + teamNames + " vs " + enemyName +
                " (HP: " + enemy.getHP() + ")");

        while (enemy.isAlive() && !team.isEmpty()) {

            for (Iterator<Character> it = team.iterator(); it.hasNext();) {

                Character c = it.next();
                String characterName = c.toString().split("\n")[0];

                int charDmg = c.attack();
                enemy.takeDamage(charDmg);
                log.add(characterName + " hits " + enemyName +
                        " for " + charDmg + " | " + enemyName + " HP: " + enemy.getHP());

                if (!enemy.isAlive()) break;

                int enemyDmg = enemy.attack();
                c.takeDamage(enemyDmg);
                log.add(enemyName + " hits " + characterName +
                        " for " + enemyDmg + " | " + characterName + " HP: " + c.getHP());

                if (!c.isAlive()) {
                    log.add(">>> " + characterName + " HAS FALLEN! <<<");
                    it.remove();
                }
            }
        }

        log.add(enemy.isAlive()
                ? enemyName + " Wins! The NAFSQUAD was wiped out."
                : "NAFSQUAD WINS!");

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
