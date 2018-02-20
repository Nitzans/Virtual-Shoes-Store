package main.java.bgu.spl.mics.impl;

import main.java.bgu.spl.mics.MicroService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class LastIndexArray {

	private int fIndex;
	public CopyOnWriteArrayList<MicroService> array;

	public int getIndex(){
		return fIndex;
	}

	public LastIndexArray(){
		fIndex = 0;
		array = new CopyOnWriteArrayList<MicroService>();
	}

	public synchronized void updateI(){
		if (fIndex<array.size()-1)
			fIndex++;
		else 
			fIndex=0;
	}

	public void print(){
		System.out.print("The MicroServices: ");
		for (MicroService i : array){
			System.out.print(i.getName()+" ");
		}
		System.out.println();
	}
}
