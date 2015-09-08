package edu.unc.flashlight.shared.model.SAINT;

import java.io.Serializable;

import edu.unc.flashlight.shared.util.Constants;

public class SaintParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int virtualControls = Constants.SAINT_VIRTUAL_CONTROLS;
	private int numReplicates = Constants.SAINT_NUM_REPLICATES;
	
	public SaintParameters() {
		
	}
	
	public SaintParameters(int virtualControls, int numReplicates) {
		setVirtualControls(virtualControls);
		setNumReplicates(numReplicates);
	}
	
	public int getVirtualControls() {
		return virtualControls;
	}
	
	public void setVirtualControls(int virtualControls) {
		if (virtualControls < Constants.SAINT_VIRTUAL_CONTROLS_MIN) virtualControls = Constants.SAINT_VIRTUAL_CONTROLS_MIN;
		else if (virtualControls > Constants.SAINT_VIRTUAL_CONTROLS_MAX) virtualControls = Constants.SAINT_VIRTUAL_CONTROLS_MAX;
		this.virtualControls = virtualControls;
	}
	
	public int getNumReplicates() {
		return numReplicates;
	}
	
	public void setNumReplicates(int numReplicates) {
		if (numReplicates < Constants.SAINT_NUM_REPLICATES_MIN) numReplicates = Constants.SAINT_NUM_REPLICATES_MIN;
		else if (numReplicates > Constants.SAINT_NUM_REPLICATES_MAX) numReplicates = Constants.SAINT_NUM_REPLICATES_MAX;
		this.numReplicates = numReplicates;
	}
	
}
