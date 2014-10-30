package Error;


public class ShindigError extends Exception {
	private String errorMessage;
	public static final String ERROR_NO_RIGHT  = "You have no rith to access this asset";
	public static final String ERROR_POST = "post not well formatted";
	@SuppressWarnings("unused")
	private int errorCode = -1;

	
	public ShindigError(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}
	
	public ShindigError(String errorMessage, int i) {
		super();
		this.errorCode = i;
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
