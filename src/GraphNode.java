/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

public class GraphNode {
	private final GraphNode[] children;
	private byte[] payload;
	private final long id;
	private volatile static long prevId = 0;

	public GraphNode(boolean isGlobal) {		
		int payloadSize = (int) Distribution.rand(Settings.getIntProperty("MIN_PAYLOAD_SIZE"), Settings.getIntProperty("MAX_PAYLOAD_SIZE"), Settings.getIntProperty("MED_PAYLOAD_SIZE"));
		int n = (int) (Distribution.rand(Settings.getIntProperty("MIN_REFS"), Settings.getIntProperty("MAX_REFS"), Settings.getIntProperty("MED_REFS")));
		
		if(payloadSize%8 != 0){
			payloadSize += (8-payloadSize%8);
		}
		
		children = new GraphNode[n];
		payload = new byte[payloadSize];
		
		synchronized(GraphNode.class){
			id = ++prevId;
		}
		
		Tracing.alloc(this, isGlobal);
	}
	
	public GraphNode getRef(int i){
		if(i<0 || i>=children.length){
			return null;
		}
		
		Tracing.read(this, i);
		
		return children[i];
	}
	
	public GraphNode getRandRef(){
		if(children.length == 0){
			return null;
		}
		int i=Distribution.randUInt(children.length);
		
		Tracing.read(this, i);
		
		return children[i];
	}
	
	public void setRandRef(GraphNode child){
		if(children.length == 0){
			return;
		}
		int i=Distribution.randUInt(children.length);
		
		Tracing.write(this, i, child);
		
		children[i] = child;
	}
	
	public synchronized void setRef(int i, GraphNode child){
		if(i<0 || i>=children.length){
			return;
		}
		
		Tracing.write(this, i, child);
		
		children[i] = child;
	}
	
	public byte readRand(){
		if(payload.length == 0){
			return 0;
		}
		int i=Distribution.randUInt(payload.length);
		
		Tracing.readPrim(this, i);
		
		return payload[i];
	}
	
	public void writeRand(){
		if(payload.length == 0){
			return;
		}
		int i=Distribution.randUInt(payload.length);
		
		Tracing.store(this, i);
		
		payload[i]++;
	}
	
	public long getId(){
		return id;
	}
	
	public long getSize(){
		return 8 + 8*children.length + payload.length; 
	}
	
	public int getPayloadLength(){
		return payload.length;
	}
	
	public int getSlots(){
		return children.length;
	}
	
	public long getPayloadOffset(){
		return 8 + 8*children.length;
	}
}
