package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.dilzio.ripphttp.util.stp.RestartPolicy;
import org.dilzio.ripphttp.util.stp.RunnableWrapper;
import org.dilzio.ripphttp.util.stp.SupervisoryThreadPool;
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
					throw new RuntimeException("from runnable 1 catch block");
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
		
		SupervisoryThreadPool _underTest = new SupervisoryThreadPool(new OneForOneRestartPolicy(4), r1);				
		
		_underTest.start();
		
		Thread.sleep(4000000);
	}

}
