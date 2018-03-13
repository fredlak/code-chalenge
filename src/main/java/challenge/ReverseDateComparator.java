package challenge;

import java.util.Comparator;

public class ReverseDateComparator implements Comparator<Message> {

	public int compare(Message arg0, Message arg1) {
		return arg0.getDate().compareTo(arg1.getDate()) * -1;
	}

}
