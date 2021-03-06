import java.util.Enumeration;
import java.util.Properties;

/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

public class Settings {
	
	private static final Properties properties = new Properties();
	
	static{
		properties.put("DEPTH_PROBABILITY", "0.50");
		properties.put("MAX_DEPTH", "8");
		
		properties.put("SEED", "666");
		
		properties.put("MIN_PAYLOAD_SIZE", "0");
		properties.put("MED_PAYLOAD_SIZE", "0");
		properties.put("MAX_PAYLOAD_SIZE", "0");
		
		properties.put("MIN_REFS", "4");
		properties.put("MED_REFS", "8");
		properties.put("MAX_REFS", "32");
		
		properties.put("ACTIONS_PER_REQUEST", "10000");
		
		properties.put("ALLOCATE_ON_ROOTSET_RATIO", "0.10");
		
		properties.put("INIT_FRAMES", "32");
		properties.put("INIT_ALLOCS", "1000000");		
		properties.put("INIT_REF_CHANGES", "100");
		
		properties.put("LOCAL_ACTION_RATIO", "0");
		
		properties.put("READ", "0");
		properties.put("WRITE", "0");
		properties.put("REFCHANGE", "50");
		properties.put("ALLOC", "10");
		properties.put("ADD", "1");
		properties.put("REMOVE", "1");
		properties.put("BLOCK", "0");
		
		properties.put("SHARED_RESOURCE_SIZE", "4");
		properties.put("SHARED_RESOURCE_TIME", "100");
		
		properties.put("NEW_FRAME_ADDS", "16");
		properties.put("MAX_FRAME_SIZE", "100000");
		
	}
		
	public static int getIntProperty(String key){
		try{
			return Integer.parseInt(properties.getProperty(key));
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public static double getDoubleProperty(String key){
		try{
			return Double.parseDouble(properties.getProperty(key));
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public static synchronized void setProperty(String key, String value){
		properties.remove(key);
		properties.put(key, value);
	}

	public static int getRandAction(double rnd){
		
		double sum = 
				getIntProperty("READ") +
				getIntProperty("WRITE") +
				getIntProperty("REFCHANGE") +
				getIntProperty("ALLOC") +
				getIntProperty("ADD") +
				getIntProperty("REMOVE") +
				getIntProperty("BLOCK");
		
		
		double current = 0;
		
		current += getIntProperty("READ");
		if(rnd < current/sum){
			return 0;
		}
		
		current += getIntProperty("WRITE");
		if(rnd < current/sum){
			return 1;
		}
		
		current += getIntProperty("REFCHANGE");
		if(rnd < current/sum){
			return 2;
		}
		
		current += getIntProperty("ALLOC");
		if(rnd < current/sum){
			return 3;
		}
		
		current += getIntProperty("ADD");
		if(rnd < current/sum){
			return 4;
		}
		
		current += getIntProperty("REMOVE");
		if(rnd < current/sum){
			return 5;
		}
		
		return 6;
	}
	
	public static String getAllJSON(){
		String resp = "{\"settings\":[\n";
		
		Enumeration<Object> keys = properties.keys();
		
		while(keys.hasMoreElements()){
			String key = (String) (keys.nextElement());
			resp+="\"" + key + "\": \""+ properties.getProperty(key) +"\",\n";
		}
			
			
		resp += "]}";
			
		return resp;
	}
	
	public static String getAllCommented(){
		String resp = "% Settings:\n";
		
		Enumeration<Object> keys = properties.keys();
		
		while(keys.hasMoreElements()){
			String key = (String) (keys.nextElement());
			resp += "% "+key + ": "+ properties.getProperty(key) +"\n";
		}
			
			
		return resp;
	}
}
