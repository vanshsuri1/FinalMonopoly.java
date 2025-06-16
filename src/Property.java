public class Property {

	/* ---------- core data ---------- */
	private final int    location;          // board index (-1 if not supplied)
	private final String name;
	private final String type;             // "property", "railroad", "utility"
	private final int    price;
	private final int    baseRent;         // rent with 0 houses
	private       int    houseCost;        // cost per house (0 if not set)
	private final int    mortgageValue;
	private boolean      mortgaged = false;

	/* ---------- houses & hotel ---------- */
	private int  houseCount = 0;           // 0-4
	private boolean hasHotel = false;

	/* ---------- constructor for MapManager quick-init ---------- */
	public Property(int loc, String nm, String tp, int pr, int rent) {
		location  = loc;
		name      = nm;
		type      = tp;
		price     = pr;
		baseRent  = rent;
		mortgageValue = pr / 2;

		/* infer house-cost if none supplied later (Monopoly rule of thumb) */
		if (tp.equals("property")) {
			/* cheap sets $50, mid $100/$150, dark blue $200 */
			if (pr <= 120)      houseCost = 50;
			else if (pr <= 200) houseCost = 100;
			else if (pr <= 280) houseCost = 150;
			else                houseCost = 200;
		} else {
			houseCost = 0;
		}
	}

	/* ---------- full constructor with rent table (railroads, etc.) ---------- */
	public Property(String nm,int pr,int hCost,int[] rentTable,int mortgage,String tp){
		location = -1;
		name     = nm;
		type     = tp;
		price    = pr;
		baseRent = rentTable.length>0 ? rentTable[0] : 0;
		houseCost     = hCost;
		mortgageValue = mortgage;
	}

	/* ---------- getters ---------- */
	public int    getLocation()      { return location; }
	public String getName()          { return name;     }
	public String getType()          { return type;     }
	public int    getPrice()         { return price;    }
	public int    getBaseRent()      { return baseRent; }
	public int    getHouseCost()     { return houseCost; }
	public boolean isMortgaged()     { return mortgaged; }
	public void   setMortgaged(boolean v){ mortgaged=v; }

	/* ---------- build house / hotel ---------- */
	public boolean buildHouse() {

		if (!type.equals("property") || mortgaged) return false;

		if (hasHotel) return false;               // already maxed

		if (houseCount < 4) {
			houseCount++;
			return true;
		}
		/* converting 4 houses -> hotel */
		houseCount = 4;
		hasHotel   = true;
		return true;
	}

	/* ---------- dynamic rent ---------- */
	public int getRent() {

		/* railroads & utilities keep original behaviour */
		if (!type.equals("property")) return baseRent;

        /* HOUSE / HOTEL rent ramp:
           0h  = base
           1h  = base * 2
           2h  = base * 3
           3h  = base * 4
           4h  = base * 5
           hotel = base * 6                                        */
		if (hasHotel) return baseRent * 6;
		if (houseCount == 0) return baseRent;
		return baseRent * (houseCount + 1);
	}

	/* ---------- debug print ---------- */
	public void print() {
		System.out.print("[" + name + "] $" + price +
			" rent $" + getRent() + " (" + type + ")");
		if (houseCount > 0) System.out.print(" houses=" + houseCount);
		if (hasHotel)       System.out.print(" [HOTEL]");
		if (mortgaged)      System.out.print(" [MORTGAGED]");
		System.out.println();
	}
}