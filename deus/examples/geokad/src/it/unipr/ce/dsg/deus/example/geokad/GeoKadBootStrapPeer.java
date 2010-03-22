package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

public class GeoKadBootStrapPeer extends GeoKadPeer{

	private static final int NODE_LIST_LIMIT = 100;
	//private final double KM_LIMIT = 20.0;
	public static int BOOTSTRAP_KEY = 0;
	
	public ArrayList<GeoKadPeerInfo> peerList = new ArrayList<GeoKadPeerInfo>();
	
	public GeoKadBootStrapPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
	}
	
	public void addIncomingNode(GeoKadPeer peer)
	{
		GeoKadPeerInfo peerInfo = new GeoKadPeerInfo(peer.getKey(),peer.getLatitude(),peer.getLongitude(),peer.getPeerCounter(),peer.getTimeStamp());
		
		if(!peerList.contains(peerInfo))
			peerList.add(peerInfo);
		else
		{
			peerList.remove(peerInfo);
			peerList.add(peerInfo);
		}
	}
	
	public ArrayList<GeoKadPeerInfo> getInitialPeerList(GeoKadPeerInfo peer)
	{
		final double peerLat = peer.getLatitude();
		final double peerLon = peer.getLongitude();
			
		if(peerList.size() > NODE_LIST_LIMIT)
		{
			ArrayList<GeoKadPeerInfo> tempList = new ArrayList<GeoKadPeerInfo>();
			
			// Sort PeerInfo according to distance
			Collections.sort(peerList, new Comparator<GeoKadPeerInfo>() {

				public int compare(GeoKadPeerInfo o1, GeoKadPeerInfo o2) {
			    
					double dist1 = GeoKadDistance.distance(peerLon,peerLat, o1.getLongitude(), o1.getLatitude());
					double dist2 = GeoKadDistance.distance(peerLon,peerLat, o2.getLongitude(), o2.getLatitude());
						
					if(dist1 == dist2)
						return 0;
					
					if(dist1 < dist2)
						return -1;
				
					if(dist1 > dist2)
						return 1;
					
					return 0;
			    }});
			
				//System.out.println("#########################################################");
				for(int index=0; index<NODE_LIST_LIMIT; index++)
				{
					GeoKadPeerInfo peerInfo = peerList.get(index);
					//double distance = GeoKadDistance.distance(peerLon,peerLat, peerInfo.getLongitude(), peerInfo.getLatitude());
					//System.out.println(distance);
					//if(distance <= KM_LIMIT)
					tempList.add(peerInfo);
				}	
				//System.out.println("#########################################################");
				
				return new ArrayList<GeoKadPeerInfo>(tempList);
		}
		else
			return new ArrayList<GeoKadPeerInfo>(peerList);
		
		
	}

	public ArrayList<GeoKadPeerInfo> getPeerList() {
		return peerList;
	}

	public void setPeerList(ArrayList<GeoKadPeerInfo> peerList) {
		this.peerList = peerList;
	}
}
