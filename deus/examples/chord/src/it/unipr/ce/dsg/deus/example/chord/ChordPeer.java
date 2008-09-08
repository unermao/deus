package it.unipr.ce.dsg.deus.example.chord;

	import it.unipr.ce.dsg.deus.core.Engine;
	import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
	import it.unipr.ce.dsg.deus.core.Resource;
	import it.unipr.ce.dsg.deus.example.chord.ResourceAdv;
import it.unipr.ce.dsg.deus.example.revol.RevolPeer;
	import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
	import it.unipr.ce.dsg.deus.p2p.node.Peer;
	import java.math.BigInteger;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.Properties;
import java.util.Random;

	/**
	 * <p>
	 * ChordPeers are characterized by three kinds of consumable resources:
	 * CPU, RAM, DISK. Moreover, each RevolPeer has a chromosome, i.e.
	 * a set of parameters whose values are randomly initialized when the
	 * RevolPeer is instantiated, and may change during its lifetime, depending
	 * on external events. The RevolPeer keeps track of the number of sent queries (Q)
	 * and of the number of query hits (QH). The query hit ratio (QHR = QH/Q) is 
	 * initialized to 0.
	 * </p>
	 * 
	 * @author Marco Muro (marco.muro@studenti.unipr.it)
	 *
	 */
	public class ChordPeer extends Peer {

		private int first_index = 0;
		private int order_index = 0;
		private int numLocalResources = 0;
		private BigInteger bigId = null;
		
		public ArrayList<String> fingerTable = null;
		public ArrayList<Integer> searchResults = null;
		private ArrayList<String> localResources = null;
		private ArrayList<String> informationChordResources = null;
		
		private static final String VIDEO = "risorsaVideo";
		private static final String AUDIO = "risorsaAudio";
		private static final String TESTO = "risorsaTesto";
		private static final String IMMAGINE = "risorsaImmagine";
		
		private int risorsaVideo = 0;
		private int risorsaAudio = 0;
		private int risorsaTesto = 0;
		private int risorsaImmagine = 0;
		
		private int g = 0;
		// chromosome
		private int[] c = new int[3]; 
		// query log
		private double q = 0;
		private double qh = 0;
		
		
		private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>(); 
		
		public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
				throws InvalidParamsException {
			super(id, params, resources);
			initialize();
			fingerTable = new ArrayList<String>();
			searchResults = new ArrayList<Integer>();
			localResources = new ArrayList<String>();
			informationChordResources = new ArrayList<String>();
		}	
	
		public void initialize() throws InvalidParamsException {
			//System.out.println(getResources().size());
			for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
				Resource r = it.next();
				if (!(r instanceof AllocableResource))
					continue;
				if ( ((AllocableResource) r).getType().equals(VIDEO) )
					risorsaVideo = (int) ((AllocableResource) r).getAmount();
				else if ( ((AllocableResource) r).getType().equals(AUDIO) )
					risorsaAudio = (int) ((AllocableResource) r).getAmount();
				else if ( ((AllocableResource) r).getType().equals(TESTO) )
					risorsaTesto = (int) ((AllocableResource) r).getAmount();
				else if ( ((AllocableResource) r).getType().equals(IMMAGINE) )
					risorsaImmagine = (int) ((AllocableResource) r).getAmount();
				//System.out.println("cpuFactor = " + ((AllocableResource) r).getAmount());
			}	
		}
		
		public Object clone() {
			ChordPeer clone = (ChordPeer) super.clone();
			clone.fingerTable = new ArrayList<String>();
			clone.searchResults = new ArrayList<Integer>();
			clone.localResources = new ArrayList<String>();
			clone.informationChordResources = new ArrayList<String>();
			clone.g = 0;
			//clone.bigid = ;
			clone.c = new int[3];
			Random random = Engine.getDefault().getSimulationRandom(); 
			for (int i = 0; i < 3; i++)
				clone.c[i] = random.nextInt(10) + 1; // each gene is a random integer in [1,10]

			clone.setVideoResource((random.nextInt(risorsaVideo)+1)*512);
			clone.setAudioResource((random.nextInt(risorsaAudio)+1)*256);
			clone.setTextResource((random.nextInt(risorsaTesto)+1)*10000);
			clone.setImageResource((random.nextInt(risorsaImmagine)+1)*10000);
			
			clone.q = 0;
			clone.qh = 0;
			clone.cache = new ArrayList<ResourceAdv>();
			return clone;
		}
		
		
		public int getFirstIndex() {
			return first_index;
		}
		
		public void setFirstIndex(int first_index) {
			this.first_index = first_index;
		}
		
//		public String getUid() {
//			return uid;
//		}
//
//		public void setUid(String uid) {
//			this.uid = uid;
//		}
//
		public BigInteger getBigId() {
			return bigId;
		}
		
		public void setId(BigInteger bigId) {
			this.bigId= bigId;
		}
		
		public void setNumLocalResources(int numLocalResources) {
			this.numLocalResources = numLocalResources;
		}

		public ArrayList<String> getFingerTable() {
			return fingerTable;
		}
		
		public void setFingerTable(String app){
			
				fingerTable.add(app);	
		}
		
		public ArrayList<Integer> getSearchResults() {
			return searchResults;
		}
		
		public void setSearchResults(Integer index){
			
			searchResults.add(index);	
		}
		
		public ArrayList<String> getLocalResources() {
			return localResources;

		}
		public ArrayList<String> getLocalChordResources() {
			return informationChordResources;

		}
		public void setLocalChordResources(String resource){
			if(!informationChordResources.contains(resource))
			informationChordResources.add(resource);
		}
		
		public int getNumLocalResources() {
			return numLocalResources;
		}

		public void setSearchResults(ArrayList<Integer> results) {
			searchResults.addAll(results);	
			
		}

		public void setOrderIndex(int index) {
			order_index = index;
		}
		
		public int getOrderIndex() {
			return order_index ;
		}
		
		
		public double getFk() {
			return ((double) c[0])/10;
		}

		public int getTtlMax() {
			return c[1];
		}

		public int getDMax() {
			return c[2]*2;
		}

		public int getVideoResource() {
			return risorsaVideo;
		}

		public void setVideoResource(int risorsaVideo) {
			this.risorsaVideo = risorsaVideo;
		}

		public int getAudioResource() {
			return risorsaAudio;
		}

		public void setAudioResource(int risorsaAudio) {
			this.risorsaAudio = risorsaAudio;
		}

		public int getImageResource() {
			return risorsaImmagine;
		}
		
		public void setImageResource(int risorsaImmagine) {
			this.risorsaImmagine = risorsaImmagine;
		}
		
		public int getTextResource() {
			return risorsaTesto;
		}

		public void setTextResource(int risorsaTesto) {
			this.risorsaTesto = risorsaTesto;
		}
		
		public int[] getC() {
			return c;
		}

		public void setC(int[] c) {
			this.c = c;
		}

		public double getQ() {
			return q;
		}

		public void setQ(double q) {
			this.q = q;
		}

		public double getQh() {
			return qh;
		}

		public void setQh(double qh) {
			this.qh = qh;
		}

		public double getQhr() {
			if (this.q == 0)
				return -1;
			else
				return this.qh / this.q;
		}

		
		public ArrayList<ResourceAdv> getCache() {
			return cache;
		}

		public void setCache(ArrayList<ResourceAdv> cache) {
			this.cache = cache;
		}
		
		public void addToCache(ResourceAdv res) {
			if (cache.size() < getDMax()) 
				cache.add(res);
			else if (cache.size() == getDMax()) {
				cache.remove(0);
				cache.add(res);
			}
		}
		
		public int getG() {
			return g;
		}

		public void setG(int g) {
			this.g = g;
		}
		
		public void dropExceedingResourceAdvs() {
			// pulizia della cache: via gli adv. associati a nodi morti
			for (Iterator<ResourceAdv> it = cache.iterator(); it.hasNext();) {
				ResourceAdv currentResourceAdv = it.next();
				ChordPeer currentNode = (ChordPeer) currentResourceAdv.getOwner();
				//if ((currentNode == null) || (!currentNode.isReachable()))
				if (currentNode == null)
					this.removeResourceAdvFromCache(currentResourceAdv);
			}
			
			int dMax = this.getDMax();
			int numResourceAdvs = cache.size();
			if (numResourceAdvs <= dMax)
				return;
			ArrayList<ResourceAdv> newResourceAdvsList = new ArrayList<ResourceAdv>();
			// mantengo solo le più recenti
			for (int i = (numResourceAdvs - dMax); i < numResourceAdvs; i++)
				newResourceAdvsList.add((ResourceAdv) cache.get(i));
			cache = newResourceAdvsList;
		}
		
		public void removeResourceAdvFromCache(ResourceAdv currentResourceAdv) {
			ArrayList<ResourceAdv> newCache = new ArrayList<ResourceAdv>();
			for (Iterator<ResourceAdv> it = cache.iterator(); it.hasNext();) {
				ResourceAdv r = it.next();
				if (!r.equals(currentResourceAdv))
					newCache.add(r);
			}
			cache = newCache;
		}
		
		public Node createInstance(String id)
		{
			ChordPeer n = (ChordPeer) super.createInstance(id);
			n.bigId = new BigInteger(id,16);
			return n;
			
		}
		
}
