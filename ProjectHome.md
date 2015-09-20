DEUS is a general-purpose tool for creating simulations of complex systems. It provides a Java API which allows to implement

  * nodes (i.e. the parts which interact in a complex system, leading to emergent behaviors.. humans, pets, cells, robots, intelligent agents, etc.)
  * events (e.g. node births/deaths, interactions among nodes, interactions with the environment, logs, etc. - briefly, whatever you want!)
  * processes (stochastic or deterministic, they regulate the timeliness of events)

Configuration scripts for simulations can be written in XML (using the DEUS schema) or using a visual editor.

Currently you can checkout the source code, which comes as an Eclipse project. If you do not need the source code, you can download deus-x.y.z.zip, which contains binaries and the correct folder structure.


**NEWS**

  * DEUS has moved to **`GitHub`** **http://github.com/dsg-unipr/deus/**

  * June 2014 - The new **DEUS v0.6.0** has been released! From this version, you are allowed to choose your preferred random number generator. Moreover, some bugs have been fixed, and several optimizations have been performed. Go to the new **[DOWNLOADS section](http://dsg.ce.unipr.it/?q=node/86)**.

  * March 2014 - The new **DEUS v0.5.1** has been released! Some bugs have been fixed, and several optimizations have been performed. We are preparing a technical report with a performance evaluation. Go to the new **[DOWNLOADS section](http://dsg.ce.unipr.it/?q=node/86)**.

  * July 2013 - **A new paper about DEUS - entitled "Simulating Mobile and Distributed Systems with DEUS and ns-3" - has been presented at HPCS 2013**, in Helskinki, Finland. The paper describes the approach we use to integrate DEUS models and ns-3 models. You can find the paper in the downloads section. **Please, cite this one, in your papers.**

  * March 2013 - The new **DEUS v.0.5.0** has been released! Its event queue's management has been improved, and the `runStep()` method has been added to the `Engine` class, allowing to execute step-by-step simulations. Moreover, new probability density functions (and related `Process`es) have been introduced: Weibull, Shifted Pareto, Multimodal. Finally, there is an utility class for computing confidence intervals. Check the tutorial for more insights!

  * A new paper about DEUS has been presented at SIMUTools 2012, in Desenzano, Italy. It describes the mobility package and the simulation of cooperating vehicles moving along the roads of a city. You can find the paper in the downloads section.

  * DEUS v0.4.9 has been released! Several bugs have been fixed, and now the class `Event` has subsumed `NodeEvent` (that will be removed from v0.5.0).

  * Towards relaese 0.4.9 - On wednesday, the 10th of august 2011 source code has been updated: the examples folder has been removed from svn (examples were obsolete); in-code documentation has been translated to English (when it was in Italian); a new class called `Distributions` has been added to the package called `it.unipr.ce.dsg.deus.util`: it provides a collection of functions that return numbers with different statistical distributions; Poisson processes in `it.unipr.ce.dsg.deus.impl.process` have been updated in order to use `Distributions`.

  * DEUS v0.4.8 has been released, with the brand new mobility package, and a fix in the `LogNodeDegreeEvent`.

  * DEUS v0.4.7 has been released! This new version has new important features that will facilitate the execution of simulations, and the collection of logs. Moreover, a critical bug has been fixed in the visual editor.

  * Release 0.4.6 has a new interesting feature, allowing to run a sequence of simulations in the console, using the automator but without opening its GUI. This is useful if you need to run a set of simulations on a remote machine. See the tutorial for details.

  * In version 0.4.5 we have added a couple of methods to the Engine class: removeNode() and getNodeByKey(int key, String id).

  * In version 0.4.4 we have fixed some bugs that slowed down simulations. To write logs, we suggest not to use the getLogger.info(), getLogger.fine(), etc. mechanism. Read the tutorial, where the other (more useful and performant) logging mechanism is explained.

  * In version 0.4.3 we have introduced a new implementation of the event queue, that now is much more efficient: the insertion of a new event in the right position, according to the timestamp, costs now O(logN) - while previously the cost was O(NlogN).

  * The Visual Editor has been included in the source tree, and the related User Manual (italian version) has been added to the "Downloads" section. There you find also the latest version of deus-lib.jar.