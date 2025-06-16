import java.util.Scanner;

/** Controls the overall Monopoly game flow. */
public class GameController {

	/* ── board reference for SaveLoadManager ── */
	private static MapManager boardRef;

	/* ── managers ── */
	private MapManager      mapManager;
	private BuyingManager   buyingManager;
	private AuctionManager  auctionManager;
	private TradeManager    tradeManager;
	private SaveLoadManager saveLoad;

	/* ── slow printer ── */
	private final PrintSlow printer = new PrintSlow();

	/* ── participants ── */
	private Participant[] players;

	/* ── console input ── */
	private final Scanner input = new Scanner(System.in);

	/* ── free-parking tax pot ── */
	private int freeParkingPot = 0;

	/* ──────────────────────────────────────────────
     Main entry
     ────────────────────────────────────────────── */
	public static void main(String[] args) { new GameController().start(); }

	/* ── helpers for other classes ── */
	public static MapManager getBoard()          { return boardRef; }
	public MapManager       getMapManager()     { return mapManager; }
	public BuyingManager    getBuyingManager()  { return buyingManager; }
	public Participant[]    getPlayers()        { return players; }

	/* ═════════════════════════════════════════════
     START – new or load
     ═════════════════════════════════════════════ */
	public void start() {

		/* build managers / board */
		mapManager     = new MapManager();
		boardRef       = mapManager;
		buyingManager  = new BuyingManager();
		auctionManager = new AuctionManager();
		tradeManager   = new TradeManager();
		saveLoad       = new SaveLoadManager();

		String filePath = null;

		/* ── optional load ── */
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

		/* ── create new game if necessary ── */
		if (players == null) {
			System.out.print("\nNumber of players (2-4): ");
			int n = input.nextInt();
			while (n < 2 || n > 4) {
				System.out.print("Enter 2-4: ");
				n = input.nextInt();
			}

			players = new Participant[n];
			for (int i = 0; i < n; i++) {
				printer.printSlow("\nName of Player " + (i + 1) + " (no spaces): ", 30);
				players[i] = new Participant(input.next());
			}

			printer.printSlow("\nSave-file path: ", 30);
			filePath = input.next();
		}

		saveLoad.setFile(filePath);
		gameLoop();
	}

	/* ═════════════════════════════════════════════
     MAIN GAME LOOP
     ═════════════════════════════════════════════ */
	private void gameLoop() {

		boolean gameOver = false;

		while (!gameOver) {

			for (Participant current : players) {

				if (current.bankrupt) continue;

				/* ── ★ NEW: Optional detailed stats ── */
				printer.printSlow("\n" + current.getName()
					+ " – view detailed stats (y / n)? ", 30);
				if (input.next().equalsIgnoreCase("y")) {
					showDetailedStats(current);
				}

				/* ── build before roll ── */
				printer.printSlow("\n" + current.getName()
					+ " – build before rolling (y / n)? ", 30);
				if (input.next().equalsIgnoreCase("y")) {
					listBuildOptions(current);
					printer.printSlow("Board location (-1 cancel): ", 30);
					int locChoice = input.nextInt();
					if (locChoice >= 0) attemptBuild(current, locChoice);
				}

				/* ── render board ── */
				mapManager.render(players);

				/* ── roll dice ── */
				printer.printSlow(current.getName() + " – Rolling Dice", 75);
				printer.printSlowln("...", 750);

				int die1 = roll(), die2 = roll();
				int sum  = die1 + die2;
				boolean rolledDoubles = die1 == die2;

				/* doubles streak tracking / Jail check */
				if (rolledDoubles) current.doublesStreak++;
				else               current.doublesStreak = 0;

				/* player is in Jail: handle special logic */
				if (current.inJail) {
					if (rolledDoubles) {                     // free immediately
						System.out.println(current.getName()
							+ " rolled doubles and leaves Jail.");
						current.inJail = false;
						current.jailTurns = 0;
					} else {
						boolean left = current.tryLeaveJail(die1, die2);
						if (!left) {                         // turn ends in Jail
							showStats();
							continue;
						}
					}
				}

				/* three consecutive doubles send to Jail */
				if (!current.inJail && current.doublesStreak == 3) {
					current.sendToJail();
					current.doublesStreak = 0;
					showStats();
					continue;
				}

				/* normal movement */
				System.out.println(current.getName() + " rolled: "
					+ die1 + " + " + die2 + " = " + sum);
				current.move(sum);

				Tile landed = mapManager.getTile(current.position);
				landed.landOn(current, this);

				/* bankruptcy check */
				if (current.money < 0 || current.getNetWorth() <= 0) {
					current.bankrupt = true;
					printer.printSlowln(current.getName() + " is bankrupt!");
				}

				showStats();                // quick status snapshot
			}

			/* ── trade phase ── */
			tradeManager.trade(players);

			/* ── auto-save ── */
			try {
				saveLoad.save(players);
				printer.printSlowln("Game auto-saved.", 50);
			} catch (Exception ex) {
				printer.printSlowln("Auto-save failed – new path? ", 50);
				saveLoad.setFile(input.next());
			}

			/* ── win condition ── */
			Participant lastAlive = null;
			int alive = 0;
			for (Participant p : players) if (!p.bankrupt) { alive++; lastAlive = p; }
			if (alive <= 1) {
				gameOver = true;
				if (lastAlive != null)
					printer.printSlowln(lastAlive.getName() + " Wins the Game!", 150);
			}
		}
	}

	/* ═════════════════════════════════════════════
     NEW: Detailed stats page for one player
     ═════════════════════════════════════════════ */
	private void showDetailedStats(Participant p) {

		System.out.println("\n===== " + p.getName() + " – Detailed Stats =====");
		System.out.println("Cash: $" + p.money);
		System.out.println("Net Worth: $" + p.getNetWorth());
		System.out.println("Board Position: " + p.position
			+ " (" + MapManager.NAMES[p.position] + ")");
		System.out.println("In Jail? " + (p.inJail ? "Yes" : "No"));
		if (p.hasGetOutOfJailCard)
			System.out.println("Special: *Get Out of Jail Free* card");

		System.out.println("\nOwned Properties:");
		boolean ownsAny = false;
		for (int loc = 0; loc < 40; loc++) {
			Property pr = p.core.getOwnedProperties().searchByLocation(loc);
			if (pr != null) {
				ownsAny = true;
				String dev = "";
				if (pr.isMortgaged()) dev = "[MORTGAGED]";
				else if (pr.getType().equals("property")) {
					if (pr.getRent() > pr.getBaseRent()) {
						dev = "(houses/hotel built)";
					}
				}
				System.out.printf("  [%02d] %-25s  Rent $%-4d %s%n",
					loc, pr.getName(), pr.getRent(), dev);
			}
		}
		if (!ownsAny) System.out.println("  (none)");
		System.out.println("========================================\n");
	}

	/* ═════════════════════════════════════════════
     Build helpers (unchanged)
     ═════════════════════════════════════════════ */
	private void listBuildOptions(Participant p) { /* … unchanged … */ }
	private void attemptBuild(Participant p,int loc){ /* … unchanged … */ }

	/* ═════════════════════════════════════════════
     Simple status snapshot
     ═════════════════════════════════════════════ */
	private void showStats() {
		printer.printSlowln("\n-- Status --", 25);
		for (Participant q : players) {
			printer.printSlow(q.getName() + " cash $" + q.money
				+ " net $" + q.getNetWorth(), 25);
			if (q.bankrupt) printer.printSlowln(" [BANKRUPT]", 25);
			else            System.out.println();
		}
		System.out.println();
	}

	/* ═════════════════════════════════════════════
     Dice helper
     ═════════════════════════════════════════════ */
	public static int roll() { return (int)(Math.random()*6)+1; }

	/* ═════════════════════════════════════════════
     Free-parking helpers (unchanged)
     ═════════════════════════════════════════════ */
	public void addToPot(int amt){ if(amt>0) freeParkingPot+=amt; }
	public int  collectPot(){ int c=freeParkingPot; freeParkingPot=0; return c; }
}