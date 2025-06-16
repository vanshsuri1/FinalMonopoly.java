class PropertyLinkedList {
	private Node first;

	public PropertyLinkedList() {
		first = null;
	}

	class Node {
		public Property property;
		public Node next;

		public Node(Property p) {
			property = p;
			next = null;
		}
	}

	public void insert(Property p) {
		Node node = new Node(p);
		if (first == null)
			first = node;
		else {
			Node current = first;
			while (current.next != null)
				current = current.next;
			current.next = node;
		}
	}

	public boolean delete(int location) {
		if (first == null)
			return false;
		if (first.property.getLocation() == location) {
			first = first.next;
			return true;
		}
		Node previous = first, current = first.next;
		while (current != null) {
			if (current.property.getLocation() == location) {
				previous.next = current.next;
				return true;
			}
			previous = current;
			current = current.next;
		}
		return false;
	}

	public Property searchByName(String name) {
		Node current = first;
		while (current != null) {
			if (current.property.getName().equalsIgnoreCase(name)) {
				return current.property;
			}
			current = current.next;
		}
		return null;
	}

	public Property searchByLocation(int location) {
		Node current = first;
		while (current != null) {
			if (current.property.getLocation() == location) {
				return current.property;
			}
			current = current.next;
		}
		return null;
	}

	public void printAll() {
		Node current = first;
		if (current == null) {
			System.out.println("No properties owned yet.");
		}
		while (current != null) {
			current.property.print();
			current = current.next;
		}
	}

	public void sortByLocation() {
		if (first == null || first.next == null)
			return;
		boolean swapped = true;
		while (swapped) {
			swapped = false;
			Node current = first;
			while (current.next != null) {
				if (current.property.getLocation() > current.next.property.getLocation()) {
					Property tmp = current.property;
					current.property = current.next.property;
					current.next.property = tmp;
					swapped = true;
				}
				current = current.next;
			}
		}
	}
}
