package it.unipr.ce.dsg.deus.example.chordStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Collections;
import java.util.Properties;

public class LogChordForNwbEvent extends Event {
	
	private int ray = 1000;
	private int x_center = 100;
	private int y_center = 100;
	
	public LogChordForNwbEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		
		double x[] = new double[Engine.getDefault().getNodes().size()];
		double y[] = new double[Engine.getDefault().getNodes().size()];
		getLogger().info("######################### ChordPeer Script for nwb:" + Engine.getDefault().getVirtualTime());
		Collections.sort(Engine.getDefault().getNodes());
		
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer n = (ChordPeer) Engine.getDefault().getNodes().get(i);
				x[i] = getX_center() + getRay() * Math.cos((i*6.283185307*(360/Engine.getDefault().getNodes().size()))/360);
				y[i] = getY_center() + getRay() * Math.sin((i*6.283185307*(360/Engine.getDefault().getNodes().size()))/360);
				getLogger().info("for n in g.nodes: " + "n"+(n.getKey()+1)+".x=" + x[i] +";" + "n"+(n.getKey()+1)+".y=" + y[i] +";");
			}
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer n = (ChordPeer) Engine.getDefault().getNodes().get(i);
				if(n.getServerId() == true)
				getLogger().info( (n.getKey()+1) + " " + "\"" + n.getKey() +"\"" + " \"" +"blue" + "\"");
				else
				getLogger().info( n.getKey()+1 + " " + "\"" + n.getKey() +"\"" + " \""+ "red" + "\"");	
			}
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer n = (ChordPeer) Engine.getDefault().getNodes().get(i);
				for(int c = 0; c < n.fingerTable.length; c++)
				{
					if( n.fingerTable[c] != null)
				getLogger().info( (n.getKey()+1) + " " + (n.fingerTable[c].getKey()+1) + " \""+ "true"+ "\"");	
					
				}
			}	
	}

	public int getRay() {
		return ray;
	}

	public void setRay(int ray) {
		this.ray = ray;
	}

	public int getCenter() {
		return x_center;
	}

	public void setCenter(int center) {
		this.y_center = center;
	}

	public int getY_center() {
		return y_center;
	}

	public void setY_center(int y_center) {
		this.y_center = y_center;
	}

	public int getX_center() {
		return x_center;
	}

	public void setX_center(int x_center) {
		this.x_center = x_center;
	}

	
}