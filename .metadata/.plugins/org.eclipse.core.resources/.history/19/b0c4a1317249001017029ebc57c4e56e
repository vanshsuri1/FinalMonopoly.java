import java.util.Scanner;

public class AuctionManager {

	private Scanner input = new Scanner(System.in);

	public void auction(Property property, Participant[] players) {

		System.out.println("\n=== Auction: " + property.getName() + " ===");

		int highestBid = 0;
		Participant winner = null;
		boolean bidPlaced;

		do {
			bidPlaced = false;

			for (int i = 0; i < players.length; i++) {

				Participant player = players[i];
				if (player.bankrupt) {
					continue;
				}

				System.out.println(player.getName() + ", high bid $" + highestBid + ", your bid (0 = pass)?");
				int bid = Integer.parseInt(input.nextLine());

				if (bid > highestBid && bid <= player.money) {
					highestBid = bid;
					winner = player;
					bidPlaced = true;
				}
			}
		} while (bidPlaced);

		if (winner != null && highestBid > 0) {
			winner.money -= highestBid;
			winner.buy(property);
			System.out.println(winner.getName() + " wins at $" + highestBid);
		} else {
			System.out.println("No bids. Auction over.");
		}
		System.out.println("========================\n");
	}
}