import java.util.Scanner;
import java.util.Random;
public class GameController {

    /* board reference for SaveLoadManager */
    private static MapManager boardRef;

    /* managers */
    private MapManager      mapManager;
    private BuyingManager   buyingManager;
    private AuctionManager  auctionManager;
    private TradeManager    tradeManager;
    private SaveLoadManager saveLoad;

    /* slow printer */
    private final PrintSlow printer = new PrintSlow();

    /* participants */
    private Participant[] players;

    /* console input */
    private final Scanner input = new Scanner(System.in);

    /* ------------ free-parking tax pot ------------ */
    private int freeParkingPot = 0;  // holds Income / Luxury tax until collected

    /* ------------ main entry ------------ */
    public static void main(String[] args) {
        new GameController().start();
    }

    /* ------------ helpers for other classes ------------ */
    public static MapManager getBoard()              { return boardRef; }
    public MapManager       getMapManager()          { return mapManager; }
    public BuyingManager    getBuyingManager()       { return buyingManager; }
    public Participant[]    getPlayers()             { return players; }

    /* ===================================================
     *  start()
     * =================================================== */
    public void start() {

        mapManager     = new MapManager();
        boardRef       = mapManager;
        buyingManager  = new BuyingManager();
        auctionManager = new AuctionManager();
        tradeManager   = new TradeManager();
        saveLoad       = new SaveLoadManager();

        String filePath = null;

        /* ---------------- optional load ---------------- */
        System.out.print("Load previous game? (y / n): ");
        if (input.next().equalsIgnoreCase("y")) {
            System.out.print("\nEnter save-file path: ");
            filePath = input.next();
            try {
                players = saveLoad.load(filePath);
            } catch (Exception ex) {
                System.out.println("\nSave not found – starting new game.");
            }
        }

        /* ---------------- new game ---------------- */
        if (players == null) {
            System.out.print("\nNumber of players (2-4): ");
            int n = input.nextInt();
            while (n < 2 || n > 4) {
                System.out.print("Enter 2-4: ");
                n = input.nextInt();
            }

            players = new Participant[n];
            int i = 0;
            while (i < n) {
                printer.printSlow("\nName of Player " + (i + 1) + " (no spaces): ", 30);
                players[i] = new Participant(input.next());
                i = i + 1;
            }

            printer.printSlow("\nSave-file path: ", 30);
            filePath = input.next();
        }

        saveLoad.setFile(filePath);

        gameLoop();
    }

    /* ===================================================
     *  main game loop
     * =================================================== */
    private void gameLoop() {

        boolean gameOver = false;

        while (!gameOver) {

            int idxTurn = 0;
            while (idxTurn < players.length) {

                Participant current = players[idxTurn];

                if (!current.bankrupt) {

                    /* -------- build before roll -------- */
                    printer.printSlow("\n" + current.getName()
                        + " – build before rolling (y / n)? ", 30);
                    String ans = input.next();
                    if (ans.equalsIgnoreCase("y")) {
                        listBuildOptions(current);
                        System.out.println();
                        printer.printSlow("Board location (-1 cancel): ", 30);
                        int locChoice = input.nextInt();
                        if (locChoice >= 0) {
                            attemptBuild(current, locChoice);
                        }
                    }

                    /* -------- board render -------- */
                    mapManager.render(players);

                    /* -------- roll the dice -------- */
                    printer.printSlow(current.getName() + " – Rolling Dice", 75);
                    printer.printSlowln("...", 750);

                    int d1  = 0;
                    int d2  = 0;
                    int sum = 0;

                    while (d1 == d2) {        // loop while doubles

                        d1  = roll();
                        d2  = roll();
                        sum = d1 + d2;

                        if (d1 == d2) {
                            printer.printSlowln(current.getName()
                                + " rolled a Double: " + sum, 50);
                        } else {
                            printer.printSlowln(current.getName()
                                + " rolled: " + sum, 50);
                        }

                        current.move(sum);

                        Tile landed = mapManager.getTile(current.position);
                        landed.landOn(current, this);

                        if (current.money < 0 || current.getNetWorth() <= 0) {
                            current.bankrupt = true;
                            printer.printSlowln(current.getName() + " is bankrupt!");
                        }

                        showStats();   // quick status after each move
                    }
                }
                idxTurn = idxTurn + 1;
            }

            /* -------- trade phase -------- */
            tradeManager.trade(players);

            /* -------- auto-save -------- */
            try {
                saveLoad.save(players);
                printer.printSlowln("Game auto-saved.", 50);
            } catch (Exception ex) {
                printer.printSlowln("Auto-save failed – new path? ", 50);
                saveLoad.setFile(input.next());
            }

            /* -------- win check -------- */
            int alive = 0;
            Participant lastAlive = null;
            int scan = 0;
            while (scan < players.length) {
                if (!players[scan].bankrupt) {
                    alive = alive + 1;
                    lastAlive = players[scan];
                }
                scan = scan + 1;
            }
            if (alive <= 1) {
                gameOver = true;
                if (lastAlive != null) {
                    printer.printSlowln(lastAlive.getName() + " Wins the Game!", 150);
                }
            }
        }
    }

    /* ===================================================
     *  free-parking helpers
     * =================================================== */
    public void addToPot(int amount) {
        if (amount > 0) {
            freeParkingPot = freeParkingPot + amount;
        }
    }

    public int collectPot() {
        int cash = freeParkingPot;
        freeParkingPot = 0;
        return cash;
    }

    /* ===================================================
     *  build-related helpers
     * =================================================== */
    private void listBuildOptions(Participant p) {

        printer.printSlowln("\nBuild-eligible properties:", 25);

        int loc = 0;
        while (loc < 40) {
            Property prop = p.core.getOwnedProperties().searchByLocation(loc);

            if (prop != null
                && prop.getType().equals("property")
                && !prop.isMortgaged()
                && new BuyingManager().ownsColourSet(p, prop)) {

                printer.printSlowln(
                    loc + " : " + prop.getName()
                        + "  cost $" + prop.getHouseCost()
                        + "  houses " + prop.getHouseCost(), 30);
            }
            loc = loc + 1;
        }
    }

    private void attemptBuild(Participant p, int loc) {

        Property prop = p.core.getOwnedProperties().searchByLocation(loc);

        if (prop == null) {
            printer.printSlowln("You don’t own a property at " + loc, 50);
            return;
        }
        if (prop.isMortgaged()) {
            printer.printSlowln("Property is mortgaged.", 50);
            return;
        }
        if (!new BuyingManager().ownsColourSet(p, prop)) {
            printer.printSlowln("You do not own the full set.", 50);
            return;
        }
        int cost = prop.getHouseCost();
        if (p.money < cost) {
            printer.printSlowln("Not enough cash.", 50);
            return;
        }
        boolean built = prop.buildHouse();
        if (built) {
            p.money = p.money - cost;
            printer.printSlowln("Built on "
                + prop.getName() + ". New rent $" + prop.getRent(), 50);
        } else {
            printer.printSlowln("Cannot build further on that property.");
        }
    }

    /* ===================================================
     *  simple status screen
     * =================================================== */
    private void showStats() {

        printer.printSlowln("\n-- Status --", 25);

        int idx = 0;
        while (idx < players.length) {
            Participant p = players[idx];

            printer.printSlow(p.getName()
                + " cash $" + p.money
                + " net $"  + p.getNetWorth(), 25);

            if (p.bankrupt) {
                printer.printSlowln(" [BANKRUPT]", 25);
            } else {
                System.out.println();
            }
            idx = idx + 1;
        }
        System.out.println();
    }

    /* ===================================================
     *  dice
     * =================================================== */
    public static int roll() {
        return (int)(Math.random() * 6) + 1;    //return (int)(Math.random() * 6) + 1;
    }
}