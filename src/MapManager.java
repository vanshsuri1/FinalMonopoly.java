/** MapManager – keeps board data, tiles and text-mode render (11 × 11 grid). */
public class MapManager {

    /* ---------- short labels for 40 squares ---------- */
    private static final String[] LABELS = {
      "GO","ME","CC1","BA","IT","RR1","OR","CH1","VE","CT",
      "JZ","ST","EC","ST2","VA","RR2","SJ","CC2","TN","NY",
      "FP","KY","CH2","IN","IL","RR3","AT","VE2","WW","MR",
      "GT","PA","NC","CC3","PE","RR4","CH3","PK","LT","BW"
    };

    /* ---------- FULL NAMES  (made public for stats screen) ---------- */
    public static final String[] NAMES = {
      "Go","Mediterranean Avenue","Community Chest 1","Baltic Avenue",
      "Income Tax","Reading Railroad","Oriental Avenue","Chance 1",
      "Vermont Avenue","Connecticut Avenue","Just Visiting / In Jail",
      "St. Charles Place","Electric Company","States Avenue",
      "Virginia Avenue","Pennsylvania Railroad","St. James Place",
      "Community Chest 2","Tennessee Avenue","New York Avenue",
      "Free Parking","Kentucky Avenue","Chance 2","Indiana Avenue",
      "Illinois Avenue","B. & O. Railroad","Atlantic Avenue",
      "Ventnor Avenue","Water Works","Marvin Gardens","Go To Jail",
      "Pacific Avenue","North Carolina Avenue","Community Chest 3",
      "Pennsylvania Avenue","Short Line Railroad","Chance 3",
      "Park Place","Luxury Tax","Boardwalk"
    };

    /* ---------- arrays for Tiles and ownable Properties ---------- */
    private final Tile[]     tiles  = new Tile[40];
    private final Property[] spaces = new Property[40];

    /* ---------- constructor: create Tiles & essential Properties ---------- */
    public MapManager() {

        /* ownable locations (location-only property ctor) */
        spaces[ 1] = new Property( 1,"Mediterranean Avenue","property",  60,  2);
        spaces[ 3] = new Property( 3,"Baltic Avenue",        "property",  60,  4);

        spaces[ 5] = new Property( 5,"Reading Railroad",     "railroad", 200, 25);

        spaces[ 6] = new Property( 6,"Oriental Avenue",      "property", 100,  6);
        spaces[ 8] = new Property( 8,"Vermont Avenue",       "property", 100,  6);
        spaces[ 9] = new Property( 9,"Connecticut Avenue",   "property", 120,  8);

        spaces[11] = new Property(11,"St. Charles Place",    "property", 140, 10);
        spaces[12] = new Property(12,"Electric Company",     "utility",  150,  0);
        spaces[13] = new Property(13,"States Avenue",        "property", 140, 10);
        spaces[14] = new Property(14,"Virginia Avenue",      "property", 160, 12);

        spaces[15] = new Property(15,"Pennsylvania Railroad","railroad", 200, 25);

        spaces[16] = new Property(16,"St. James Place",      "property", 180, 14);
        spaces[18] = new Property(18,"Tennessee Avenue",     "property", 180, 14);
        spaces[19] = new Property(19,"New York Avenue",      "property", 200, 16);

        spaces[21] = new Property(21,"Kentucky Avenue",      "property", 220, 18);
        spaces[23] = new Property(23,"Indiana Avenue",       "property", 220, 18);
        spaces[24] = new Property(24,"Illinois Avenue",      "property", 240, 20);

        spaces[25] = new Property(25,"B. & O. Railroad",     "railroad", 200, 25);

        spaces[26] = new Property(26,"Atlantic Avenue",      "property", 260, 22);
        spaces[27] = new Property(27,"Ventnor Avenue",       "property", 260, 22);
        spaces[28] = new Property(28,"Water Works",          "utility",  150,  0);
        spaces[29] = new Property(29,"Marvin Gardens",       "property", 280, 24);

        spaces[31] = new Property(31,"Pacific Avenue",       "property", 300, 26);
        spaces[32] = new Property(32,"North Carolina Avenue","property", 300, 26);
        spaces[34] = new Property(34,"Pennsylvania Avenue",  "property", 320, 28);

        spaces[35] = new Property(35,"Short Line Railroad",  "railroad", 200, 25);

        spaces[37] = new Property(37,"Park Place",           "property", 350, 35);
        spaces[39] = new Property(39,"Boardwalk",            "property", 400, 50);

        /* create Tiles */
        for (int i = 0; i < 40; i++) {
            tiles[i] = new Tile(NAMES[i]);
        }
    }

    /* ---------- accessors ---------- */
    public Tile getTile(int idx) {
        return (idx >= 0 && idx < 40) ? tiles[idx] : null;
    }
    public Property getProperty(int idx) {
        return (idx >= 0 && idx < 40) ? spaces[idx] : null;
    }

    /* ---------- helper to draw a single board slot ---------- */
    private String slot(int idx, Participant[] players) {

        char mark = ' ';
        for (Participant p : players) {
            if (!p.bankrupt && p.position == idx) {
                mark = p.getName().charAt(0);
                break;
            }
        }
        String lbl = LABELS[idx];
        if (lbl.length() == 2) lbl += " ";      // pad to 3
        lbl = lbl + mark;                       // now length 4
        while (lbl.length() < 4) lbl += " ";
        return "[" + lbl + "]";
    }

    /* ---------- render whole board (11 × 11) ---------- */
    public void render(Participant[] players) {

        PrintSlow ps = new PrintSlow();

        /* top row (20 → 30) */
        for (int i = 20; i <= 30; i++) ps.printSlow(slot(i, players) + " ", 0);
        System.out.println();

        /* middle rows */
        int left = 19, right = 31;
        for (int r = 0; r < 9; r++) {
            ps.printSlow(slot(left, players) + " ", 0);
            for (int s = 0; s < 10; s++) System.out.print("      ");
            ps.printSlow(slot(right, players) + " ", 0);
            System.out.println();
            left--;  right++;
        }

        /* bottom row (10 → 0) */
        for (int i = 10; i >= 0; i--) ps.printSlow(slot(i, players) + " ", 0);
        System.out.println("\n");
    }
}