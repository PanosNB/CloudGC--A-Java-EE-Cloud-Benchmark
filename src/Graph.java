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

	private void read(Distribution dist){
		GraphNode obj = getRandomObj(true, dist, false);
		if(obj != null){
			obj.readRand(dist);
		}
	}
	

	private void write(Distribution dist) {
		GraphNode obj = getRandomObj(true, dist, false);
		if(obj != null){
			obj.writeRand(dist);
		}		
	}
	
	public void changeRef(Distribution dist){
		GraphNode parent = getRandomObj(false, dist, false);
		GraphNode child = getRandomObj(false, dist, false);
		
		if(parent!=null){
			parent.setRandRef(child, dist);
		}
	}
	
	public void allocate(Distribution dist){
		GraphNode parent = getRandomObj(false, dist, true);
		GraphNode child = new GraphNode(isGlobal, dist);
		
		if(parent!=null && dist.randU() > Settings.getDoubleProperty("ALLOCATE_ON_ROOTSET_RATIO")){
			parent.setRef(parent.getFreeSlot(), child);
			Tracing.del(child, isGlobal);
		} else {
			root.add(child);
		}
	}
	
	public void add(Distribution dist){
		GraphNode obj = getRandomObj(true, dist, false);
		
		if(obj!=null){
			if(!root.contains(obj)){
				root.add(obj);
				Tracing.add(obj, isGlobal);
			}
		}
	}
	
	private synchronized void remove(Distribution dist){
		if(root.isEmpty()){
			return;
		}
		
		long rootCount = root.size();
		
		long id = (long) (rootCount - 1 - (rootCount-1)*dist.rand(0, 1, Settings.getDoubleProperty("OBJECTS_DIE_YOUNG_BIAS")));
		
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
	
	private GraphNode getRandomObj(boolean uniform, Distribution dist, boolean noOverwrite){
		if(root.isEmpty()){
			return null;
		}
		
		long id;
		long rootCount = root.size();
		
		if(uniform){
			id = dist.randULong(rootCount);
		} else {
			id = (long) (rootCount - 1 - (rootCount-1)*dist.rand(0, 1, Settings.getDoubleProperty("OBJECTS_DIE_YOUNG_BIAS")));
		}
		
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
		
		if(noOverwrite){
			int j = 0;
			while(obj.getFreeSlot()==-1){
				obj = obj.getRandRef(dist);
				if(obj == null){
					return null;
				}
				j++;
				
				if(j > Settings.getIntProperty("MAX_NO_OVERWRITE_TRIES")){
					return null;
				}
			}
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

	public void doRandAction(PrintWriter out, Distribution dist) {
		double rnd = dist.randU();
		
		int action = Settings.getRandAction(rnd);

		doAction(action, dist);	
	}
	
	public synchronized void emptyAllAndGC(){
		root.clear();
		System.gc();
	}

	private void doAction(int i, Distribution dist) {
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
		case 4:
			add(dist);
			break;
		case 5:
			remove(dist);
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
