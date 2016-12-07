/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public final class Graph {
	private final GraphNode[] root;
	private int top = -1;
	
	public final Semaphore sharedResource = new Semaphore(Settings.getIntProperty("SHARED_RESOURCE_SIZE"));
	
	public Graph(){
		root = new GraphNode[Settings.getIntProperty("MAX_FRAME_SIZE")];
	}

	private void read(Distribution dist){
		GraphNode obj = getRandomObj(dist, false);
		if(obj != null){
			obj.readRand(dist);
		}
	}
	

	private void write(Distribution dist) {
		GraphNode obj = getRandomObj(dist, false);
		if(obj != null){
			obj.writeRand(dist);
		}		
	}
	
	public void changeRef(Distribution dist){
		GraphNode parent = getRandomObj(dist, false);
		GraphNode child = getRandomObj(dist, false);
			
		if(parent!=null){
			parent.setRandRef(child, dist);
		}
	}
	
	public void allocate(Distribution dist){
		GraphNode parent = null;
		GraphNode child = new GraphNode(dist);
		
		if(dist.randU() > Settings.getDoubleProperty("ALLOCATE_ON_ROOTSET_RATIO")){
			parent = getRandomObj(dist, true);
		}
		
		if(parent != null){
			parent.setRef(parent.getFreeSlot(), child);
			//Tracing.del(child, isGlobal);
		} else {
			addToRoot(child);
		}
	}
	
	public void add(Distribution dist, Graph prevTop){
		GraphNode obj = prevTop.getRandomObj(dist, false);
		
		if(obj!=null){
			addToRoot(obj);
			//Tracing.add(obj, isGlobal);
		}
	}
	
	private void addToRoot(GraphNode obj){
		if(top + 1 < root.length){
			top++;
			root[top] = obj;
		}
	}
	
	private void block() {
		try{
			sharedResource.acquire();
			Thread.sleep(Settings.getIntProperty("SHARED_RESOURCE_TIME"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sharedResource.release();
		}
	}
	
	public GraphNode getRandomObj(Distribution dist, boolean noOverwrite){
		if(top == -1){
			return null;
		}
		
		int id;
		int rootCount = top + 1;
		
		id = (int) dist.randULong(rootCount);
		
		GraphNode obj = getRoot(id);
		
		if(obj==null){
			return null;
		}
		
		int i = 0;
		while(dist.randU() < Settings.getDoubleProperty("DEPTH_PROBABILITY") && i++ < Settings.getIntProperty("MAX_DEPTH")){
			GraphNode child = obj.getRandRef(dist);
			if(child == null){
				break;	
			}
			obj = child;
		}
		
		if(noOverwrite && obj.getFreeSlot()==-1){
			return null;
		}
		
		return obj;
	}
	
	private GraphNode getRoot(int pos){
		if(pos >= 0 && pos < top - 1){
			return root[pos];
		}
		return null;
	}
	
	public synchronized void emptyAllAndGC(){
		empty();
		System.gc();
	}

	public void doAction(int i, Distribution dist) {
		switch(i){
		case 0:
			read(dist);
			break;
		case 1:
			write(dist);
			break;
		case 2:
			changeRef(dist);
			break;
		case 3:
			allocate(dist);
			break;
		case 6:
			block();
			break;
		}
	}

	public void empty() {
		for(int i = 0; i < top - 1; i++){
			root[i] = null;
		}
		top = -1;
	}


	public void traverse() {
		for(int i = 0; i < top - 1; i++){
			System.out.println(root[i]);
		}	
	}
}
