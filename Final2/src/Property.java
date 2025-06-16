public class Property {

    private int location = -1;
    private String name;
    private String type;
    private int price;
    private int baseRent;
    private int houseCost;
    private int[] rentLevels;
    private int mortgageValue;
    private boolean mortgaged = false;

    private int houseCount = 0;
    private boolean hasHotel = false;

    public Property(int location, String name, String type, int price, int rent) {
        this.location = location;
        this.name = name;
        this.type = type;
        this.price = price;
        this.baseRent = rent;
        this.houseCost = 0;

        this.rentLevels = new int[1];
        this.rentLevels[0] = rent;

        this.mortgageValue = price / 2;
    }

    public Property(String name, int price, int houseCost, int[] rentTable, int mortgage, String type) {
        this.name = name;
        this.price = price;
        this.houseCost = houseCost;
        this.type = type;
        this.mortgageValue = mortgage;

        this.rentLevels = new int[rentTable.length];
        for (int index = 0; index < rentTable.length; index++) {
            this.rentLevels[index] = rentTable[index];
        }

        if (rentTable.length > 0) {
            this.baseRent = rentTable[0];
        } else {
            this.baseRent = 0;
        }
    }

    public int getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getBaseRent() {
        return baseRent;
    }

    public int getHouseCost() {
        return houseCost;
    }

    public int getMortgageValue() {
        return mortgageValue;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public void setMortgaged(boolean value) {
        mortgaged = value;
    }

    public boolean buildHouse() {
        if (!type.equals("property")) {
            return false;
        }

        if (hasHotel) {
            return false;
        }

        if (houseCount < 4) {
            houseCount = houseCount + 1;
            return true;
        }

        houseCount = 4;
        hasHotel = true;
        return true;
    }

    public int getRent() {
        int index = 0;

        if (hasHotel) {
            index = rentLevels.length - 1;
        } else {
            if (houseCount < rentLevels.length) {
                index = houseCount;
            } else {
                index = rentLevels.length - 1;
            }
        }
        return rentLevels[index];
    }

    public void print() {
        System.out.print("[" + name + "]  $" + price);

        System.out.print("  rent $" + getRent());

        System.out.print("  (" + type + ")");

        if (houseCount > 0) {
            System.out.print("  houses=" + houseCount);
        }

        if (hasHotel) {
            System.out.print("  [HOTEL]");
        }

        if (mortgaged) {
            System.out.print("  [MORTGAGED]");
        }

        System.out.println();
    }
}