package org.dilzio.ripphttp.util.stp;

import org.junit.Test;

public class SupervisedThreadPoolTest {

	@Test
	public void happyPath() throws Exception{
		Runnable r1 = new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException("from runnable 1 catch block", e);
				}
				System.out.println("Throwing RTE from runnable 1");
				throw new RuntimeException("from runnable 1");
			}
			
		};
	
		Runnable r2 = new Runnable(){
			@Override
			public void run() {
				System.out.println("running runnable 2");
			}
			
		};
		
		SupervisoryThreadPool _underTest = new SupervisoryThreadPool(new OneForOneRestartPolicy(4), r1, r2);				
		
		_underTest.start();
		
		Thread.sleep(4000000);
	}

}
