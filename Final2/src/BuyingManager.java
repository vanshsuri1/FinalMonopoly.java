import java.util.Scanner;

public class BuyingManager {

	private final Scanner input = new Scanner(System.in);
	private final AuctionManager auctionManager = new AuctionManager();

	/* =========================================================
     Called from Tile.landOn → handles buy / rent / build
     ========================================================= */
	public void handleSpace(Participant player, Participant[] players, MapManager map) {

		Property land = map.getProperty(player.position);
		if (land == null) return;                 // non-ownable square

		/* ---------- check if someone owns it ---------- */
		Participant owner = null;
		for (Participant q : players) {
			if (q.core.getOwnedProperties().searchByLocation(player.position) != null) {
				owner = q; break;
			}
		}

		/* ---------- unowned: buy or auction ---------- */
		if (owner == null) {
			System.out.print("Buy " + land.getName() +
				" for $" + land.getPrice() + " (y/n): ");
			if (input.nextLine().equalsIgnoreCase("y")
				&& player.money >= land.getPrice()) {
				player.money -= land.getPrice();
				player.buy(land);
				System.out.println(player.getName() + " bought " + land.getName());
			} else {
				System.out.println("Property goes to auction.");
				auctionManager.auction(land, players);
			}
			return;
		}

		/* ---------- pay rent if someone else owns ---------- */
		if (owner != player) {
			int rent = land.getRent();
			/* double rent if “nearest Railroad” chance card flagged in owner */
			if (land.getType().equals("railroad") && owner.doubleRentNextRR) {
				rent *= 2;
				owner.doubleRentNextRR = false;  // reset flag
				System.out.println("(Double-rent Railroad)");
			}
			System.out.println("Pay rent $" + rent + " to " + owner.getName());
			if (player.money >= rent) {
				player.money -= rent;
				owner.money  += rent;
			} else {
				player.bankrupt = true;
				System.out.println(player.getName() + " is bankrupt!");
			}
			return;
		}

		/* ---------- owned by self: build if allowed ---------- */
		if (land.getType().equals("property")
			&& ownsColourSet(player, land)
			&& !land.isMortgaged()) {

			System.out.print("Build on " + land.getName() +
				" for $" + land.getHouseCost() + " (y/n): ");
			if (input.nextLine().equalsIgnoreCase("y")
				&& player.money >= land.getHouseCost()) {

				boolean ok = land.buildHouse();
				if (ok) {
					player.money -= land.getHouseCost();
					System.out.println("Built!  Rent now $" + land.getRent());
				} else {
					System.out.println("Maximum houses / hotel already present.");
				}
			}
		}
	}

	/* =========================================================
     Owns full colour-set?  (2- or 3-property groups)
     ========================================================= */
	public boolean ownsColourSet(Participant p, Property sample) {

		int needed = requiredForSet(sample.getLocation());
		if (needed == 0) return false;            // not a colour set

		/* use first THREE letters of name as group key */
		String key = sample.getName().substring(0, Math.min(3, sample.getName().length())).toUpperCase();

		int count = 0;
		for (int loc = 0; loc < 40; loc++) {
			Property pr = p.core.getOwnedProperties().searchByLocation(loc);
			if (pr != null) {
				String k2 = pr.getName().substring(0, Math.min(3, pr.getName().length())).toUpperCase();
				if (k2.equals(key)) count++;
			}
		}
		return count >= needed;
	}

	/* small helper: how many properties complete the set? */
	private int requiredForSet(int loc) {
		/* browns & dark-blues */
		if (loc==1 || loc==3 || loc==37 || loc==39) return 2;
		/* railroads / utilities ignored for building */
		/* everything else is a 3-property colour */
		return 3;
	}
}