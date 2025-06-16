public class Board {
    public static final String[] NAMES = { "Go", "Mediterranean", "Community Chest", "Baltic", "Income Tax",
        "Reading Railroad", "Oriental", "Chance", "Vermont", "Connecticut", "Just Visiting", "St. Charles Place",
        "Electric Company", "States Avenue", "Virginia Avenue", "Pennsylvania Railroad", "St. James Place",
        "Community Chest", "Tennessee", "New York Avenue", "Free Parking", "Kentucky", "Chance", "Indiana",
        "Illinois", "B. & O. Railroad", "Atlantic", "Ventnor", "Water Works", "MarvinGardens", "Go To Jail",
        "Pacific", "North Carolina", "Community Chest", "Pennsylvania", "Short Line", "Chance", "Park Place",
        "Luxury Tax", "Boardwalk" };

    public static final String[] ABBR = { "GO", "ME", "CC1", "BA", "IT", "RR1", "OR", "CH1", "VE", "CO", "JV", "SC",
        "EC", "SA", "VA", "RR2", "SJ", "CC2", "TE", "NY", "FP", "KY", "CH2", "IN", "IL", "RR3", "AT", "VE2", "WW",
        "MG", "GJ", "PA", "NC", "CC3", "PE", "SL", "CH3", "PP", "LT", "BW" };

    public static final Property[] properties = new Property[40];
    static {
        properties[1] = new Property("Mediterranean", 60, 50, new int[] { 2, 10, 30, 90, 160, 250 }, 30, "property");
        properties[3] = new Property("Baltic", 60, 50, new int[] { 4, 20, 60, 180, 320, 450 }, 30, "property");
        properties[6] = new Property("Oriental", 100, 50, new int[] { 6, 30, 90, 270, 400, 550 }, 50, "property");
        properties[8] = new Property("Vermont", 100, 50, new int[] { 6, 30, 90, 270, 400, 550 }, 50, "property");
        properties[9] = new Property("Connecticut", 120, 50, new int[] { 8, 40, 100, 300, 450, 600 }, 60, "property");
        properties[11] = new Property("St. Charles", 140, 100, new int[] { 10, 50, 150, 450, 625, 750 }, 70, "property");
        properties[13] = new Property("States", 140, 100, new int[] { 10, 50, 150, 450, 625, 750 }, 70, "property");
        properties[14] = new Property("Virginia", 160, 100, new int[] { 12, 60, 180, 500, 700, 900 }, 80, "property");
        properties[16] = new Property("St. James", 180, 100, new int[] { 14, 70, 200, 550, 750, 950 }, 90, "property");
        properties[18] = new Property("Tennessee", 180, 100, new int[] { 14, 70, 200, 550, 750, 950 }, 90, "property");
        properties[19] = new Property("New York", 200, 100, new int[] { 16, 80, 220, 600, 800, 1000 }, 100, "property");
        properties[21] = new Property("Kentucky", 220, 150, new int[] { 18, 90, 250, 700, 875, 1050 }, 110, "property");
        properties[23] = new Property("Indiana", 220, 150, new int[] { 18, 90, 250, 700, 875, 1050 }, 110, "property");
        properties[24] = new Property("Illinois", 240, 150, new int[] { 20, 100, 300, 750, 925, 1100 }, 120, "property");
        properties[26] = new Property("Atlantic", 260, 150, new int[] { 22, 110, 330, 800, 975, 1150 }, 130, "property");
        properties[27] = new Property("Ventnor", 260, 150, new int[] { 22, 110, 330, 800, 975, 1150 }, 130, "property");
        properties[29] = new Property("MarvinGardens", 280, 150, new int[] { 24, 120, 360, 850, 1025, 1200 }, 140, "property");
        properties[31] = new Property("Pacific", 300, 200, new int[] { 26, 130, 390, 900, 1100, 1275 }, 150, "property");
        properties[32] = new Property("NorthCarolina", 300, 200, new int[] { 26, 130, 390, 900, 1100, 1275 }, 150, "property");
        properties[34] = new Property("Pennsylvania", 320, 200, new int[] { 28, 150, 450, 1000, 1200, 1400 }, 160, "property");
        properties[37] = new Property("ParkPlace", 350, 200, new int[] { 35, 175, 500, 1100, 1300, 1500 }, 175, "property");
        properties[39] = new Property("Boardwalk", 400, 200, new int[] { 50, 200, 600, 1400, 1700, 2000 }, 200, "property");
        properties[5] = new Property("ReadingRR", 200, 0, new int[] { 25, 50, 100, 200 }, 100, "railroad");
        properties[15] = new Property("PennsylvaniaRR", 200, 0, new int[] { 25, 50, 100, 200 }, 100, "railroad");
        properties[25] = new Property("BORailroad", 200, 0, new int[] { 25, 50, 100, 200 }, 100, "railroad");
        properties[35] = new Property("ShortLineRR", 200, 0, new int[] { 25, 50, 100, 200 }, 100, "railroad");
        properties[12] = new Property("ElectricCompany", 150, 0, new int[] { 4, 10 }, 75, "utility");
        properties[28] = new Property("WaterWorks", 150, 0, new int[] { 4, 10 }, 75, "utility");
    }

    private Tile[] tiles;

    public Board() {
        tiles = new Tile[40];
        for (int i = 0; i < 40; i++) {
            tiles[i] = new Tile(NAMES[i]);
        }
    }

    private String getAbbreviation(int index) {
        return ABBR[index];
    }

    private String slotLabel(int index, Participant[] players) {
        String abbreviation = getAbbreviation(index);
        String abbreviationTwo;
        if (abbreviation.length() >= 2) {
            abbreviationTwo = abbreviation.substring(0, 2).toUpperCase();
        } else if (abbreviation.length() == 1) {
            abbreviationTwo = (abbreviation.substring(0, 1).toUpperCase() + " ");
        } else {
            abbreviationTwo = "  ";
        }
        char playerChar = ' ';
        for (int i = 0; i < players.length; i++) {
            if (players[i].position == index) {
                String pName = players[i].getName();
                if (pName != null && pName.length() > 0) {
                    playerChar = Character.toLowerCase(pName.charAt(0));
                    break;
                }
            }
        }
        String slotText = "[" + abbreviationTwo + playerChar;
        while (slotText.length() < 10) {
            slotText += " ";
        }
        slotText += "]";
        return slotText;
    }

    public void render(Participant[] players) {
        for (int i = 0; i <= 10; i++) {
            System.out.print(slotLabel(i, players));
        }
        System.out.println();

        for (int line = 0; line < 9; line++) {
            int leftIndex = 39 - line;
            System.out.print(slotLabel(leftIndex, players));
            for (int s = 0; s < 8; s++) {
                System.out.print("           ");
            }
            int rightIndex = 11 + line;
            System.out.print(slotLabel(rightIndex, players));
            System.out.println();
        }

        for (int i = 30; i >= 20; i--) {
            System.out.print(slotLabel(i, players));
        }
        System.out.println();
        System.out.println();
    }

    public Tile getTile(int position) {
        return tiles[position];
    }

    public Property getProperty(int position) {
        return properties[position];
    }
}