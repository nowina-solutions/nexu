package lu.nowina.nexu.api;

public class FeedbackClientTestApp {

	public static void main(String[] args) {
		
		FeedbackClient client = new FeedbackClient("http://localhost:8070/");
		
		Feedback f = new Feedback();
		f.setApiParameter("c:/bidule");
		
		client.reportError(f);
		
	}
	
}
