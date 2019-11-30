package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import static byow.Core.Generator._RANDOM;

public class Engine {
    private static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 35;
    private static final int MENUW = 80;
    private static final int MENUH = 30;
    private static long SEED;
    private static boolean gameOver;
    private static int level;
    private static int gold;
    private static boolean bossisdead;
    private static boolean haveitem;
    private static int health;
    private static int denerocount = 6;
    private static int frenkelcount = 3;
    private static boolean lang;
    private static String keystyped = "";
    private static String specialkeys = "";
    private static String sk;
    private static boolean replayed = false;

    private static long getStringtoNum(String str) {
        str = str.trim();
        String str2 = "";
        if (!"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 = str2 + str.charAt(i);
                }
            }
        }
        return Long.parseLong(str2);
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {

        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        /*
        int index = 0;
        for (int i = 1; i < input.size() - 1; i++) {
            char c = input.charat(i);
            index = index * 10 + (int) c;
        }

        Generator.randomgenerator(finalWorldFrame, index;);
        */
        health = 5;
        level = 2;
        gold = 3;
        bossisdead = false;
        haveitem = false;
        lang = true;
        TETile[][] result = playanothergame(input);
        return result;
    }

    private static void helper(World world) {
        for (int i = 0; i < keystyped.length(); i++) {
            if (keystyped.charAt(i) == 'w') {
                world.world()[world.player().x][world.player().y + 1] = Tileset.AVATAR;
                world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            }
            if (keystyped.charAt(i) == 's') {
                world.world()[world.player().x][world.player().y - 1] = Tileset.AVATAR;
                world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            }
            if (keystyped.charAt(i) == 'a') {
                world.world()[world.player().x - 1][world.player().y] = Tileset.AVATAR;
                world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            }
            if (keystyped.charAt(i) == 'w') {
                world.world()[world.player().x + 1][world.player().y] = Tileset.AVATAR;
                world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            }
        }
    }

    private static TETile[][] playanothergame(String input) {
        switch (input.charAt(0)) {
            case ('n'):
            case ('N'): {
                SEED = getStringtoNum(input);
                TETile[][] newworld = Generator.randomgenerator(SEED);
                World world = new World(Generator.lockdoor(newworld),
                        Generator.player(newworld), new Position(0, 0),
                        null, null, newworld);

                int start = 1;
                for (int i = 0; i < input.length(); i += 1) {
                    if (input.charAt(i) == 's' || input.charAt(i) == 'S') {
                        start = i + 1;
                        break;
                    }
                }
                for (int i = start; i < input.length(); i += 1) {
                    keystyped += input.charAt(i);
                }
                helper(world);
                if (keystyped.length() >= 2) {
                    if ((keystyped.charAt(keystyped.length() - 2) == ':'
                            && keystyped.charAt(keystyped.length() - 1) == 'q')
                            || (keystyped.charAt(keystyped.length() - 2) == ':'
                            && keystyped.charAt(keystyped.length() - 1) == 'Q')) {
                        saveseed();
                        savekeys();
                        return world.world();
                    }
                }

                return world.world();
            }
            case ('l'):
            case ('L'): {
                //load game.
                SEED = loadseed();
                keystyped = loadkeys();
                TETile[][] newworld = Generator.randomgenerator(SEED);
                World world = new World(Generator.lockdoor(newworld),
                        Generator.player(newworld), new Position(0, 0),
                        null, null, newworld);
                helper(world);
                keystyped = "";
                int start = 1;
                for (int i = 0; i < input.length(); i += 1) {
                    if (input.charAt(i) == 's' || input.charAt(i) == 'S') {
                        start = i + 1;
                        break;
                    }
                }
                for (int i = start; i < input.length(); i += 1) {
                    keystyped += input.charAt(i);
                }
                helper(world);
                for (int i = start; i < input.length(); i += 1) {
                    if ((input.charAt(i) == ':' && input.charAt(i + 1) == 'q')
                            || (input.charAt(i) == ':' && input.charAt(i + 1) == 'Q')) {
                        gameOver = true;
                        saveseed();
                        savekeys();
                        break;
                    }
                }
                return world.world();
            }
            case ('q'):
            case ('Q'): {
                return emptyworld();
            } default: {
                return emptyworld();
            }
        }
    }

    private static TETile[][] emptyworld() {
        gameOver = true;
        TETile[][] world = new TETile[80][30];
        for (TETile[] x : world) {
            for (TETile y : x) {
                y = Tileset.NOTHING;
            }
        }
        return world;
    }


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public static void interactWithKeyboardini() {
        lang = true;
        initialize();
        menu();
        health = 5;
        level = 2;
        gold = 5;
        bossisdead = false;
        haveitem = false;
    }

    public static void interactWithKeyboardcasen() {
        String sd = "";
        String trueSeed = "";
        char c = 'l';
        StdDraw.clear(Color.BLACK);
        minimenu();
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 10,
                    "Now please enter a seed, then press 'S' to start the game.");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 10,
                    "随便输点数字，然后狠狠砸下 S");
        }
        StdDraw.show();
        do {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            c = StdDraw.nextKeyTyped();
            if (c >= 48 && c <= 57) {
                trueSeed += String.valueOf(c);
            }
            sd += String.valueOf(c);
            if (c != 's' && c != 'S') {
                StdDraw.clear(Color.BLACK);
                minimenu();
                if (lang) {
                    StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "Your seed is: " + sd);
                } else {
                    StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "你的密码: " + sd);
                }
                StdDraw.show();
            }
        } while (c != 's' && c != 'S');
        SEED = getStringtoNum(trueSeed);
        StdDraw.pause(500);
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = Generator.randomgenerator(SEED);
        World wd = new World(Generator.lockdoor(world),
                Generator.player(world), Generator.boss(world),
                Generator.denero(world), Generator.frenkel(world), world);
        ter.renderFrame(world);
        playGame(wd);
    }

    public static void interactWithKeyboardcasel() {
        SEED  = loadseed();
        keystyped = loadkeys();
        specialkeys = loadspecialkeys();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = Generator.randomgenerator(SEED);
        World wd = new World(Generator.lockdoor(world),
                Generator.player(world), Generator.boss(world),
                Generator.denero(world), Generator.frenkel(world), world);
        ter.renderFrame(world);
        sk = specialkeys;
        for (int i = 0; i < keystyped.length(); i++) {
            if (keystyped.charAt(i) == ':') {
                continue;
            }
            wd = move(wd, keystyped.charAt(i), sk);
            if (denerocount < 2) {
                Generator.denero(wd.world());
                denerocount = 6;
                ter.initialize(WIDTH, HEIGHT);
                if (lang) {
                    drawFrame("You did a good job, but... MORE DENERO!!!!!!");
                } else {
                    drawFrame("Denero不是万能的，"
                            + "但没有Denero是万万不能的");
                }
                StdDraw.pause(1500);
                ter.initialize(WIDTH, HEIGHT);
            }
        }
        replayed = true;
        playGame(wd);
    }

    public static void interactWithKeyboard() {
        interactWithKeyboardini();
        String s;
        StdDraw.enableDoubleBuffering();
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "Enter your choice please: ");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "请输入选项： ");
        }
        StdDraw.show();
        while (true) {
            s = "";
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            s += key;
            StdDraw.enableDoubleBuffering();
            StdDraw.clear(Color.BLACK);
            menu();
            if (lang) {
                StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "Enter your choice please: " + s);
            } else {
                StdDraw.text(MENUW / 2, MENUH * 1.5 / 10, "请输入选项: " + s);
            }
            StdDraw.show();
            switch (key) {
                case ('n'):
                case ('N'): {
                    interactWithKeyboardcasen();
                    break;
                }
                case ('l'):
                case ('L'): {
                    interactWithKeyboardcasel();
                    break;
                }
                case ('q'):
                case ('Q'): {
                    gameOver = true;
                    System.exit(0);
                    break;
                }
                case ('c'):
                case ('C'): {
                    if (lang) {
                        lang = false;
                    } else {
                        lang = true;
                    }
                    menu();
                    StdDraw.show();
                    continue;
                }
                default:
            }
        }
    }


    private static void initialize() {
        gameOver = false;

        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(MENUW * 16, MENUH * 16);
        Font font = new Font("Monaco", Font.BOLD, 100);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, MENUW);
        StdDraw.setYscale(0, MENUH);
        StdDraw.clear(Color.BLACK);
    }

    private static void menu() {
        Font title = new Font("Monaco", Font.BOLD, 50);
        Font mainMenu = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(title);
        StdDraw.picture(MENUW / 2, MENUH / 2, "images/razer3.png");
        StdDraw.setPenColor(Color.white);
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 3, "Find your way out");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 3, "四散而逃吧...凡人");
        }
        StdDraw.setFont(mainMenu);
        if (lang) {
            StdDraw.text(MENUW / 4, MENUH * 5.5 / 10, "New Game (n / N)");
            StdDraw.text(MENUW * 3 / 4, MENUH * 5.5 / 10, "Load Game (l / L)");
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 10, "Quit (q / Q)");
            StdDraw.text(MENUW * 5 / 6, MENUH * 0.5 / 10, "Change language (c / C)");
        } else {
            StdDraw.text(MENUW / 4, MENUH * 5.5 / 10, "新游戏 (n / N)");
            StdDraw.text(MENUW * 3 / 4, MENUH * 5.5 / 10, "加载游戏 (l / L)");
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 10, "退出游戏 (q / Q)");
            StdDraw.text(MENUW * 5 / 6, MENUH * 0.5 / 10, "更换语言 (c / C)");
        }
    }

    private static void minimenu() {
        Font title = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(title);
        StdDraw.picture(MENUW / 2, MENUH / 2, "images/razer3.png");
        StdDraw.setPenColor(Color.white);
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 3, "Find your way out");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 2.5 / 3, "四散而逃吧...凡人");
        }
        Font mainMenu = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(mainMenu);
    }


    private static int showfight() {
        int index = _RANDOM.nextInt(5) + 1;
        Font title = new Font("Monaco", Font.BOLD, 40);
        Font fight = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.white);
        StdDraw.picture(MENUW / 2, MENUH * 6 / 7, "images/bigdenero.jpg");
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 3, "A LV" + Integer.toString(index)
                    + " " + adj() + " Denero appears!");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 3, "一只 " + Integer.toString(index)
                    + "级 " + adj() + " Denero 出现啦!");
        }
        StdDraw.setFont(fight);
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1 / 3, "Fight With Him! (f / F)");
            StdDraw.text(MENUW / 2, MENUH * 0.5 / 3, "Run Away! (r / R)");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1 / 3, "刚他! (f / F)");
            StdDraw.text(MENUW / 2, MENUH * 0.5 / 3, "溜了溜了! (r / R)");
        }
        //ter.initialize(WIDTH, HEIGHT);
        return index;
    }

    private static int showboss() {
        int index = _RANDOM.nextInt(5) + 1;
        Font title = new Font("Monaco", Font.BOLD, 40);
        Font fight = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.white);
        StdDraw.picture(MENUW / 2, MENUH * 6 / 7, "images/bigboss.jpg");
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 3, "FACE YOURSELF! "
                    + "IT'S the Devil Inside Your Heart!!");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.5 / 3,
                    "你为什么要唤醒我！！直面你的心魔吧！！");
        }
        StdDraw.setFont(fight);
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.2 / 3, "Fight With Him! (f / F)");
            StdDraw.text(MENUW / 2, MENUH * 0.9 / 3, "Run Away! (r / R)");
            StdDraw.text(MENUW / 2, MENUH * 0.6 / 3, "Bet Your Luck! (b / B)");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.2 / 3, "刚他! (f / F)");
            StdDraw.text(MENUW / 2, MENUH * 0.9 / 3, "溜了溜了! (r / R)");
            StdDraw.text(MENUW / 2, MENUH * 0.6 / 3, "随缘吧! (b / B)");
        }
        return index;
    }

    private static void bosscasef() {
        if (level >= 10) {
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("YOU GOT IT!! YOU ARE A REAL HERO.... at least for yourself");
            } else {
                drawFrame("你赢了，但这个世界在劫难逃...");
            }
            level = 99999;
            gold = 99999;
            bossisdead = true;
            StdDraw.pause(3000);
            ter.initialize(WIDTH, HEIGHT);

        } else if (haveitem) {
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("WOW! THAT'S WHAT HAIR RESTORER CAN DO! "
                        + "YOU ARE YOUR HERO.... at least for your hair");
            } else {
                drawFrame("生发剂真是太好了，"
                        + "你做到了...至少不变成光头");
            }
            level = 99999;
            gold = 99999;
            bossisdead = true;
            StdDraw.pause(3000);
            ter.initialize(WIDTH, HEIGHT);
        } else {
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("You are so weak..... Congratulations! You lose!!");
            } else {
                drawFrame("您可真弱啊！");
            }
            health = 0;
            gold = 0;
            StdDraw.pause(3000);
            ter.initialize(WIDTH, HEIGHT);
        }
    }

    private static void boss(char special) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        showboss();
        StdDraw.show();
        char key;
        if (special == '.') {
            while (!StdDraw.hasNextKeyTyped()) {
                String a;
            }
            key = StdDraw.nextKeyTyped();
            while (key != 'f' && key != 'F' && key != 'R'
                    && key != 'r' && key != 'B' && key != 'b') {
                while (!StdDraw.hasNextKeyTyped()) {
                    String a;
                }
                key = StdDraw.nextKeyTyped();
            }
            specialkeys += key;
        } else {
            key = special;
        }

        switch (key) {
            case ('f'):
            case ('F'): {
                bosscasef();
                return;
            }
            case ('r'):
            case ('R'): {
                ter.initialize(WIDTH, HEIGHT);
                if (lang) {
                    drawFrame("WHY R U NOT PREPARED!!! YOU LOST THE GAME!");
                } else {
                    drawFrame("你打游戏像蔡徐坤");
                }
                health = 0;
                gold = 0;
                StdDraw.pause(3000);
                ter.initialize(WIDTH, HEIGHT);
                return;
            }
            case ('b'):
            case ('B'): {
                int index = _RANDOM.nextInt(10);
                if (index <= 3) {
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("YOUR ARE LUCKY THIS TIME.... BUT NOT FOREVER!!");
                    } else {
                        drawFrame("这次你很幸运...你可"
                                + "别叫我给逮到了，有你好果汁吃！");
                    }
                    gold = 99999;
                    level = 99999;
                    bossisdead = true;
                    StdDraw.pause(3000);
                    ter.initialize(WIDTH, HEIGHT);

                } else {
                    ter.initialize(WIDTH, HEIGHT);
                    Font title = new Font("Monaco", Font.BOLD, 55);
                    StdDraw.setFont(title);
                    StdDraw.setPenColor(Color.white);
                    StdDraw.text(MENUW / 2, MENUH * 1 / 2, "死");
                    StdDraw.show();
                    StdDraw.pause(3000);
                    health = 0;
                    gold = 0;
                    ter.initialize(WIDTH, HEIGHT);
                }
                ter.initialize(WIDTH, HEIGHT);
                return;
            }
            default:
                return;
        }
    }

    private static String adj() {
        Random r = new Random(SEED);
        int index = r.nextInt(6);
        if (lang) {
            switch (index) {
                case (0):
                    return "strong";
                case (1):
                    return "happy";
                case (2):
                    return "friendly";
                case (3):
                    return "humrous";
                case (4):
                    return "shaco";
                case (5):
                    return "delicious";
                case (6):
                    return "rough";
                default:
                    return "sad";
            }
        } else {
            switch (index) {
                case (0):
                    return "牛逼的";
                case (1):
                    return "哈哈哈哈哈哈的";
                case (2):
                    return "萌萌的";
                case (3):
                    return "默默无闻的";
                case (4):
                    return "小丑";
                case (5):
                    return "好吃的";
                case (6):
                    return "哇哦的";
                default:
                    return "伤心的";
            }
        }
    }

    private static void playGame(World world) {
        char key;
        while (!gameOver) {
            mousePointer(world.world());
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            key = StdDraw.nextKeyTyped();
            keystyped += key;
            //System.out.println(keystyped);
            int d = denerocount;
            int f = frenkelcount;
            world = move(world, key, "");
            if (denerocount != d || f != frenkelcount) {
                ter.initialize(WIDTH, HEIGHT);
            }
            if (health <= 0) {
                gameOver = true;
                health = 5;
                ter.initialize(WIDTH, HEIGHT);
                drawFrame("Sorry! You lose!");
                StdDraw.pause(5000);
                System.exit(0);
            }
            /*else if ((world.world()[world.player().x][world.player().y].equals(Tileset
                    .LOCKED_DOOR)) && health >= 1 && bossisdead) {
                gameOver = true;
                health = 5;
                ter.initialize(WIDTH, HEIGHT);
                drawFrame("Congratulation! You win!");
                StdDraw.pause(5000);
                System.exit(0);
            }*/
            if (keystyped.length() >= 2) {
                if ((keystyped.charAt(keystyped.length() - 2) == ':'
                        && keystyped.charAt(keystyped.length() - 1) == 'q')
                        || (keystyped.charAt(keystyped.length() - 2) == ':'
                        && keystyped.charAt(keystyped.length() - 1) == 'Q')) {
                    saveseed();
                    savekeys();
                    savespecialkeys();
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("Your game has been saved!");
                    } else {
                        drawFrame("游戏已保存！");
                    }
                    StdDraw.pause(2000);
                    gameOver = true;
                    System.exit(0);
                }
            }

            if (gameOver) {
                health = 5;
                ter.initialize(WIDTH, HEIGHT);
                if (lang) {
                    drawFrame("Congratulation! You win!");
                } else {
                    drawFrame("你赢了...但这个世界在劫难逃!");
                }
                StdDraw.pause(3000);
                System.exit(0);
            }
            if (denerocount < 2) {
                Generator.denero(world.world());
                denerocount = 6;
                ter.initialize(WIDTH, HEIGHT);
                if (lang) {
                    drawFrame("You did a good job, but... MORE DENERO!!!!!!");
                } else {
                    drawFrame("Denero不是万能的，但没有Denero是万万不能的");
                }
                StdDraw.pause(1500);
                ter.initialize(WIDTH, HEIGHT);
            }
        }
        ter.initialize(WIDTH, HEIGHT);
    }

    private static void mousePointerhelper() {
        if (lang) {
            StdDraw.text(WIDTH * 1 / 4, HEIGHT - 2,
                    "Your Health : " + Integer.toString(health));
            StdDraw.text(WIDTH * 2 / 4, HEIGHT - 2,
                    "Your Level : " + Integer.toString(level));
            StdDraw.text(WIDTH * 3 / 4, HEIGHT - 2,
                    "Your Gold : " + Integer.toString(gold));
        } else {
            StdDraw.text(WIDTH * 1 / 4, HEIGHT - 2,
                    "生命值 : " + Integer.toString(health));
            StdDraw.text(WIDTH * 2 / 4, HEIGHT - 2,
                    "等级 : " + Integer.toString(level));
            StdDraw.text(WIDTH * 3 / 4, HEIGHT - 2,
                    "金钱 : " + Integer.toString(gold));
        }
        StdDraw.show();
    }

    //Enables the mouse to read what's on the screen
    private static void mousePointer(TETile[][] world) {
        mousePointerhelper();
        int mx = (int) StdDraw.mouseX();
        int my = (int) StdDraw.mouseY();
        if (mx <= 0 || mx >= 80 || my >= 30  || my <= 0) {
            ter.renderFrame(world);
            return;
        }
        StdDraw.enableDoubleBuffering();
        if (world[mx][my].equals(Tileset.LOCKED_DOOR)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "This is a door "
                        + "where you can escape from this crazy world!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "这是个门 "
                        + "用来跑的");
            }
        } else if (world[mx][my].equals(Tileset.WALL)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "This is a wall where you can't go!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "这是墙 别撞");
            }
        } else  if (world[mx][my].equals(Tileset.AVATAR)) {
            //StdDraw.enableDoubleBuffering();
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "You, the player!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "这是你");
            }
        } else if (world[mx][my].equals(Tileset.FLOOR)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "Floor!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "地板");
            }
        } else if (world[mx][my].equals(Tileset.DENERO)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "MONSTER DENERO!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "Denero大魔王");
            }
        } else if (world[mx][my].equals(Tileset.FRENKEL)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "FRENKEL! YOU CAN BUY ITEMS FROM HIM!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "贪婪商人Frenkel");
            }
        } else if (world[mx][my].equals(Tileset.BOSS)) {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "The BOSS!"
                        + " Don't Fight Him Without Enough Prep");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "别碰我");
            }
        } else {
            ter.renderFrame(world);
            StdDraw.setPenColor(Color.white);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "Nothing!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "啥都没有你瞅啥玩意");
            }
        }
        StdDraw.show();
    }

    private static void fight(char special) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        int lv = showfight();
        StdDraw.show();
        char key;
        if (special == '.') {
            while (!StdDraw.hasNextKeyTyped()) {
                String a;
            }
            key = StdDraw.nextKeyTyped();
            while (key != 'f' && key != 'F' && key != 'R' && key != 'r') {
                while (!StdDraw.hasNextKeyTyped()) {
                    String a;
                }
                key = StdDraw.nextKeyTyped();
            }
            specialkeys += key;
        } else {
            key = special;
        }

        ter.initialize(WIDTH, HEIGHT);
        switch (key) {
            case ('f'):
            case ('F'): {

                if (level >= lv) {
                    if (lang) {
                        drawFrame("You Won the Fight and Got Some Gold!");
                        StdDraw.pause(1500);
                        drawFrame("Health Recovered!");
                    } else {
                        drawFrame("你赢了，恢复所有生命值");
                    }
                    health = 5;
                    level += 1;
                    gold += lv;
                    StdDraw.pause(1500);
                    ter.initialize(WIDTH, HEIGHT);
                } else {
                    if (lang) {
                        drawFrame("You Lost the Fight and Lost Three Health!");
                    } else {
                        drawFrame("你输了，还失去了三点生命值");
                    }
                    health -= 2;
                    StdDraw.pause(1500);
                    ter.initialize(WIDTH, HEIGHT);
                }
                return;
            }
            case ('r'):
            case ('R'): {
                if (lang) {
                    drawFrame("You Ran Away from the Fight and Lost One Health!");
                } else {
                    drawFrame("你溜了，但是丢了一点生命值");
                }
                health -= 1;
                if (gold < 0) {
                    gold = 0;
                }
                StdDraw.pause(1500);
                ter.initialize(WIDTH, HEIGHT);
                return;
            }
            default:
                return;
        }


    }

    private static void showbuy() {
        Font title = new Font("Monaco", Font.BOLD, 40);
        Font buy = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.white);
        StdDraw.picture(MENUW / 2, MENUH * 6 / 7, "images/bigfrenkel.jpg");
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.6 / 3,
                    "WELCOME! YOU ONLY HAVE ONE CHANCE TO BUY ONE ITEM!");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.6 / 3, "你有钱就是我的兄弟，至少这一次是");
        }
        StdDraw.setFont(buy);
        if (lang) {
            StdDraw.text(MENUW / 2, MENUH * 1.2 / 3, "Level UP $3 (l / L)");
            StdDraw.text(MENUW / 2, MENUH * 0.8 / 3, "Recover Health $2 (r / R)");
            StdDraw.text(MENUW / 2, MENUH * 0.4 / 3, "Hair Restorer $15 (h / H)");
        } else {
            StdDraw.text(MENUW / 2, MENUH * 1.2 / 3, "等级提升 $3 (l / L)");
            StdDraw.text(MENUW / 2, MENUH * 0.8 / 3, "神圣药水 $2 (r / R)");
            StdDraw.text(MENUW / 2, MENUH * 0.4 / 3, "药效不明的生发剂 $15 (h / H)");
        }
        StdDraw.show();
    }

    private static void buyhelper() {
        if (gold >= 3) {
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("LEVEL UP!!!");
            } else {
                drawFrame("等级提升！但这并没有什么用");
            }
            level += 1;
            gold -= 3;
            StdDraw.pause(1500);

        } else {
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("OMG! YOU DO NOT HAVE ENOUGH MONEY!");
            } else {
                drawFrame("穷鬼！快走！比如打扰我做生意！");
            }
            StdDraw.pause(1500);
        }

    }

    private static void buy(char special) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        showbuy();
        char key;
        if (special == '.') {
            while (!StdDraw.hasNextKeyTyped()) {
                key = special;
            }
            key = StdDraw.nextKeyTyped();
            while (key != 'l' && key != 'L' && key != 'R'
                    && key != 'r' && key != 'h' && key != 'H') {
                while (!StdDraw.hasNextKeyTyped()) {
                    key = key;
                }
                key = StdDraw.nextKeyTyped();
            }
            specialkeys += key;
        } else {
            key = special;
        }
        switch (key) {
            case ('l'):
            case ('L'): {
                buyhelper();
                return;
            }
            case ('r'):
            case ('R'): {

                if (gold >= 2) {
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("HEALTH RECOVERED!");
                    } else {
                        drawFrame("你喝了一瓶味道极其古怪的药水，这使你充满了决心");
                    }
                    health = 5;
                    gold -= 2;
                    StdDraw.pause(1500);

                } else {
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("OMG! YOU DO NOT HAVE ENOUGH MONEY!");
                    } else {
                        drawFrame("穷鬼！快走！别打扰我做生意！");
                    }
                    StdDraw.pause(1500);
                }
                return;
            }
            case ('h'):
            case ('H'): {
                if (gold >= 15) {
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("You Buy a Hair Restorer.... But what it can do???");
                    } else {
                        drawFrame("你买了瓶生发剂...但是这有啥用呢？？");
                    }
                    gold -= 15;
                    haveitem = true;
                    StdDraw.pause(1500);

                } else {
                    ter.initialize(WIDTH, HEIGHT);
                    if (lang) {
                        drawFrame("OMG! YOU DO NOT HAVE ENOUGH MONEY!");
                    } else {
                        drawFrame("穷鬼！快走！比如打扰我做生意！");
                    }
                    StdDraw.pause(1500);
                }
                return;
            }
            default:
                return;
        }
    }

    private static void movewall(World world) {
        health -= 1;
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world.world());
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.white);
        Font f = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(f);
        if (lang) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Health Deducted! Don't hit the wall!");
        } else {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "撞墙了蠢货，你掉血了！");
        }
        StdDraw.show();
        StdDraw.pause(1000);
        Font original = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(original);
        ter.initialize(WIDTH, HEIGHT);
    }

    private static World movedoor(World world) {
        if (bossisdead && health >= 1) {
            gameOver = true;
            health = 5;
            ter.initialize(WIDTH, HEIGHT);
            if (lang) {
                drawFrame("Congratulation! You win!");
            } else {
                drawFrame("你赢了...但这个世界在劫难逃");
            }
            StdDraw.pause(5000);
            System.exit(0);
        } else {
            health -= 1;
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world.world());
            StdDraw.enableDoubleBuffering();
            StdDraw.setPenColor(Color.white);
            Font f = new Font("Monaco", Font.BOLD, 50);
            StdDraw.setFont(f);
            if (lang) {
                StdDraw.text(WIDTH / 2, HEIGHT / 2,
                        "You Gotta Fight the Boss First!! ");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "谁给你的勇气现在就开门？梁静茹么？ ");
            }
            StdDraw.show();
            StdDraw.pause(1000);
            Font original = new Font("Monaco", Font.BOLD, 16);
            StdDraw.setFont(original);
            return world;
        }
        return world;
    }

    private static World movecasew(World world, char key, String skey) {
        TETile upper = world.world()[world.player().x][world.player().y + 1];
        if (upper.equals(Tileset.WALL)) {
            movewall(world);
            return world;
        } else if (upper.equals(Tileset.LOCKED_DOOR)) {
            return movedoor(world);
        } else if (upper.equals(Tileset.DENERO)) {

            if (!skey.isEmpty()) {
                fight(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                fight('.');
            }
            world.world()[world.player().x][world.player().y + 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y + 1);
            denerocount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (upper.equals(Tileset.FRENKEL)) {
            if (!skey.isEmpty()) {
                buy(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                buy('.');
            }
            world.world()[world.player().x][world.player().y + 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y + 1);
            frenkelcount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (upper.equals(Tileset.BOSS)) {
            if (!skey.isEmpty()) {
                boss(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                boss('.');
            }
            world.world()[world.player().x][world.player().y + 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y + 1);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else {
            world.world()[world.player().x][world.player().y + 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y + 1);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        }
    }

    private static World movecases(World world, char key, String skey) {
        TETile lower = world.world()[world.player().x][world.player().y - 1];
        if (lower.equals(Tileset.WALL)) {
            movewall(world);
            return world;
        } else if (lower.equals(Tileset.LOCKED_DOOR)) {
            return movedoor(world);
        } else if (lower.equals(Tileset.DENERO)) {
            if (!skey.isEmpty()) {
                fight(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                fight('.');
            }
            world.world()[world.player().x][world.player().y - 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y - 1);
            denerocount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (lower.equals(Tileset.FRENKEL)) {
            if (!skey.isEmpty()) {
                buy(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                buy('.');
            }
            world.world()[world.player().x][world.player().y - 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y - 1);
            frenkelcount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (lower.equals(Tileset.BOSS)) {
            if (!skey.isEmpty()) {
                boss(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                boss('.');
            }
            world.world()[world.player().x][world.player().y - 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y - 1);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else {
            world.world()[world.player().x][world.player().y - 1] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x, world.player().y - 1);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        }
    }

    private static World movecasea(World world, char key, String skey) {
        TETile left = world.world()[world.player().x - 1][world.player().y];
        if (left.equals(Tileset.WALL)) {
            movewall(world);
            return world;
        } else if (left.equals(Tileset.LOCKED_DOOR)) {
            return movedoor(world);
        } else if (left.equals(Tileset.DENERO)) {
            if (!skey.isEmpty()) {
                fight(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                fight('.');
            }
            world.world()[world.player().x - 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x - 1, world.player().y);
            denerocount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (left.equals(Tileset.FRENKEL)) {
            if (!skey.isEmpty()) {
                buy(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                buy('.');
            }
            world.world()[world.player().x - 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x - 1, world.player().y);
            frenkelcount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (left.equals(Tileset.BOSS)) {
            if (!skey.isEmpty()) {
                boss(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                boss('.');
            }
            world.world()[world.player().x - 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x - 1, world.player().y);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else {
            world.world()[world.player().x - 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x - 1, world.player().y);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        }
    }

    private static World movecased(World world, char key, String skey) {
        TETile right = world.world()[world.player().x + 1][world.player().y];
        if (right.equals(Tileset.WALL)) {
            movewall(world);
            return world;
        } else if (right.equals(Tileset.LOCKED_DOOR)) {
            return movedoor(world);
        } else if (right.equals(Tileset.DENERO)) {
            if (!skey.isEmpty()) {
                fight(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                fight('.');
            }
            world.world()[world.player().x + 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x + 1, world.player().y);
            denerocount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (right.equals(Tileset.FRENKEL)) {
            if (!skey.isEmpty()) {
                buy(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                buy('.');
            }
            world.world()[world.player().x + 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x + 1, world.player().y);
            frenkelcount--;
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else if (right.equals(Tileset.BOSS)) {
            if (!skey.isEmpty()) {
                boss(skey.charAt(0));
                sk = skey.substring(1);
            } else {
                boss('.');
            }
            world.world()[world.player().x + 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x + 1, world.player().y);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        } else {
            world.world()[world.player().x + 1][world.player().y] = Tileset.AVATAR;
            world.world()[world.player().x][world.player().y] = Tileset.FLOOR;
            Position newPlayer = new Position(world.player().x + 1, world.player().y);
            return new World(world.lockedDoor(), newPlayer, world.boss(),
                    world.denero(), world.frenkel(), world.world());
        }
    }

    private static World move(World world, char key, String skey) {
        TETile upper = world.world()[world.player().x][world.player().y + 1];
        TETile lower = world.world()[world.player().x][world.player().y - 1];
        TETile right = world.world()[world.player().x + 1][world.player().y];
        TETile left = world.world()[world.player().x - 1][world.player().y];
        switch (key) {
            case ('w'):
            case ('W'): {
                return movecasew(world, key, skey);
            }
            case ('s'):
            case ('S'): {
                return movecases(world, key, skey);
            }
            case ('a'):
            case ('A'): {
                return movecasea(world, key, skey);
            }
            case ('d'):
            case ('D'): {
                return movecased(world, key, skey);
            } default:
                ter.initialize(WIDTH, HEIGHT);
                ter.renderFrame(world.world());
                return world;
        }
    }

    private static void saveseed() {
        File f = new File("./seed.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(SEED);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private static void savekeys() {
        File f = new File("./keys.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(keystyped);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private static void savespecialkeys() {
        File f = new File("./specialkeys.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(specialkeys);
            //System.out.println(specialkeys);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public static void drawFrame(String str) {
        int midWidth = MENUW / 2;
        int midHeight = MENUH / 2;
        StdDraw.clear();
        StdDraw.clear(Color.black);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, midHeight, str);
        StdDraw.show();
    }

    private static long loadseed() {
        File f = new File("./seed.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (long) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
            /* In the case no World has been saved yet, we return a new one. */
        long result = 355201314;
        return result;
    }

    private static String loadkeys() {
        File f = new File("./keys.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (String) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        /* In the case no World has been saved yet, we return a new one. */
        return "no keys typed";
    }

    private static String loadspecialkeys() {
        File f = new File("./specialkeys.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (String) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        /* In the case no World has been saved yet, we return a new one. */
        return "no keys typed";
    }



/*
    public static void main(String[] args) {


        TERenderer myworld = new TERenderer();
        myworld.initialize(WIDTH, HEIGHT);

        TETile[][] world;
        world = interactWithInputString("n123sdssssssdd");
        myworld.renderFrame(world);


        interactWithKeyboard();
        //interactWithInputString("n7313251667695476404sasdw");

    }
*/


}
