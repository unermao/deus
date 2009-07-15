package it.unipr.ce.dsg.deus.example.jxta;

/**
* JXTA Advertisement are characterized by one ID (JXTAID).
* The flag "published" is set when the peer publish resource on a Rendezvous Super Peer.
* 
* @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
*
*/

public class JXTAAdvertisement {

	public int JXTAID;
	public boolean published;

	public JXTAAdvertisement(int jxtaid) {
		super();
		JXTAID = jxtaid;
		published = false;
	}
	
}
