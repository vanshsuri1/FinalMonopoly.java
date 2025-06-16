public class Player {
    private String playerName;
    private PropertyLinkedList ownedProperties;
    private boolean bankrupt;

    public Player(String playerName) {
        this.playerName = playerName;
        this.ownedProperties = new PropertyLinkedList();
        this.bankrupt = false;
    }

    public String getName() {
        return playerName;
    }

    public PropertyLinkedList getOwnedProperties() {
        return ownedProperties;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public void setBankrupt(boolean state) {
        bankrupt = state;
    }

    public void buyProperty(Property p) {
        ownedProperties.insert(p);
        System.out.println("Property bought: " + p.getName());
    }

    public void sellProperty(int boardPosition) {
        boolean wasDeleted = ownedProperties.delete(boardPosition);
        if (wasDeleted)
            System.out.println("Sold property at location " + boardPosition);
        else
            System.out.println("You do not own a property at location " + boardPosition);
    }

    public void showProperties() {
        System.out.println(playerName + "'s Properties:");
        ownedProperties.printAll();
    }

    public void sortProperties() {
        ownedProperties.sortByLocation();
        System.out.println("Properties sorted by location.");
    }

    public void searchProperty(String propertyName) {
        Property property = ownedProperties.searchByName(propertyName);
        if (property != null) {
            System.out.println("Found:");
            property.print();
        } else {
            System.out.println("You do not own that property.");
        }
    }

    public void mortgageProperty(int boardPosition) {
        Property property = ownedProperties.searchByLocation(boardPosition);
        if (property != null && !property.isMortgaged()) {
            property.setMortgaged(true);
            System.out.println("Mortgaged " + property.getName());
        } else if (property != null) {
            System.out.println(property.getName() + " is already mortgaged.");
        } else {
            System.out.println("You do not own a property at location " + boardPosition);
        }
    }

    public void unmortgageProperty(int boardPosition) {
        Property property = ownedProperties.searchByLocation(boardPosition);
        if (property != null && property.isMortgaged()) {
            property.setMortgaged(false);
            System.out.println("Unmortgaged " + property.getName());
        } else if (property != null) {
            System.out.println(property.getName() + " is not mortgaged.");
        } else {
            System.out.println("You do not own a property at location " + boardPosition);
        }
    }
}