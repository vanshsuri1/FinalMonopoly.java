public class DiceRoll {

    /** Roll two six‑sided dice and return their sum (2‑12). */
    public static int roll() {
        int dieOne = (int) (Math.random() * 6) + 1;
        int dieTwo = (int) (Math.random() * 6) + 1;
        return dieOne + dieTwo;
    }
}
