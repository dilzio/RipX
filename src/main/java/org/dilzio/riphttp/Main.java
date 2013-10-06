package org.dilzio.riphttp;

import org.dilzio.riphttp.core.RipHttp;

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
	 */
	public static void main(String[] args) {
		if (args.length == 0){
			new RipHttp().start();
		}
		//TODO: source properties file
	}

}
