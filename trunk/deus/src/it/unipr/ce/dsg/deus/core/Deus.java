package it.unipr.ce.dsg.deus.core;

import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

public class Deus {
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java " + Deus.class.getCanonicalName()
					+ " configfile.xml");
			return;
		}

		try {
			AutomatorParser automator = new AutomatorParser(args[0]);
			automator.getEngine().run();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}
}
