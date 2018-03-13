package challenge;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String userName;
	private List<User> followUsers;
	private List<Message> messages;
	
	public User(String userName) {
		this.userName = userName;
		this.followUsers = new ArrayList<User>();
		this.messages = new ArrayList<Message>();
	}
	
	public String getUserName() {
		return userName;
	}

	public List<User> getFollowUsers() {
		return followUsers;
	}

	public List<Message> getMessages() {
		return messages;
	}
	
	public void addMessage(Message message) {
		messages.add(message);
	}
	
	public void addFollowUser(User user) {
		followUsers.add(user);
	}
}
