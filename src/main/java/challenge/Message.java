package challenge;

import java.util.Date;

public class Message {
	
	private Date date;	
	private String message;
	
	public Message(Date date, String message) {
		this.date = date;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public Date getDate() {
		return date;
	}
	
}
