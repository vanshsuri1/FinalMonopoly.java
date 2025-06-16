import java.util.Random;

class CardLinkedList {
    private static class CardNode {
        Card card;
        CardNode next;

        CardNode(Card c) {
            card = c;
            next = null;
        }
    }

    private CardNode head;

    public CardLinkedList() {
        head = null;
    }

    // Insert a card at the end of the list
    public void insert(Card c) {
        CardNode newNode = new CardNode(c);
        if (head == null) {
            head = newNode;
        } else {
            CardNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    // Draw a card (pop) and rotate it to the bottom of the list
    public Card draw() {
        if (head == null) {
            return null;
        }
        CardNode top = head;
        head = head.next; // remove from front
        insert(top.card); // add it back at the end
        return top.card;
    }

    // Shuffle the deck using Fisher-Yates shuffle
    public void shuffle() {
        int count = 0;
        CardNode temp = head;
        while (temp != null) {
            count++;
            temp = temp.next;
        }

        if (count <= 1) return;

        // Copy to an array
        Card[] cardsArray = new Card[count];
        temp = head;
        int i = 0;
        while (temp != null) {
            cardsArray[i] = temp.card;
            temp = temp.next;
            i++;
        }

        // Shuffle
        Random rnd = new Random();
        for (int k = count - 1; k > 0; k--) {
            int j = rnd.nextInt(k + 1);
            Card tempCard = cardsArray[k];
            cardsArray[k] = cardsArray[j];
            cardsArray[j] = tempCard;
        }

        // Rebuild the list
        head = null;
        for (Card card : cardsArray) {
            insert(card);
        }
    }
}