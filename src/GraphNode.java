/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

public class GraphNode {
	private final GraphNode[] children;
	private byte[] payload;

	public GraphNode() {		
		int payloadSize = (int) Distribution.rand(Settings.MIN_PAYLOAD_SIZE, Settings.MAX_PAYLOAD_SIZE, Settings.MED_PAYLOAD_SIZE);
		int n = (int) (Distribution.rand(Settings.MIN_REFS, Settings.MAX_REFS, Settings.MED_REFS)); 		
		
		children = new GraphNode[n];
		payload = new byte[payloadSize];
	}
	
	public GraphNode getRef(int i){
		if(i<0 || i>=children.length){
			return null;
		}
		return children[i];
	}
	
	public GraphNode getRandRef(){
		if(children.length == 0){
			return null;
		}
		return children[Distribution.randUInt(children.length)];
	}
	
	public void setRandRef(GraphNode child){
		if(children.length == 0){
			return;
		}
		children[Distribution.randUInt(children.length)] = child;
	}
	
	public synchronized void setRef(int i, GraphNode child){
		if(i<0 || i>=children.length){
			return;
		}
		
		children[i] = child;
	}
	
	public byte readRand(){
		if(payload.length == 0){
			return 0;
		}
		return payload[Distribution.randUInt(payload.length)];
	}
	
	public void writeRand(){
		if(payload.length == 0){
			return;
		}
		payload[Distribution.randUInt(payload.length)]++;
	}
}
