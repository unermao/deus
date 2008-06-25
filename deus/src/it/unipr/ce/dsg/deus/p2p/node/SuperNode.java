package it.unipr.ce.dsg.deus.p2p.node;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;

import java.util.Properties;

public class SuperNode extends Node  {

	private int kMax = 0;
	private float downloadBandwidth = 0;
	private float uploadBandwidth = 0;
	private float cpu = 0;
	private float ram = 0;
	
	public SuperNode(String id, Properties params)
			throws InvalidParamsException {
		super(id, params);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {

	}
	
	public Object clone() {
		SuperNode clone = (SuperNode) super.clone();
		clone.kMax = 0;
		clone.downloadBandwidth = 0;
		clone.uploadBandwidth = 0;
		clone.cpu = 0;
		clone.ram = 0;
		return clone;
	}

	public int getKMax() {
		return kMax;
	}

	public void setKMax(int max) {
		kMax = max;
	}

	public float getDownloadBandwidth() {
		return downloadBandwidth;
	}

	public void setDownloadBandwidth(float downloadBandwidth) {
		this.downloadBandwidth = downloadBandwidth;
	}

	public float getUploadBandwidth() {
		return uploadBandwidth;
	}

	public void setUploadBandwidth(float uploadBandwidth) {
		this.uploadBandwidth = uploadBandwidth;
	}

	public float getCpu() {
		return cpu;
	}

	public void setCpu(float cpu) {
		this.cpu = cpu;
	}

	public float getRam() {
		return ram;
	}

	public void setRam(float ram) {
		this.ram = ram;
	}
}
