# DEUS v0.6.x #

To quickly start using DEUS, download the full release from the **[DOWNLOADS section](http://dsg.ce.unipr.it/?q=node/86)**, unzip it, run Eclipse, go to the workbench, right-click in the left column (`PackageExplorer` or `Navigator`), select Import ---> Existing projects into workspace. Then press Next and select, as root directory, the unzipped deus-0.6.x folder; the project will be automatically imported in your workspace.

The examples/ folder contains a basic example which comes without source code, as everything it needs is included in the DEUS API. In general, we suggest to put any deus-based simulator in examples/, remembering to add the examples/yoursimulator/src/ folder to the source path of the deus project.


# DEUS API #

DEUS API includes the following packages:

  * `it.unipr.ce.dsg.deus.automator` (classes for batch simulations with automatically changing parameters)
  * `it.unipr.ce.dsg.deus.automator.gui` (the GUI for managing batch simulations)
  * `it.unipr.ce.dsg.deus.core` (core interfaces and classes, such as Event, Node, Process)
  * `it.unipr.ce.dsg.deus.editor` (implementation of the visual editor)
  * `it.unipr.ce.dsg.deus.impl.event` (implementation of common events, such as node birth)
  * `it.unipr.ce.dsg.deus.impl.node` (implementation of basic node classes)
  * `it.unipr.ce.dsg.deus.impl.process` (implementation of common processes, such as Poisson)
  * `it.unipr.ce.dsg.deus.impl.resource` (implementation of basic resource classes)
  * `it.unipr.ce.dsg.deus.p2p.event` (P2P-specific events, such as connection)
  * `it.unipr.ce.dsg.deus.p2p.node` (P2P-specific node types, such as Peer)
  * `it.unipr.ce.dsg.deus.schema` (classes that map DEUS schema)
  * `it.unipr.ce.dsg.deus.schema.automator` (classes that map DEUS' XML schema)
  * `it.unipr.ce.dsg.deus.util` (utility classes)

You can download the source code, or browse it online in the "Source" section of the project web site.


# Configuring simulations #

The XML schema basically defines the following concepts: event, node, process, engine.

In the following we describe a XML configuration file for a simulation that creates a network of 10000 nodes.

Like any other XML file for the configuration of a DEUS-based simulation, you have to declare nodes like:

```
<aut:node id="revolNode" handler="it.unipr.ce.dsg.deus.example.revol.RevolPeer">
  <aut:params>
    <aut:param name="fitnessFunction" value="1" />
    <aut:param name="maxInitChromosome" value="6" />
    <aut:param name="isRandomInit" value="true" />
  </aut:params>
  <aut:resources>
    <aut:resource handler="it.unipr.ce.dsg.deus.impl.resource.AllocableResource">
      <aut:params>
        <aut:param name="type" value="cpuFactor" />
	<aut:param name="amount" value="4" /><!-- *512 MHz = 2GHz -->
      </aut:params>	
    </aut:resource>
    <aut:resource handler="it.unipr.ce.dsg.deus.impl.resource.AllocableResource">
      <aut:params>
	<aut:param name="type" value="ramFactor" />
	<aut:param name="amount" value="4" /><!-- *256 MB = 1GB -->
      </aut:params>	
    </aut:resource>
    <aut:resource handler="it.unipr.ce.dsg.deus.impl.resource.AllocableResource">
      <aut:params>
        <aut:param name="type" value="diskFactor" />
        <aut:param name="amount" value="10" /><!-- *10000 MB = 100GB -->
      </aut:params>	
    </aut:resource>
  </aut:resources>
</aut:node>
```

The handler must be a Java class in the classpath (this is a general rule, not only for nodes but also for resources, events, processes). Resources may be complex objects characterizing nodes (for this, they are not simply params of nodes, but entities with an associated handler which is the Java class defining the resource).

Other than nodes, you have to declare events, like:

```
<aut:event id="birth" handler="it.unipr.ce.dsg.deus.impl.event.BirthEvent" 
schedulerListener="it.unipr.ce.dsg.deus.example.revol.RevolBirthSchedulerListener" >
  <aut:events>
    <!-- events to be scheduled on created node -->
    <aut:reference id="connection" />
  </aut:events>
</aut:event>

<aut:event id="connection" handler="it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent" oneShot="true" >
  <aut:params>
    <aut:param name="isBidirectional" value="true" />
    <aut:param name="maxNumInitialConnections" value="3" />
  </aut:params>
</aut:event>
```

The "birth" event has an internal reference to another event, "connection", which is defined separately. This means that when a "birth" is scheduled, also a "connection" is scheduled (the timestamp of the referenced event is always greater than the rimestamp of the referencer). An event may have or not some parameters (e.g. "birth" has no params, while "connection" has params). An event can be one-shot, which means that it is executed only once, even though its time-regulating process is periodic.

Then, an event may be declared and instantiated in the Java code of another event. This approach is out of the control of the user that creates the XML configuration file.

In the XML file, you may define processes which regulate the timeliness of events, like:

```
<aut:process id="rectpulseperiodic" handler="it.unipr.ce.dsg.deus.impl.process.RectangularPulsePeriodicProcess">
  <aut:params>
    <aut:param name="period" value="10" />
    <aut:param name="startVtThreshold" value="0" />
    <aut:param name="stopVtThreshold" value="100000" />
  </aut:params>
  <aut:nodes>
    <aut:reference id="revolNode" />
  </aut:nodes>
  <aut:events>
    <aut:reference id="birth" />
  </aut:events>
</aut:process>

<aut:process id="poisson2" handler="it.unipr.ce.dsg.deus.impl.process.PoissonProcess">
  <aut:params>
    <aut:param name="meanArrival" value="5" />
  </aut:params>
  <aut:nodes>
    <aut:reference id="revolNode" />
  </aut:nodes>
  <aut:events>
    <aut:reference id="connection" />
  </aut:events>
</aut:process>
```

In DEUS v.0.5.0 we have added three new processes, which allow to schedule events with inter-arrival time having respectively Weibull, Shifted Pareto and Multimodal distribution.

Each process may refer to one or more node type, and to one or more events. Important note: the node associated to an event (if the event is a `NodeEvent`, thus having the associatedEvent variable) must be decided by the event itself! If a process referes to N node types, the events scheduled according to that process can be associated to one of the node types, according to a specific behavior implemented in the event's code. See for example `BirthEvent.java` - such event selects (with uniformly random probability) one of the node types.

A declared event cannot be associated to more than one declared process. Of course you can declare more events of the same type (e.g., "birth1" and "birth2" both handled by the same Java class) and associate them to the same process or to two different processes (which means differently declared processes, i.e. two processes with different id).

Finally, you have to declare which processes to run:
```
<aut:engine maxvt="2000000" seed="123456789" prng="ISAAC"> 
  <aut:logger level="INFO" />
  <aut:processes>
    <aut:reference id="rectpulseperiodic" /> 
    <aut:reference id="periodic2" />
  </aut:processes>
</aut:engine>
```

Introduced with v0.6.0, the `prng` parameter allows you to set the random number generator. Possible choices are: "ISAAC", "WELL1024a", "WELL19937a", "WELL19937c", "WELL44497a", "WELL44497b", "WELL512a". If you do not put the `prng` parameter in the engine tag, the default Java random number generator is used.

NOTE 1:

The engine does not refer to the poisson2 process, defined above. This means that "connection" events are not directly scheduled by the engine. The latter schedules "birth" events, according to the "rectpulseperiodic" process. Each "birth" triggers the scheduling of a "connection", whose timestamp is automatically computed using the "poisson2" process.

NOTE 2:

If you define in the XML an event and the associated process, you can obtain in the Java code a reference to the process, given a reference to the event. Look at the following example:

```
        PurchaseEvent pevModel = null;
	for (Iterator<Event> it = Engine.getDefault().getConfigEvents().iterator(); it.hasNext();) {
		Event ev = it.next();
		if (ev instanceof PurchaseEvent) {
			pevModel = (PurchaseEvent) ev;
			break;
		}
	}
```

```
        float timestamp = pevModel.getParentProcess().getNextTriggeringTime(pevModel, currentVirtualTime);
        if (timestamp < Engine.getDefault().getMaxVirtualTime()) {
        	PurchaseEvent purchaseEvent = (PurchaseEvent) pevModel.createInstance(timestamp);
		purchaseEvent.setTargetCustomer(selectedNeighbor);
		purchaseEvent.setTargetStore(targetStore);
		purchaseEvent.setProductId(targetProductList.get(0));
		purchaseEvent.setBonus(bonus);
		Engine.getDefault().insertIntoEventsList(purchaseEvent);
        }
```

We first get a reference to the "model" of `PurchaseEvent` (the one used by the engine to create clones). The we create an instance of `PurchaseEvent` to be scheduled. The triggering time of such an instance is computed using the process associated to `PurchaseEvent`.


# DEUS Visual Editor #

Finally we have a visual editor that avoids to write the XML files manually, for configuring simulations.

To run the visual tool, supposing you have imported deus in Eclipse, create a "Run configuration" with the following features:

Project: deus

Main Class: it.unipr.ce.dsg.deus.editor.MainGUI

The user manual of the Visual Editor will be published in the "Downloads" section of this site.

Note: By default, the XML generated by the visual editor has the following initial tag:

```
<aut:automator xmlns:aut="http://dsg.ce.unipr.it/software/deus/schema/automator"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://dsg.ce.unipr.it/software/deus/schema/automator schema/automator.xsd">
```

You will need to fix the path to `automator.xsd`, depending on where you will place the XML.


# Timestamps of events #

In the Event class, the scheduleReferencedEvents method creates and insert in the queue the events that are referred from the event scheduled by the Engine (example: `BirthEvent` references `DiscoveryEvent`).

If event X references events A, B and C, their triggering times are respectively:

**tA = tX + tGivenByProcessAssociatedToA**

**tB = tX + tGivenByProcessAssociatedToB**

**tC = tX + tGivenByProcessAssociatedToC**

BE CAREFUL!

If you want that tA < tB <tC you need to create a reference chain:

**A references B**

**B references C**

for which C always comes after B, which always come after A.

Remember to implement all necessary scheduler listeners!!!

NOTE:

Starting from DEUS v.0.5.0, events with the same timestamp are randomly ordered into the event queue (in previous versions, they were ordered according to the time of creation).

Moreover, from DEUS v.0.5.0, the `Engine` class has the `runStep()` method which advances the simulation by one event. The event queue is now returned by a method of the `Engine` class.


# `Event` and `NodeEvent` #

The it.unipr.ce.dsg.deus.core package of the DEUS API provides two abstract classes representing events: `Event` and `NodeEvent`. The first one is supposed to be for events that affect the system as a whole, without being associated to a specific node. Also log events should be implemented by subclassing `Event`. The `NodeEvent` abstract class, on the contrary, must be associated to a specific node.

`NodeEvent` has a boolean parameter "hasSameAssociatedNode": if you set it to true, all subsequent instances of your subclass will be associated to the node that was associated with the first instance of the subclass. The following example illustrate one possibile use case.

Suppose you have three subclasses of `NodeEvent`: a `BirthEvent` (that create a node), a `ConnectionEvent` (that connect the node with other nodes) and a `DiscoveryEvent` (that performs searches inside the node) - connected in a reference as illustrated in the previous paragraph. You want that, for the same node, 1 `BirthEvent`, 1 `ConnectionEvent` and N `DiscoveryEvent` are scheduled. Thus you will set the attribute "oneShot" to "true" in `BirthEvent` and `ConnectionEvent`, and to "false" in `DiscoveryEvent`. For the latter, you MUST also set:

```
<aut:param name="hasSameAssociatedNode" value="true" />
```

A different case is that in which you associate the process that manages `DiscoveryEvent` to the Engine. In this case you want that discovery events are generated at random time points, each time associated to a different node. In this case you MUST set

```
<aut:param name="hasSameAssociatedNode" value="false" />
```

But since "false" is the default value for "hasSameAssociatedNode", you can also avoid to declare the parameter.

**NOTE: starting from release 0.4.9, `Event` exposes all the methods of `NodeEvent`, which is now useless (it has been removed in the 0.5.0 release).**


# Seeds #

The user sets a seed in the `<aut:engine>` tag. This seed is used to generate N seeds, one for each of the _declared_ events, to initialize its own Random Number Generator (RNG). In this way, for example, if the initial seed is S, and there are two declared events (e1 and e2), their respective seeds will be S1 and S2. If their scheduling is associated to a random process, if we configure the simulation to run both kinds of event, the schedules of their instances will be:

  * from S1, for e1: t11, t12, t13, etc.

  * from S2, for e2: t21, t22, t23, etc.

If we configure the simulationto run, for example, only e1, the schedules of its instances will be again: t11, t12, t13, etc. If you modify the XML and declare another event e3 between the declaration of e1 and e2, in general the seeds will change (S1 will remain the same, S2 will become S3, and a new seed will be associated to e2). Be careful! Suggestion: declare all the events and use only those you need. If you add new events, add them after those already declared.

Important note: to get the default RNG, you can use

```
Random rng = Engine.getDefault().getSimulationRandom();
```

in the Java code of your events, nodes, etc., **but do not use it in their constructor!**


# The `clone()` method #
User-implemented subclasses of `Node` and `Event` are used by the DEUS parser to create objects for the simulation. For each node and event defined in the XML configuration file, the parser creates an object, then calls its `clone()` method to create other copies of such nodes and events (when necessary).
In the `clone()` method you can put the code you want.
Example: suppose you have a class called `Customer` of which you want N different instances, each one characterized by a different budget. Through the XML you can define the max budget:

```
        <aut:node id="customer" handler="it.unipr.ce.dsg.deus.example.socialmarket.Customer">
		<aut:params>
		    <aut:param name="maxBudget" value="100" />
		</aut:params>
	</aut:node>
```

In the constructor of the `Customer` class you place the code for getting such a value:

```
        public Customer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}
	
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(MAX_BUDGET))
			maxBudget = Integer.parseInt(params.getProperty(MAX_BUDGET));
	}
```

Finally, in the `clone()` method you generate a random value for the actual `budget` of each instance:

```
        public Object clone() {
		Customer clone = (Customer) super.clone();
                Random random = Engine.getDefault().getSimulationRandom();
                clone.setBudget(random.nextInt(maxBudget)+1);
		return clone;
	}
```


# Writing logs #

In DEUS, there are two logging strategies. One allows to have different logging granularity (**but slows down your simulations!**), by using Java instructions like

```
getLogger().fine(aLogMessage);
getLogger().info(anotherLogMessage);
```

and deciding which log messages to write by setting the logging level (info, fine, etc.) in the XML:

```
<aut:engine maxvt="2000000" seed="123456789"> 
     <aut:logger level="INFO" />
...
```

The other approach (**highly suggested**) is the one for producing a log file whose format is good for the GUI automator, in order to produce 2D-graph Gnuplot data (i.e. files with x,y columns).
In your Log event, use the following code:

```
AutomatorLogger a = new AutomatorLogger("./temp/logger");
ArrayList<LoggerObject> fileValue = new ArrayList<LoggerObject>();
```

then, if you want to log the value of a variable called "var", use:

```
fileValue.add(new LoggerObject("var", value));
a.write(Engine.getDefault().getVirtualTime(), fileValue);
```


# Utility Classes #

In the `it.unipr.ce.dsg.deus.util` package, starting from DEUS v.0.5.0, there is a class for computing confidence intervals about the variables which have been logged into the files which are stored in the temp/ folder.


# Running simulations #

We suggest to import deus in Eclipse and to run the simulations from there.

Project: deus

Main Classes:

`it.unipr.ce.dsg.deus.automator.gui.DeusAutomatorFrame` (Automator)

`it.unipr.ce.dsg.deus.core.Deus` (direct run)

Program Arguments:
the first parameter passed is the path of the XML configuration file, while the second is the name of the XML file that will be generated by the Automator

VM Arguments: `-Djava.util.logging.config.file=logging.properties -Xms256M -Xmx1000M`


Once the DEUS Automator GUI has loaded, you can configure which node paramaters, processes, etc. to vary in order to create many different simulations from the XML configuration file (that was introduced as first program argument).

For example, if a node has an integer parameter "x", you can add in the Node Parameter panel a rule to make "x" vary from an initial value, let say 1, to a final value, let say 5, with a step, e.g. 1. Then you can `Save` this configuration in a text file, to be reused in the future. If you press the `Run` button, 5 simulations are sequentially executed (one for each value of "x"). You can also set several seeds, other than default one, thus if you set 2 seeds, the number of simulations becomes 10.

Very important is the `GnuPlot File` panel. Here you can define files in which to put bidimensional data to be plotted.

For example, suppose that you have Logger that periodically computes the value of a variable "y". You can define a file (call it yVariation.txt, for example) with X label "VT" (virtual time) and Y Label "y". Then, after each simulation, you will have a yVariation.txt file reporting the values of y (first column) with respect to virtual time (second column).


If you want to run a set of simulations without using Eclipse and without running the automator's GUI, you have to type:

`java -Djava.util.logging.config.file=logging.properties -Xms256M -Xmx1000M it.unipr.ce.dsg.deus.automator.DeusAutomatorCommandLine simulationDescription.xml automatorLog.xml automatorConfig.xml`

where `simulationDescription.xml` is the file that describes the simulation (e.g. examples/life/Life.xml), `automatorLog.xml` is the name of the file that the automator will create for logging, and `automatorConfig.xml` is the file that describes the set of simulation to perform (previously created with te Automator GUI).

Of course you need to set the Java classpath in order to include DEUS libraries. The best strategy is to use Eclipse to create a "Run configuration" with the previously listed parameters, and to Export an ant's build.xml file for project deus. That build.xml will include tags with all the "Run configurations" associated with project deus.