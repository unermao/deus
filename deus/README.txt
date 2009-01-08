
Come vengono gestite tutte le possibili situazioni

1) evento associato a processo associato all'engine:
l'engine schedula istanze di quell'evento con la temporizzazione dettata dal processo;

2) evento associato a processo non associato all'engine: 
deve essere messo nella coda da altri eventi (es. eventi che lo referenziano);
dopo che è stato eseguito, un evento dello stesso tipo viene creato dall'Engine, e inserito
nella coda (con temporizzazione data dal processo);
in questo modo posso ad esempio far sì che RevolAdaptationEvent sia schedulato periodicamente
per ogni nodo creato, se metto RevolAdaptationEvent tra le reference di BirthEvent

3) evento non associato a nessun processo 
<<<<<<< .mine
a cosa serve dichiararlo nell'XML? Per settargli i parametri e un id che viene utilizzato come riferimento al caricamento
=======
non e' necessario dichiararlo nell'XML!
per generarlo nel run() di un altro evento lo costruisco così (es.):
			try {
				RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) new RevolFreeResourceEvent("freeResource", null, null).createInstance(this.triggeringTime + random.nextInt(20));
				freeResEv.setResOwner(res.getOwner());
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(res.getAmount());
				Engine.getDefault().insertIntoEventsList(freeResEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
>>>>>>> .r237


Per qualsiasi evento che contenga riferimenti ad altri eventi, dopo la sua esecuzione 
vengono eseguiti gli eventi referenziati, a patto che abbiano un processo associato (parentProcess). 


SIMULATION AUTOMATOR
1) RUN CONFIGURATIONS 
	1.1) MAIN
	PROJECT --> deus
	MAIN CLASS --> it.unipr.ce.dsg.deus.automator.gui.DeusAutomatorFrame
	1.2) ARGUMENTS
	PROGRAM ARGUMENTS --> passare come primo parametro il path dell'XML del progetto e come secondo il path dove andare a scrivere l'XML per l'automatizzazione delle 			      simulazioni  
	VM ARGUMENTS --> -Djava.util.logging.config.file=logging.properties -Xms256M -Xmx1000M