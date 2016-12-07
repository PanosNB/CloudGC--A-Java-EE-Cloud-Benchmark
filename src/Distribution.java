/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

import java.util.Random;

public class Distribution {
	
	private Random rand;
	
	public Distribution(){
		rand = new Random(Settings.getIntProperty("SEED"));
	}
	
	public void reSeed(){
		rand = new Random(Settings.getIntProperty("SEED"));
	}
	
	public double randU(){
		return rand.nextDouble();
	}
	
	public int randUInt(int max){
		if(max>=1){
			return rand.nextInt(max);
		} else {
			return 0;
		}
	}
	
	public long randULong(long max){
		if(max>=1){
			return rand.nextLong() % max;
		} else {
			return 0;
		}
	}

	public double rand(double min, double max, double med){
		double U = randU();
		
		double delta = (min-max)*(min-max)-4*(med-min)*(max-med);
		if(delta < 0){
			return U;
		}
		
		double x1, x2;
		x1 = ((max-min)+Math.sqrt(delta))/(2*(med-min));
		x2 = ((max-min)+Math.sqrt(delta))/(2*(med-min));
		
		double x = (x1==1)?x2:x1;
		
		if(x == 1){
			return U*(max-min)+min;
		}
				
		double a = 2*Math.log(x);
		double b = (max-min)/(Math.exp(a-1)-Math.exp(-1));
		double d = min - b*Math.exp(-1);
		
		return b*Math.exp(a*U-1) + d;
	}
}
