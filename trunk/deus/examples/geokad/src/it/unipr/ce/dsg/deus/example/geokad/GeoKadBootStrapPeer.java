package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;

import java.util.ArrayList;
import java.util.Properties;

public class GeoKadBootStrapPeer extends GeoKadPeer{

	private final double KM_LIMIT = 20.0;
	public static int BOOTSTRAP_KEY = 0;
	
	public ArrayList<GeoKadPeerInfo> peerList = new ArrayList<GeoKadPeerInfo>();
	
	public GeoKadBootStrapPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
	}
	
	public void addIncomingNode(GeoKadPeer peer)
	{
		GeoKadPeerInfo peerInfo = new GeoKadPeerInfo(peer.getKey(),peer.getLatitude(),peer.getLongitude());
		if(!peerList.contains(peerInfo))
			peerList.add(peerInfo);
	}
	
	public ArrayList<GeoKadPeerInfo> getInitialPeerList(GeoKadPeer peer)
	{
		ArrayList<GeoKadPeerInfo> tempList = new ArrayList<GeoKadPeerInfo>();
		
		for(int index=0; index<peerList.size(); index++)
		{
			GeoKadPeerInfo peerInfo = peerList.get(index);
			double distance = GeoKadDistance.distance(peer.getLongitude(), peer.getLatitude(), peerInfo.getLongitude(), peerInfo.getLatitude());
			if(distance <= KM_LIMIT)
				tempList.add(peerInfo);
		}
		
		return tempList;
	}
}
