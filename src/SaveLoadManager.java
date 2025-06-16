// File: SaveLoadManager.java


import java.io.*;

public class SaveLoadManager {

	private static String saveFilePath;

	public void setFile(String path) {
		saveFilePath = path;
	}

	public void save(Participant[] players) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(saveFilePath));

		writer.println(players.length);

		for (int i = 0; i < players.length; i++) {
			Participant p = players[i];
			writer.print(p.getName() + "," + p.money + "," + p.position + "," + p.inJail + "," + p.bankrupt + ","
				+ p.hasGetOutOfJailCard + "|");

			String locations = "";
			for (int loc = 0; loc < 40; loc++) {
				Property prop = p.core.getOwnedProperties().searchByLocation(loc);
				if (prop != null) {
					if (locations.length() == 0) {
						locations = locations + loc;
					} else {
						locations = locations + ";" + loc;
					}
				}
			}
			writer.println(locations);
		}
		writer.close();
		System.out.println("Game saved to " + saveFilePath);
	}

	public Participant[] load(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		Participant[] loadedPlayers = new Participant[Integer.parseInt(reader.readLine())];
		int index = 0;
		String line;

		while ((line = reader.readLine()) != null && index < 4) {
			int bar = line.indexOf('|');
			String statsPart = line.substring(0, bar);
			String propPart = line.substring(bar + 1);

			String[] stats = statsPart.split(",");
			Participant p = new Participant(stats[0]);
			p.money = Integer.parseInt(stats[1]);
			p.position = Integer.parseInt(stats[2]);
			p.inJail = Boolean.parseBoolean(stats[3]);
			p.bankrupt = Boolean.parseBoolean(stats[4]);
			p.hasGetOutOfJailCard = Boolean.parseBoolean(stats[5]);

			if (propPart.length() > 0) {
				String[] locs = propPart.split(";");
				for (int i = 0; i < locs.length; i++) {
					int loc = Integer.parseInt(locs[i]);
					Property prop = GameController.getBoard().getProperty(loc);
					if (prop != null) {
						p.buy(prop);
					}
				}
			}
			loadedPlayers[index] = p;
			index = index + 1;
		}
		reader.close();

		Participant[] result = new Participant[index];
		for (int i = 0; i < index; i++) {
			result[i] = loadedPlayers[i];
		}
		System.out.println("Game loaded from " + saveFilePath);
		return result;
	}

}