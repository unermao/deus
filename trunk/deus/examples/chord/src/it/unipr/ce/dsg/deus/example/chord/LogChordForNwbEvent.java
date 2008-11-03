package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.chord.ChordPeer;

import java.util.Collections;
import java.util.HashMap;
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
		HashMap<String,Integer> app = new HashMap<String,Integer>();
		
		getLogger().info("######################### ChordPeer Script for nwb:" + Engine.getDefault().getVirtualTime());
		
		Collections.sort(Engine.getDefault().getNodes());

		getLogger().info("#NWB Data for Streaming P2P\n *Nodes " +Engine.getDefault().getNodes().size() + "\n id*int label*string x*float y*float");
		
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer n = (ChordPeer) Engine.getDefault().getNodes().get(i);
				x[i] = getX_center() + getRay() * Math.cos((i*6.283185307*(360.0/(double)Engine.getDefault().getNodes().size()))/360.0);
				y[i] = getY_center() + getRay() * Math.sin((i*6.283185307*(360.0/(double)Engine.getDefault().getNodes().size()))/360.0);
				getLogger().info((i+1)+" " + "\"" + (n.getKey()) + "\"" +" " +  x[i] + " " +  y[i]);
				app.put(Integer.toString(n.getKey()), i+1);
			}
		
			getLogger().info("*DirectedEdges\n source*int target*int");
			
			
			
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				int control = -1;
				ChordPeer n = (ChordPeer) Engine.getDefault().getNodes().get(i);
				for(int c = 0; c < n.fingerTable.length; c++)
				{
					if( n.fingerTable[c] != null && control != app.get(Integer.toString(n.fingerTable[c].getKey())))
				getLogger().info( app.get(Integer.toString(n.getKey())) + " " + app.get(Integer.toString(n.fingerTable[c].getKey())) + " \""+ "true"+ "\"");	
				control = app.get(Integer.toString(n.fingerTable[c].getKey()));	
					
				}
				
			}
			//per fare lo script
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				x[i] = getX_center() + getRay() * Math.cos((i*6.283185307*(360.0/(double)Engine.getDefault().getNodes().size()))/360.0);
				y[i] = getY_center() + getRay() * Math.sin((i*6.283185307*(360.0/(double)Engine.getDefault().getNodes().size()))/360.0);
				getLogger().info("for n in g.nodes: " + "n"+(i+1)+".x=" + x[i] +";" + "n"+(i+1)+".y=" + y[i] +";");
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
