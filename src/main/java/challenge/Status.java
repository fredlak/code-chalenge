package challenge;

public class Status {

	private boolean successed; 
	private String message;
	
	public Status(boolean successed, String message) {
		this.successed = successed;
		this.message = message;
	}
	
	
	public boolean isSuccessed() {
		return successed;
	}

	public String getMessage() {
		return message;
	}
	
}
