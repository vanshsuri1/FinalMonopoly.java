public class Participant {

    /* original Player instance (keeps the linked-list of properties) */
    public Player core;

    /* cash and board position */
    public int money;           /* liquid cash           */
    public int position;        /* 0-39 board index      */

    /* status flags */
    public boolean bankrupt;
    public boolean inJail;
    public boolean hasGetOutOfJailCard;

    /* jail + dice counters */
    public int jailTurns;       /* 0,1,2 while in Jail   */
    public int doublesStreak;   /* consecutive doubles   */

    /* ------------------------------------------------------------- */
    public Participant(String name) {

        core             = new Player(name);
        money            = 1500;
        position         = 0;

        bankrupt         = false;
        inJail           = false;
        hasGetOutOfJailCard = false;

        jailTurns        = 0;   /* <-- now initialised   */
        doublesStreak    = 0;   /* also initialised      */
    }

    /* simple helper */
    public String getName() {
        return core.getName();
    }

    /* -------------------------------------------------------------
       Movement – adds $200 when passing GO (but **not** while in jail)
     ------------------------------------------------------------- */
    public void move(int steps) {

        int oldPos = position;
        position   = (position + steps) % 40;

        /* only award $200 if not in Jail */
        if (!inJail && position < oldPos) {
            money = money + 200;
            System.out.println(getName() + " collects $200 for passing GO.");
        }
    }

    /* -------------------------------------------------------------
       Net-worth helper used by bankruptcy check
     ------------------------------------------------------------- */
    public int getNetWorth() {

        int total = money;
        int loc   = 0;
        while (loc < 40) {
            Property pr = core.getOwnedProperties().searchByLocation(loc);
            if (pr != null) {
                total = total + pr.getPrice();
            }
            loc = loc + 1;
        }
        return total;
    }

    /* -------------------------------------------------------------
       Buy wrapper – inserts property into the linked list
     ------------------------------------------------------------- */
    public void buy(Property p) {
        core.buyProperty(p);
    }

    /* -------------------------------------------------------------
       Jail helper – called from GameController.handleJailTurn
     ------------------------------------------------------------- */
    public boolean tryLeaveJail(int die1, int die2) {

        jailTurns = jailTurns + 1;

        if (die1 == die2) {          /* rolled doubles -> free immediately */
            inJail     = false;
            jailTurns  = 0;
            System.out.println(getName() + " rolled doubles and leaves Jail.");
            return true;
        }

        if (jailTurns == 3) {        /* served 3 turns -> pay $50 and leave */
            System.out.println(getName() + " served 3 turns and pays $50.");
            if (money >= 50) {
                money     = money - 50;
            } else {
                bankrupt  = true;    /* cannot pay */
            }
            inJail     = false;
            jailTurns  = 0;
            return true;
        }

        /* still in Jail */
        return false;
    }

    /* -------------------------------------------------------------
       Send a player to jail
     ------------------------------------------------------------- */
    public void sendToJail() {
        this.position = 10;   // Jail is usually at index 10 on the board
        this.inJail = true;
        this.jailTurns = 0;   // Reset jail turns counter
        System.out.println(getName() + " is sent to Jail.");
    }
}