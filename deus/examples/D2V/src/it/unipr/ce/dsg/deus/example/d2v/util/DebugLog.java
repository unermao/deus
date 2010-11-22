package it.unipr.ce.dsg.deus.example.d2v.util;

public class DebugLog {

	private long start;
	private long end;

	public void print(String className, float time)
	{
		System.out.println("VT:"+time+" "+className);
	}
	
	public void printStart(Integer peerKey,String className, float time)
	{
		this.start = System.currentTimeMillis();
		System.out.println("\nVT:"+time+" PeerKey: " + peerKey +" Event: "+className+" Starting At: " +  this.start);
	}
	
	public void printEnd(Integer peerKey,String className, float time)
	{
		this.end = System.currentTimeMillis();
		System.out.println("VT:"+time+" PeerKey: " + peerKey + " Event: "+className+" End At: " +  this.end + " PERIOD:"+(this.end-this.start)+" milli sec \n");
	}
	
	
}
