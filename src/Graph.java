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
	private final ConcurrentLinkedDeque<GraphNode> root = new ConcurrentLinkedDeque<GraphNode>();
	
	public final Semaphore sharedResource = new Semaphore(Settings.getIntProperty("SHARED_RESOURCE_SIZE"));
	
	private final boolean isGlobal;
	
	public Graph(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	private void read(){
		GraphNode obj = getRandomObj(true);
		if(obj != null){
			obj.readRand();
		}
	}
	

	private void write() {
		GraphNode obj = getRandomObj(true);
		if(obj != null){
			obj.writeRand();
		}		
	}
	
	public void changeRef(){
		GraphNode parent = getRandomObj(false);
		GraphNode child = getRandomObj(false);
		
		if(parent!=null){
			parent.setRandRef(child);
		}
	}
	
	public void allocate(){
		GraphNode parent = getRandomObj(false);
		GraphNode child = new GraphNode(isGlobal);
		
		if(parent!=null && Distribution.randU() > Settings.getDoubleProperty("ALLOCATE_ON_ROOTSET_RATIO")){
			parent.setRandRef(child);
			Tracing.del(child, isGlobal);
		} else {
			root.add(child);
		}
	}
	
	public void add(){
		GraphNode obj = getRandomObj(true);
		
		if(obj!=null){
			if(!root.contains(obj)){
				root.add(obj);
				Tracing.add(obj, isGlobal);
			}
		}
	}
	
	private synchronized void remove(){
		if(root.isEmpty()){
			return;
		}
		
		long rootCount = root.size();
		
		long id = (long) (rootCount - 1 - (rootCount-1)*Distribution.rand(0, 1, Settings.getDoubleProperty("OBJECTS_DIE_YOUNG_BIAS")));
		
		GraphNode obj = getRoot(id);
		
		root.remove(obj);
		Tracing.del(obj, isGlobal);
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
	
	private GraphNode getRandomObj(boolean uniform){
		if(root.isEmpty()){
			return null;
		}
		
		long id;
		long rootCount = root.size();
		
		if(uniform){
			id = Distribution.randULong(rootCount);
		} else {
			id = (long) (rootCount - 1 - (rootCount-1)*Distribution.rand(0, 1, Settings.getDoubleProperty("OBJECTS_DIE_YOUNG_BIAS")));
		}
		
		GraphNode obj = getRoot(id);
		
		if(obj==null){
			return null;
		}
		
		int i = 0;
		while(Distribution.randU() < Settings.getDoubleProperty("DEPTH_PROBABILITY") && i++ < Settings.getIntProperty("MAX_DEPTH")){
			GraphNode child = obj.getRandRef();
			if(child == null){
				return obj;
			}
			obj = child;
		}
		
		return obj;
	}
	
	private GraphNode getRoot(long pos){
		Iterator<GraphNode> it = root.descendingIterator();
		for(long i=0; i<pos && it.hasNext(); i++){
			it.next();
		}
		if(it.hasNext()){
			return it.next();
		} else {
			return null;
		}
	}

	public void doRandAction(PrintWriter out) {
		double rnd = Distribution.randU();
		
		int action = Settings.getRandAction(rnd);

		doAction(action);	
	}
	
	public synchronized void emptyAllAndGC(){
		root.clear();
		System.gc();
	}

	private void doAction(int i) {
		switch(i){
		case 0:
			read();
			break;
		case 1:
			write();
			break;
		case 2:
			changeRef();
			break;
		case 3:
			allocate();
			break;
		case 4:
			add();
			break;
		case 5:
			remove();
			break;
		case 6:
			block();
			break;
		}
	}

	public void empty() {
		Iterator<GraphNode> it = root.descendingIterator();
		for(long i=0; it.hasNext(); i++){
			GraphNode obj = it.next();
			Tracing.del(obj, false);
		}
		root.clear();
	}
}
