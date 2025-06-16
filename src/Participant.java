public class Property {

	/* ---------- data copied from the official chart ---------- */
	private final int    location;          // -1 if non-board specific
	private final String name;
	private final String type;              // "property", "railroad", "utility"
	private final int    price;
	private final int    houseCost;         // cost for EACH house
	private final int[]  rentTable;         // length 6: 0H,1H,2H,3H,4H,Hotel
	private final int    mortgage;
	private boolean      mortgaged = false;

	/* live state */
	private int     houses   = 0;           // 0-4
	private boolean hasHotel = false;

	/* ---------- constructor used by MapManager ---------- */
	public Property(int loc,String nm,String tp,
									int price,int housePrice,
									int[] rents,int mortgage)
	{
		this.location   = loc;
		this.name       = nm;
		this.type       = tp;
		this.price      = price;
		this.houseCost  = housePrice;
		this.rentTable  = rents.clone();    // deep copy
		this.mortgage   = mortgage;
	}

	/* ---------- getters ---------- */
	public int    getLocation()   { return location; }
	public String getName()       { return name;     }
	public String getType()       { return type;     }
	public int    getPrice()      { return price;    }
	public int    getHouseCost()  { return houseCost;}
	public int    getMortgage()   { return mortgage; }
	public boolean isMortgaged()  { return mortgaged;}
	public void   setMortgaged(boolean v){ mortgaged = v;}

	/* ---------- build ---------- */
	public boolean buildHouse() {
		if (!type.equals("property") || mortgaged) return false;
		if (hasHotel) return false;

		if (houses < 4) {
			houses++;
			return true;
		}
		hasHotel = true;           // convert 4 houses → hotel
		return true;
	}

	/* ---------- rent ---------- */
	public int getRent() {
		if (!type.equals("property"))      // railroads/utilities handled elsewhere
			return rentTable[0];
		if (hasHotel)  return rentTable[5];
		return rentTable[houses];          // 0-4
	}

	/* ---------- debug print ---------- */
	public void print() {
		System.out.print("[" + name + "] $" + price +
			" rent $" + getRent());
		if (houses>0)  System.out.print(" H=" + houses);
		if (hasHotel)  System.out.print(" HOTEL");
		if (mortgaged) System.out.print(" *MORTGAGED*");
		System.out.println();
	}
}