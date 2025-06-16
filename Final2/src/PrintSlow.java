public class PrintSlow {

	public void printSlow(String message) {
		for (int i = 0; i < message.length(); i++) {
			System.out.print("" + message.charAt(i));
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				;
			}
		}
	}

	public void printSlow(String message, int delay) {
		for (int i = 0; i < message.length(); i++) {
			System.out.print("" + message.charAt(i));
			try {
				Thread.sleep(delay);
			} catch (InterruptedException exception) {
				;
			}
		}
	}

	public void printSlowln(String message, int delay) {
		for (int i = 0; i < message.length(); i++) {
			System.out.print("" + message.charAt(i));
			try {
				Thread.sleep(delay);
			} catch (InterruptedException exception) {
				;
			}
		}
		System.out.println();
	}

	public void printSlowln(String message) {
		for (int i = 0; i < message.length(); i++) {
			System.out.print("" + message.charAt(i));
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				;
			}
		}
		System.out.println();
	}

}
