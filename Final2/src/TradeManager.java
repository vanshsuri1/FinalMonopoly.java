import java.util.Scanner;

public class TradeManager {

    private Scanner input = new Scanner(System.in);

    /* =========================================================
       MAIN ENTRY
       ========================================================= */
    public void trade(Participant[] players) {

        System.out.println("\n--- Trade Phase ---");
        System.out.print("Start a trade? (y / n): ");
        String ansStart = input.nextLine();
        if (!ansStart.equalsIgnoreCase("y")) {
            System.out.println("No trades this round.\n");
            return;
        }

        /* choose participants */
        int idx = 0;
        while (idx < players.length) {
            System.out.println((idx + 1) + ": " + players[idx].getName());
            idx = idx + 1;
        }

        System.out.print("Trader A #: ");
        Participant traderA = players[
          Integer.parseInt(input.nextLine()) - 1];

        System.out.print("Trader B #: ");
        Participant traderB = players[
          Integer.parseInt(input.nextLine()) - 1];

        /* build offers */
        TradeOffer offerA = buildOffer(traderA, "A");
        TradeOffer offerB = buildOffer(traderB, "B");

        /* summary */
        System.out.println("\n--- Proposed Trade ---");
        showOffer(traderA, offerA);
        showOffer(traderB, offerB);

        System.out.print(traderA.getName() + ", accept? (y / n): ");
        boolean acceptA = input.nextLine().equalsIgnoreCase("y");

        System.out.print(traderB.getName() + ", accept? (y / n): ");
        boolean acceptB = input.nextLine().equalsIgnoreCase("y");

        if (acceptA && acceptB) {
            executeTrade(traderA, traderB, offerA, offerB);
            System.out.println("Trade completed.\n");
        } else {
            System.out.println("Trade cancelled.\n");
        }
    }

    /* =========================================================
       SUPPORT TYPES
       ========================================================= */
    private static class TradeOffer {
        int      cash;
        Property prop;      // may be null
    }

    /* =========================================================
       BUILD SINGLE OFFER
       ========================================================= */
    private TradeOffer buildOffer(Participant p, String label) {

        TradeOffer offer = new TradeOffer();

        System.out.println("\n" + p.getName() + "'s properties:");
        printOwnedWithIndex(p);                       // show index + name

        System.out.print("Player " + label +
          " – choose location (0 = none): ");
        int loc = Integer.parseInt(input.nextLine());
        if (loc > 0) {
            offer.prop = p.core.getOwnedProperties()
              .searchByLocation(loc);
        }

        System.out.print("Player " + label + " – cash $: ");
        int cashIn = Integer.parseInt(input.nextLine());
        if (cashIn < 0)          cashIn = 0;
        if (cashIn > p.money)    cashIn = p.money;
        offer.cash = cashIn;

        return offer;
    }

    /* =========================================================
       PRINT OFFER SUMMARY
       ========================================================= */
    private void showOffer(Participant p, TradeOffer offer) {

        System.out.print(p.getName() + " offers ");
        if (offer.prop != null) {
            System.out.print("[" + offer.prop.getLocation() + "] "
              + offer.prop.getName());
        } else {
            System.out.print("no property");
        }
        System.out.println(" and $" + offer.cash);
    }

    /* =========================================================
       EXECUTE SWAP
       ========================================================= */
    private void executeTrade(Participant a, Participant b,
                              TradeOffer offA, TradeOffer offB) {

        /* property A -> B */
        if (offA.prop != null) {
            a.core.sellProperty(offA.prop.getLocation());
            b.buy(offA.prop);
        }

        /* property B -> A */
        if (offB.prop != null) {
            b.core.sellProperty(offB.prop.getLocation());
            a.buy(offB.prop);
        }

        /* cash transfer */
        a.money = a.money - offA.cash + offB.cash;
        b.money = b.money - offB.cash + offA.cash;
    }

    /* =========================================================
       HELPER – list property indexes
       ========================================================= */
    private void printOwnedWithIndex(Participant p) {

        int pos = 0;
        while (pos < 40) {
            Property pr = p.core.getOwnedProperties()
              .searchByLocation(pos);
            if (pr != null) {
                System.out.println("  [" + pos + "] " + pr.getName());
            }
            pos = pos + 1;
        }
        if (p.core.getOwnedProperties().searchByLocation(0) == null
          && pos == 40) {
            System.out.println("  (none)");
        }
    }
}