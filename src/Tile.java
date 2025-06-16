/**
 * Board square.  When a player lands here, landOn(...)
 * decides what happens based on the square’s label.
 */
class Tile {

    private final String name;

    Tile(String name) { this.name = name; }

    /** Invoked from GameController after the player is moved. */
    void landOn(Participant p, GameController ctrl) {

        /* ---------- Chance ---------- */
        if (name.toLowerCase().contains("chance")) {
            Card card = Card.drawChance();
            System.out.println("Chance: " + card.text);
            card.applyEffect(p, ctrl);
            return;
        }

        /* ---------- Community Chest ---------- */
        if (name.toLowerCase().contains("community")
          && name.toLowerCase().contains("chest")) {
            Card card = Card.drawChest();
            System.out.println("Community Chest: " + card.text);
            card.applyEffect(p, ctrl);
            return;
        }

        /* ---------- Property / Railroad / Utility ---------- */
        Property pr = ctrl.getMapManager().getProperty(p.position);
        if (pr != null) {
            ctrl.getBuyingManager()
              .handleSpace(p, ctrl.getPlayers(), ctrl.getMapManager());
            return;
        }

        /* ---------- Special board squares ---------- */
        if (name.equalsIgnoreCase("Go")) {
            p.money += 200;
            System.out.println(p.getName() + " collects $200 for landing on GO.");
        }
        else if (name.equalsIgnoreCase("Income Tax")) {
            p.money -= 200;
            ctrl.addToPot(200);
            System.out.println(p.getName() + " pays $200 Income Tax.");
        }
        else if (name.equalsIgnoreCase("Luxury Tax")) {
            p.money -= 100;
            ctrl.addToPot(100);
            System.out.println(p.getName() + " pays $100 Luxury Tax.");
        }
        else if (name.equalsIgnoreCase("Free Parking")) {
            int pot = ctrl.collectPot();
            p.money += pot;
            System.out.println(p.getName() + " lands on Free Parking and collects $" + pot + "!");
        }
        else if (name.equalsIgnoreCase("Go To Jail")) {
            p.sendToJail();
            System.out.println(p.getName() + " goes directly to Jail!");
        }
        else if (name.toLowerCase().contains("jail")) {     // “Just Visiting”
            System.out.println(p.getName() + " is just visiting Jail.");
        }
    }
}