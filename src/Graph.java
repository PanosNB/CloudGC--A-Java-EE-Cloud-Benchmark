/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class Graph {
	private final ConcurrentLinkedDeque<GraphNode> root = new ConcurrentLinkedDeque<GraphNode>();
	
	public static final Graph globalGraph = new Graph(); 
	
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
		GraphNode child = new GraphNode();
		
		if(parent!=null && Distribution.randU() > Settings.ALLOCATE_ON_ROOTSET_RATIO){
			parent.setRandRef(child);
		} else {
			root.add(child);
		}
	}
	
	private void add(){
		GraphNode obj = getRandomObj(true);
		
		if(obj!=null){
			if(!root.contains(obj)){
				root.add(obj);
			}
		}
	}
	
	private synchronized void remove(){
		if(root.isEmpty()){
			return;
		}
		
		long rootCount = root.size();
		
		long id = (long) (rootCount - 1 - (rootCount-1)*Distribution.rand(0, 1, Settings.OBJECTS_DIE_YOUNG_BIAS));
		
		GraphNode obj = getRoot(id);
		
		root.remove(obj);
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
			id = (long) (rootCount - 1 - (rootCount-1)*Distribution.rand(0, 1, Settings.OBJECTS_DIE_YOUNG_BIAS));
		}
		
		GraphNode obj = getRoot(id);
		
		if(obj==null){
			return null;
		}
		
		int i = 0;
		while(Distribution.randU() < Settings.DEPTH_PROBABILITY && i++ < Settings.MAX_DEPTH){
			GraphNode child = obj.getRandRef();
			if(child == null){
				return obj;
			}
			obj = child;
		}
		
		return obj;
	}
	
	private GraphNode getRoot(long pos){
		GraphNode obj =  null;
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

	public void doRandAction() {
		double rnd = Distribution.randU();
		
		for(int i = 0; i < Settings.ACTION_RATIOS.length; i++){
			if(rnd < Settings.getProbRange(i)){
				doAction(i);
				return;
			}
		}		
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
		}
	}

}
