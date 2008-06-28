package it.unipr.ce.dsg.deus.core;

import it.unipr.ce.dsg.deus.schema.Automator;
import it.unipr.ce.dsg.deus.schema.Param;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class AutomatorParser {

	private ArrayList<Node> nodes = null;

	private ArrayList<Event> events = null;

	private ArrayList<Process> processes = null;

	private Engine engine = null;

	@SuppressWarnings("unchecked")
	public AutomatorParser(String fileName) throws JAXBException,
			ClassNotFoundException, IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		nodes = new ArrayList<Node>();
		events = new ArrayList<Event>();
		processes = new ArrayList<Process>();

		JAXBContext jc = JAXBContext.newInstance("it.unipr.ce.dsg.deus.schema");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Automator automator = (Automator) ((JAXBElement) unmarshaller
				.unmarshal(new File(fileName))).getValue();

		// Parse all the nodes in order to initialize Node objects
		for (Iterator<it.unipr.ce.dsg.deus.schema.Node> it = automator
				.getNode().iterator(); it.hasNext();) {
			it.unipr.ce.dsg.deus.schema.Node node = it.next();
			Class<Node> nodeHandler = (Class<Node>) this.getClass()
					.getClassLoader().loadClass(node.getHandler());

			Properties params = new Properties();
			if (node.getParams() != null)
				params = parseParams(node.getParams().getParam().iterator());

			ArrayList<Resource> resources = new ArrayList<Resource>();
			if (node.getResources() != null) {
				for (Iterator<it.unipr.ce.dsg.deus.schema.Resource> it2 = node.getResources().getResource().iterator();
					 it2.hasNext(); ) {
					it.unipr.ce.dsg.deus.schema.Resource resource = it2.next();
					Class<Resource> resourceHandler = (Class<Resource>) this.getClass()
					.getClassLoader().loadClass(resource.getHandler());

					Properties resourceParams = new Properties();
					if (resource.getParams() != null)
						resourceParams = parseParams(resource.getParams().getParam().iterator());
					
					Resource configResource = resourceHandler.getConstructor(
							new Class[] { Properties.class })
							.newInstance(new Object[] { resourceParams });
					resources.add(configResource);
				}
			}
			
			Node configNode = nodeHandler.getConstructor(
					new Class[] { String.class, Properties.class, ArrayList.class})
					.newInstance(new Object[] { node.getId(), params, resources });
			if (node.getLogger() != null) {
				configNode.setLoggerLevel(node.getLogger().getLevel());
				configNode
						.setLoggerPathPrefix(node.getLogger().getPathPrefix());
			}
			nodes.add(configNode);
		}

		// Parse all the events in order to initialize Event objects
		for (Iterator<it.unipr.ce.dsg.deus.schema.Event> it = automator
				.getEvent().iterator(); it.hasNext();) {
			it.unipr.ce.dsg.deus.schema.Event event = it.next();
			Class<Event> eventHandler = (Class<Event>) this.getClass()
					.getClassLoader().loadClass(event.getHandler());

			Properties params = new Properties();
			if (event.getParams() != null)
				params = parseParams(event.getParams().getParam().iterator());

			Event configEvent = eventHandler
					.getConstructor(
							new Class[] { String.class, Properties.class,
									Process.class }).newInstance(
							new Object[] { event.getId(), params, null });
			if (event.isOneShot() != null)
				configEvent.setOneShot(event.isOneShot().booleanValue());

			if (event.getLogger() != null) {
				configEvent.setLoggerLevel(event.getLogger().getLevel());
				configEvent.setLoggerPathPrefix(event.getLogger()
						.getPathPrefix());
			}
			
			if (event.getSchedulerListener() != null) {
				SchedulerListener schedulerListener = (SchedulerListener) this
						.getClass().getClassLoader().loadClass(
								event.getSchedulerListener()).newInstance();
				
				configEvent.setSchedulerListener(schedulerListener);
			}
			
			events.add(configEvent);

		}

		// Parse all the events checking for referenced events in order to store
		// this information into previously created Event objects
		for (Iterator<it.unipr.ce.dsg.deus.schema.Event> it = automator
				.getEvent().iterator(); it.hasNext();) {
			it.unipr.ce.dsg.deus.schema.Event event = it.next();
			Event realEvent = getEventById(event.getId());

			if (event.getEvents() != null) {
				for (Iterator<it.unipr.ce.dsg.deus.schema.Reference> it2 = event
						.getEvents().getReference().iterator(); it2.hasNext();) {
					realEvent.getReferencedEvents().add(
							getEventById(it2.next().getId()));
				}
			}
		}

		// Parase all the processes in order to initialize Process objects
		for (Iterator<it.unipr.ce.dsg.deus.schema.Process> it = automator
				.getProcess().iterator(); it.hasNext();) {
			it.unipr.ce.dsg.deus.schema.Process process = it.next();

			ArrayList<Node> referencedNodes = new ArrayList<Node>();
			if (process.getNodes() != null)
				for (Iterator<it.unipr.ce.dsg.deus.schema.Reference> it2 = process
						.getNodes().getReference().iterator(); it2.hasNext();)
					referencedNodes.add(getNodeById(it2.next().getId()));

			ArrayList<Event> referencedEvents = new ArrayList<Event>();
			if (process.getEvents() != null)
				for (Iterator<it.unipr.ce.dsg.deus.schema.Reference> it2 = process
						.getEvents().getReference().iterator(); it2.hasNext();)
					referencedEvents.add(getEventById(it2.next().getId()));

			Properties params = new Properties();
			if (process.getParams() != null)
				params = parseParams(process.getParams().getParam().iterator());

			Class<Process> processHandler = (Class<Process>) this.getClass()
					.getClassLoader().loadClass(process.getHandler());
			Process configProcess = processHandler.getConstructor(
					new Class[] { String.class, Properties.class,
							ArrayList.class, ArrayList.class }).newInstance(
					new Object[] { process.getId(), params, referencedNodes,
							referencedEvents });
			if (process.getLogger() != null) {
				configProcess.setLoggerLevel(process.getLogger().getLevel());
				configProcess.setLoggerPathPrefix(process.getLogger()
						.getPathPrefix());
			}
			processes.add(configProcess);

			// TODO sistemare evitare di scorrere due volte i referenced events
			// di un process
			for (Iterator<it.unipr.ce.dsg.deus.schema.Reference> it2 = process
					.getEvents().getReference().iterator(); it2.hasNext();)
				getEventById(it2.next().getId())
						.setParentProcess(configProcess);

		}

		ArrayList<Process> referencedProcesses = new ArrayList<Process>();
		for (Iterator<it.unipr.ce.dsg.deus.schema.Reference> it = automator
				.getEngine().getProcesses().getReference().iterator(); it
				.hasNext();) {
			referencedProcesses.add(getProcessById(it.next().getId()));
		}

		engine = new Engine(automator.getEngine().getMaxvt(), automator
				.getEngine().getSeed(), nodes, events, processes,
				referencedProcesses);

		if (automator.getEngine().getLogger() != null) {
			engine.setLoggerLevel(automator.getEngine().getLogger().getLevel());
			engine.setLoggerPathPrefix(automator.getEngine().getLogger()
					.getPathPrefix());
		}
	}

	private Node getNodeById(String id) {
		for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
			Node n = it.next();
			if (n.getId().equals(id))
				return n;
		}

		return null;
	}

	private Event getEventById(String id) {
		for (Iterator<Event> it = events.iterator(); it.hasNext();) {
			Event e = it.next();
			if (e.getId().equals(id))
				return e;
		}

		return null;
	}

	private Process getProcessById(String id) {
		for (Iterator<Process> it = processes.iterator(); it.hasNext();) {
			Process p = it.next();
			if (p.getId().equals(id))
				return p;
		}
		return null;
	}

	private Properties parseParams(Iterator<Param> it) {
		Properties params = new Properties();
		while (it.hasNext()) {
			Param param = it.next();
			params.setProperty(param.getName(), param.getValue());
		}
		return params;
	}

	public Engine getEngine() {
		return engine;
	}

}