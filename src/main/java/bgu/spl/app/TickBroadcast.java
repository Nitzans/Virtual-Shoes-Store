package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
	
	private int brodTick;
	
	public int getBrodTick() {
		return brodTick;
	}

	public TickBroadcast(int brodTick) {
		super();
		this.brodTick = brodTick;
	}
	
	
}
