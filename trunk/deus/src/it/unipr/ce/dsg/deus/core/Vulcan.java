package it.unipr.ce.dsg.deus.core;

import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXBException;

public class Vulcan {
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java " + Vulcan.class.getCanonicalName()
					+ " configfile.xml");
			return;
		}

		try {
			AutomatorParser automator = new AutomatorParser(args[0]);
			automator.getEngine().run();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
