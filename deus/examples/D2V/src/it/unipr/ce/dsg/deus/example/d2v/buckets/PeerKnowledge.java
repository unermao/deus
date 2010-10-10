package it.unipr.ce.dsg.deus.example.d2v.buckets;

public class PeerKnowledge {

	public static String ADD_ACTION = "A";
	public static String REMOVE_ACTION = "R";
	public static String UPDATE_ACTION = "U";
	
	private float timeStamp = 0;
	private String action = null;
	
	public PeerKnowledge(float timeStamp, String action) {
		super();
		this.timeStamp = timeStamp;
		this.action = action;
	}

	public float getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public static String getADD_ACTION() {
		return ADD_ACTION;
	}

	public static void setADD_ACTION(String aDDACTION) {
		ADD_ACTION = aDDACTION;
	}
	
}
