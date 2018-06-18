package lu.nowina.nexu.flow;

public class StageHelper {

	private static StageHelper instance;

	private String title;

	private StageHelper() {
	}

	public static synchronized StageHelper getInstance() {
		if (instance == null) {
			synchronized (StageHelper.class) {
				if (instance == null) {
					instance = new StageHelper();
				}
			}
		}
		return instance;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}
}
