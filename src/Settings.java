/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

public class Settings {
	
	public volatile static double DEPTH_PROBABILITY = 0.75;
	public volatile static int MAX_DEPTH = 7;
	
	public volatile static int SEED = 0x666;
	
	public volatile static int MIN_PAYLOAD_SIZE = 8;
	public volatile static int MED_PAYLOAD_SIZE = 24;
	public volatile static int MAX_PAYLOAD_SIZE = 4196;
	
	public volatile static int MIN_REFS = 0;
	public volatile static int MED_REFS = 8;
	public volatile static int MAX_REFS = 4196;
	
	public volatile static int ACTIONS_PER_REQUEST = 1000;
	
	public volatile static double OBJECTS_DIE_YOUNG_BIAS = 0.99;
	public volatile static double ALLOCATE_ON_ROOTSET_RATIO = 0.25;
	
	public volatile static int INIT_ALLOCS  = 100000;
	public volatile static int INIT_REF_CHANGES  = 10000;
	
	public volatile static double LOCAL_ACTION_RATIO = 0.5;
	
	//[0]=>READ, [1]=>WRITE, [2]=>REFCHANGE, [3]=>ALLOCATE, [4]=>ADD, [5]=>REMOVE
	public volatile static double ACTION_RATIOS[] = {0, 0, 25, 25, 25, 25};
	
	public static double getProbRange(int actionId){
		double sum = 0;
		for(double prob: ACTION_RATIOS){
			sum += prob;
		}
		
		double range = 0;
		for(int i = 0; i <= actionId; i++){
			range += ACTION_RATIOS[i]/sum;
		}
		return range;
	}
}
