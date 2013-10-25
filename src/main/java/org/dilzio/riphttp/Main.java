package org.dilzio.riphttp;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.core.RipHttp;
import org.dilzio.riphttp.core.Route;
import org.dilzio.riphttp.handlers.BasicOkResponseHttpRequestHandler;
import org.dilzio.riphttp.handlers.HttpFileHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.dilzio.riphttp.util.ParamEnum;
import org.dilzio.ripphttp.appparam.ApplicationParams;
import org.dilzio.ripphttp.appparam.ApplicationParamsFactory;

/**
 * Main class for running RipHttp as a stand alone server
 * 
 * @author dilzio
 */
public final class Main {
	private Main() {
	}

	/**
	 * @param args
	 *            [0] should be the path to a java properties file where each
	 *            property is a name value pair. These are read into an
	 *            ApplicationParams class for further processing. See the
	 *            ParamEnum class for the list of available options.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		final Logger LOG = LogManager.getFormatterLogger(Main.class.getName());
	
 		LOG.info("Welcome to RipHttp");
		ApplicationParams params = null;
		if (null == args[0]){
			params = ApplicationParamsFactory.getInstance().newParamsFromEnvironment();
			LOG.info("Loaded application params from System properties");
		}else{
			params = ApplicationParamsFactory.getInstance().newParams(args[0], true);
			LOG.info("Loaded application params from property file %s with system environment overlay", args[0]);
		}

		
		String docroot = params.getStringParam(ParamEnum.DOCROOT);
		LOG.info("Docroot set to: %s", docroot);
		final RipHttp server = new RipHttp(params);

		// add a default file handler
		server.addHandlers(new Route("*", new HttpFileHandler(docroot), HttpMethod.GET),
						   new Route("/foo", new BasicOkResponseHttpRequestHandler("OK"), HttpMethod.GET));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				server.stop();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		server.start();
		try{
			server.join();
		}catch (InterruptedException e){
			System.out.println("ignored");
		}
	}
}
