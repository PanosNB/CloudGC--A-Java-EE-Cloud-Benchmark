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
		properties.put("DEPTH_PROBABILITY", "0.75");
		properties.put("MAX_DEPTH", "7");
		
		properties.put("SEED", "0x666");
		
		properties.put("MIN_PAYLOAD_SIZE", "8");
		properties.put("MED_PAYLOAD_SIZE", "24");
		properties.put("MAX_PAYLOAD_SIZE", "4196");
		
		properties.put("MIN_REFS", "0");
		properties.put("MED_REFS", "8");
		properties.put("MAX_REFS", "4196");
		
		properties.put("ACTIONS_PER_REQUEST", "1000");
		
		properties.put("OBJECTS_DIE_YOUNG_BIAS", "0.99");		
		properties.put("ALLOCATE_ON_ROOTSET_RATIO", "0.25");
		
		properties.put("INIT_ALLOCS", "50000");		
		properties.put("INIT_REF_CHANGES", "5000");
		
		properties.put("LOCAL_ACTION_RATIO", "0.5");
		
		properties.put("READ", "0");
		properties.put("WRITE", "0");
		properties.put("REFCHANGE", "20");
		properties.put("ALLOC", "20");
		properties.put("ADD", "20");
		properties.put("REMOVE", "20");
		properties.put("BLOCK", "20");
		
		properties.put("SHARED_RESOURCE_SIZE", "2");
		properties.put("SHARED_RESOURCE_TIME", "500");
		
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
		if(current/sum < rnd){
			return 0;
		}
		
		current += getIntProperty("WRITE");
		if(current/sum < rnd){
			return 1;
		}
		
		current += getIntProperty("REFCHANGE");
		if(current/sum < rnd){
			return 2;
		}
		
		current += getIntProperty("ALLOC");
		if(current/sum < rnd){
			return 3;
		}
		
		current += getIntProperty("ADD");
		if(current/sum < rnd){
			return 4;
		}
		
		current += getIntProperty("REMOVE");
		if(current/sum < rnd){
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
}
