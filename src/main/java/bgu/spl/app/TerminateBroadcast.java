package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Broadcast;

public class TerminateBroadcast implements Broadcast {
	private String name;
	
	public TerminateBroadcast (String name){
		this.name = name;
	}
}