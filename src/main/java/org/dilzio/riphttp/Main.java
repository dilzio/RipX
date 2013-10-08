package org.dilzio.riphttp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.dilzio.riphttp.core.RipHttp;
import org.dilzio.riphttp.core.Route;
import org.dilzio.riphttp.handlers.HttpFileHandler;
import org.dilzio.riphttp.util.HttpMethod;

/**
 * 
 * @author dilzio
 */
public class Main {
	/**
	 * Main class for running RipHttp as a standalone server
	 * @param args arg[0] should be the path to a java properties file where each property is a name value pair.
	 * These are read into an ApplicationParams class for further processing.  See the ParamEnum class for 
	 * the list of available options.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * 
	 */
	//TODO source properties file correctly. take docroot
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final RipHttp server = new RipHttp();
		
		//add a default file handler
		server.addHandlers(new Route(HttpMethod.GET, "*", new HttpFileHandler("C:\\tmp")));
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				server.stop();
			}
		});
		
		if (args.length == 0){
			Future<?> shutdownFuture = server.start();
			shutdownFuture.get();
		}
	}

}
