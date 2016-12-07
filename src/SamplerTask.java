import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;


public class SamplerTask implements Runnable{

	private long LOW;
	private long HIGH;
	private long pid = -1;
	private PrintWriter out = null;
	private long[] prevThru = {-1, -1, -1};
	private long prevAcThru = -1, prevOutThru = -1;
	private boolean lowSoftmx = false;
	private long thruThres = 1<<10;
	
	private MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	private ObjectName name;
	
	@Override
	public void run() {
		
		//init
		String processName =
				java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		pid = Long.parseLong(processName.split("@")[0]);
		
		HIGH = Runtime.getRuntime().maxMemory();
		LOW = HIGH/3;
		

		try {
			name=new ObjectName("java.lang:type=Memory");
			out = new PrintWriter(new FileOutputStream(new File(StartAVS.outPath + "out"), true));
			while(!Thread.interrupted()){

				doWork();

				Thread.sleep(1000);
			}
		} catch (FileNotFoundException | InterruptedException | MalformedObjectNameException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void doWork() {

		try {
			long thru = -1;
			long outThru = -1;
			//Find In Thru
			Scanner devNet = new Scanner(new File("/proc/net/dev"));
			int count = 0;
			while(devNet.hasNext()){

				if(devNet.hasNextLong()){
					count++;
					if(count==1){
						long curThru = devNet.nextLong(); 
						if(curThru != 0){
							if(prevAcThru>=0){
								thru = curThru - prevAcThru;
								
								prevThru[2] = prevThru[1];
								prevThru[1] = prevThru[0];
								prevThru[0] = thru;
								
							} else {
								thru = -1;
							}
							
	
							prevAcThru = curThru;
						}
					}
					if(count == 9){
						long curThru = devNet.nextLong();
						outThru = curThru - prevOutThru;
						prevOutThru = curThru;
						break;
					}
					
				}
				devNet.next();
			}
			if(devNet != null){
				devNet.close();
			}

			//Find VMS and RSS
			Scanner statm = new Scanner(new File("/proc/"+pid+"/statm"));
			long vms = statm.nextLong();
			long rss = statm.nextLong();
			if(statm != null){
				statm.close();
			}

			//Find used heap
			Runtime runtime = Runtime.getRuntime();
			long usedHeap = runtime.totalMemory() - runtime.freeMemory();

			if(thru >= 0){
				//Make vertical scaling decision
				decideVertScale(thru);

				//Print data
				out.println(thru+"\t"+(vms*4096)+"\t"+(rss*4096)+"\t"+usedHeap + "\t"+getSoftMX() + "\t" + outThru);
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block	
			e1.printStackTrace();
			return;
		} finally {
			out.flush();
		}
	}

	private void decideVertScale(long thru) {
		if(prevThru[0]<0 || prevThru[1]<0 || prevThru[2]<0){
			return;
		}
		
		//Update thruThres
		if(thru/2 > thruThres){
			thruThres = thru/2;
		}

		if(lowSoftmx){
			if(prevThru[0] > thruThres &&  prevThru[1] > thruThres && prevThru[2] > thruThres){
				lowSoftmx = false;
				setSoftMX();
				if(!StartAVS.noGC){
					System.gc();
				}
			}
		} else {
			if(prevThru[0] < thruThres &&  prevThru[1] < thruThres && prevThru[2] < thruThres){
				lowSoftmx = true;
				setSoftMX();
			}
		}
	}

	private void setSoftMX() {
		
		if(StartAVS.noChange){
			return;
		}
		
		long limit;
		
		if(lowSoftmx){
			limit = LOW;
		} else {
			limit = HIGH;
		}
		
		try {
			mbs.setAttribute(name, new Attribute("MaxHeapSize", limit));
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException
				| ReflectionException | InvalidAttributeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object getSoftMX(){
		try {
			return mbs.getAttribute(name, "MaxHeapSize");
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
