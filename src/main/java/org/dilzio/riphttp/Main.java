package org.dilzio.riphttp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.dilzio.riphttp.core.RipHttp;
import org.dilzio.riphttp.core.Route;
import org.dilzio.riphttp.handlers.HttpFileHandler;
import org.dilzio.riphttp.util.HttpMethod;

/**
 * Main class for running RipHttp as a stand alone server
 * @author dilzio
 */
public final class Main {
	private Main(){}
	/**
	 * @param args [0] should be the path to a java properties file where each property is a name value pair.
	 * These are read into an ApplicationParams class for further processing.  See the ParamEnum class for 
	 * the list of available options.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * 
	 */
	//TODO source properties file correctly. take doc root
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String docroot = System.getProperty("DOCROOT");
		
		if (null == docroot){
			System.out.println("DOCROOT System Property Must be set. Exiting");
			System.exit(0);
		}
		final RipHttp server = new RipHttp();
		
		//add a default file handler
		server.addHandlers(new Route("*", new HttpFileHandler(docroot), HttpMethod.GET));
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				//server.stop();
			}
		});
		
		if (args.length == 0){
			Future<?> shutdownFuture = server.start();
			shutdownFuture.get();
		}
	}

}
