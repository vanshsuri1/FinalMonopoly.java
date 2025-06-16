public class GamePlayer {
	public Player playerCore;
	public int cash;
	public int boardPosition;
	public boolean isBankrupt;

	public GamePlayer(String name) {
		this.playerCore = new Player(name);
		this.cash = 1500;
		this.boardPosition = 0;
		this.isBankrupt = false;
	}

	public String getName() {
		return playerCore.getName();
	}

	public int getCash() {
		return cash;
	}

	public int getBoardPosition() {
		return boardPosition;
	}

	public boolean getIsBankrupt() {
		return isBankrupt;
	}

	public void buyProperty(Property p) {
		playerCore.buyProperty(p);
	}

	public void sellProperty(int loc) {
		playerCore.sellProperty(loc);
	}

	public void showProperties() {
		playerCore.showProperties();
	}

}
