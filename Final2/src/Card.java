import java.util.Random;

class Card {

    /* ---------- descriptive fields ---------- */
    public String text;   // full message shown to the player
    public String type;   // "Chance" or "Community Chest"

    /* ---------- raw text (16 per deck) ---------- */
    private static final String[] CHANCE_TEXTS = {
        "Advance to Boardwalk.",
        "Advance to Go (Collect $200).",
        "Advance to Illinois Avenue. If you pass Go, collect $200.",
        "Advance to St. Charles Place. If you pass Go, collect $200.",
        "Advance to the nearest Railroad. If unowned, you may buy it. If owned, pay double rent.",
        "Advance to the nearest Railroad. If unowned, you may buy it. If owned, pay double rent.",
        "Advance token to nearest Utility. If unowned, you may buy it. If owned, throw dice and pay 10× dice roll.",
        "Bank pays you dividend of $50.",
        "Get Out of Jail Free.",
        "Go Back 3 Spaces.",
        "Go to Jail. Go directly to Jail, do not pass Go, do not collect $200.",
        "Make general repairs on all your property. For each house pay $25. For each hotel pay $100.",
        "Speeding fine $15.",
        "Take a trip to Reading Railroad. If you pass Go, collect $200.",
        "You have been elected Chairman of the Board. Pay each player $50.",
        "Your building loan matures. Collect $150."
    };

    private static final String[] CHEST_TEXTS = {
        "Advance to Go (Collect $200)",
        "Bank error in your favor. Collect $200",
        "Doctor’s fee. Pay $50",
        "From sale of stock you get $50",
        "Get Out of Jail Free",
        "Go to Jail. Go directly to jail, do not pass Go, do not collect $200",
        "Holiday fund matures. Receive $100",
        "Income tax refund. Collect $20",
        "It is your birthday. Collect $10 from every player",
        "Life insurance matures. Collect $100",
        "Pay hospital fees of $100",
        "Pay school fees of $50",
        "Receive $25 consultancy fee",
        "You are assessed for street repair. $40 per house. $115 per hotel",
        "You have won second prize in a beauty contest. Collect $10",
        "You inherit $100"
    };

    /* ---------- the two decks ---------- */
    private static final CardLinkedList chanceDeck = new CardLinkedList();
    private static final CardLinkedList chestDeck  = new CardLinkedList();

    /* ---------- static block builds + shuffles once ---------- */
    static {
        int i = 0;
        while (i < CHANCE_TEXTS.length) {
            chanceDeck.insert(new Card(CHANCE_TEXTS[i], "Chance"));
            i = i + 1;
        }
        i = 0;
        while (i < CHEST_TEXTS.length) {
            chestDeck.insert(new Card(CHEST_TEXTS[i], "Community Chest"));
            i = i + 1;
        }
        chanceDeck.shuffle();
        chestDeck.shuffle();
    }

    /* ---------- private constructor ---------- */
    private Card(String txt, String tp) {
        text = txt;
        type = tp;
    }

    /* ---------- single-arg public ctor (rarely used) ---------- */
    public Card(String txt) {
        this(txt, "");
    }

    /* ---------- draw helpers ---------- */
    public static Card drawChance() {
        return chanceDeck.draw();
    }

    public static Card drawChest() {
        return chestDeck.draw();
    }

    /* ==========================================================
     *  applyEffect
     * ========================================================== */
    public void applyEffect(Participant player, GameController ctrl) {

        MapManager       map  = ctrl.getMapManager();
        BuyingManager    buy  = ctrl.getBuyingManager();
        Participant[]    all  = ctrl.getPlayers();

        /* ===== CHANCE ===== */
        if (type.equals("Chance")) {

            if (text.equals("Advance to Boardwalk.")) {
                player.position = 39;
                System.out.println(player.getName() + " advances to Boardwalk.");
                buy.handleSpace(player, all, map);
            }

            else if (text.equals("Advance to Go (Collect $200).")) {
                player.position = 0;
                player.money += 200;
                System.out.println(player.getName() + " advances to GO and collects $200.");
            }

            else if (text.equals("Advance to Illinois Avenue. If you pass Go, collect $200.")) {
                if (player.position > 24) {
                    player.money += 200;
                }
                player.position = 24;
                System.out.println(player.getName() + " advances to Illinois Avenue.");
                buy.handleSpace(player, all, map);
            }

            else if (text.equals("Advance to St. Charles Place. If you pass Go, collect $200.")) {
                if (player.position > 11) {
                    player.money += 200;
                }
                player.position = 11;
                System.out.println(player.getName() + " advances to St. Charles Place.");
                buy.handleSpace(player, all, map);
            }

            else if (text.contains("nearest Railroad")) {
                int[] rr = {5, 15, 25, 35};
                int i = 0;
                int nextPos = player.position;          // fallback = stay
                while (i < rr.length) {
                    if (rr[i] > player.position) {
                        nextPos = rr[i];
                        i = rr.length;                   // exit loop without break
                    } else {
                        i = i + 1;
                    }
                }
                player.position = nextPos;
                System.out.println(player.getName() + " moves to nearest Railroad.");
                Property railroad = GameController.getBoard().getProperty(nextPos);
                if (railroad != null && railroad.getType().equals("railroad")) {
                    int rent = railroad.getRent() * 2;  // Double the rent
                    player.money -= rent;
                    // Add logic to give rent to the railroad owner
                    System.out.println(player.getName() + " pays double rent of $" + rent);
                }
                buy.handleSpace(player, all, map);
            }

            else if (text.contains("nearest Utility")) {
                int[] ut = {12, 28};
                int i = 0;
                int nextPos = player.position;
                while (i < ut.length) {
                    if (ut[i] > player.position) {
                        nextPos = ut[i];
                        i = ut.length;
                    } else {
                        i = i + 1;
                    }
                }
                player.position = nextPos;
                System.out.println(player.getName() + " moves to nearest Utility.");
                buy.handleSpace(player, all, map);       // 10× dice rent not implemented
            }

            else if (text.equals("Bank pays you dividend of $50.")) {
                player.money += 50;
            }

            else if (text.equals("Get Out of Jail Free.")) {
                player.hasGetOutOfJailCard = true;
            }

            else if (text.equals("Go Back 3 Spaces.")) {
                player.position = (player.position + 37) % 40;
                System.out.println(player.getName() + " goes back 3 spaces.");
                buy.handleSpace(player, all, map);
            }

            else if (text.startsWith("Go to Jail")) {
                player.position = 10;
                player.inJail = true;
                System.out.println(player.getName() + " goes directly to Jail.");
            }

            else if (text.startsWith("Make general repairs")) {
                System.out.println("General repairs card not implemented.");
            }

            else if (text.equals("Speeding fine $15.")) {
                player.money -= 15;
            }

            else if (text.equals("Take a trip to Reading Railroad. If you pass Go, collect $200.")) {
                if (player.position > 5) {
                    player.money += 200;
                }
                player.position = 5;
                System.out.println(player.getName() + " travels to Reading Railroad.");
                buy.handleSpace(player, all, map);
            }

            else if (text.equals("You have been elected Chairman of the Board. Pay each player $50.")) {
                int idx = 0;
                while (idx < all.length) {
                    Participant q = all[idx];
                    if (q != player && !q.bankrupt) {
                        q.money += 50;
                        player.money -= 50;
                    }
                    idx = idx + 1;
                }
            }

            else if (text.equals("Your building loan matures. Collect $150.")) {
                player.money += 150;
            }
        }

        /* ===== COMMUNITY CHEST ===== */
        else {

            if (text.equals("Advance to Go (Collect $200)")) {
                player.position = 0;
                player.money += 200;
            }

            else if (text.equals("Bank error in your favor. Collect $200")) {
                player.money += 200;
            }

            else if (text.equals("Doctor’s fee. Pay $50")) {
                player.money -= 50;
            }

            else if (text.equals("From sale of stock you get $50")) {
                player.money += 50;
            }

            else if (text.equals("Get Out of Jail Free")) {
                player.hasGetOutOfJailCard = true;
            }

            else if (text.startsWith("Go to Jail")) {
                player.position = 10;
                player.inJail = true;
            }

            else if (text.equals("Holiday fund matures. Receive $100")) {
                player.money += 100;
            }

            else if (text.equals("Income tax refund. Collect $20")) {
                player.money += 20;
            }

            else if (text.equals("It is your birthday. Collect $10 from every player")) {
                int idx = 0;
                while (idx < all.length) {
                    Participant q = all[idx];
                    if (q != player && !q.bankrupt) {
                        q.money -= 10;
                        player.money += 10;
                    }
                    idx = idx + 1;
                }
            }

            else if (text.equals("Life insurance matures. Collect $100")) {
                player.money += 100;
            }

            else if (text.equals("Pay hospital fees of $100")) {
                player.money -= 100;
            }

            else if (text.equals("Pay school fees of $50")) {
                player.money -= 50;
            }

            else if (text.equals("Receive $25 consultancy fee")) {
                player.money += 25;
            }

            else if (text.startsWith("You are assessed for street repair")) {
                System.out.println("Street-repair card not implemented.");
            }

            else if (text.equals("You have won second prize in a beauty contest. Collect $10")) {
                player.money += 10;
            }

            else if (text.equals("You inherit $100")) {
                player.money += 100;
            }
        }
    }
}