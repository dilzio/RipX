package org.dilzio.ripphttp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.dilzio.riphttp.util.ApplicationParams;
import org.dilzio.riphttp.util.ParamEnum;

public final class ApplicationParamsFactory {
	private static final ApplicationParamsFactory _instance = new ApplicationParamsFactory();
	
	private ApplicationParamsFactory(){/*no op private constructor*/}
	
	public static ApplicationParamsFactory getInstance() {
		return _instance;
	}

	/**
	 * Create a new ApplicationParams from a standard java properties file.  If overlayEnvVars is true then any
	 * param in the property file will be overridden by any properties set with -D when the JVM is started. So precedence of prop
	 * setting is -D env var > property file > default hardcoded.
	 * @param path
	 * @param overlayEnvVars
	 * @return
	 */
	public ApplicationParams newParams(final String path, final boolean overlayEnvVars) {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(path));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load properties from file.", e);
		}
		
		ApplicationParams params = new ApplicationParams();
		
		for (ParamEnum pe : ParamEnum.values()){
			String val = (String) p.get(pe.toString());
			if (null == val){
				continue;
			}
			
			params.setParam(pe, val.trim());
		}
	
		if (overlayEnvVars){
			enrichFromEnv(params);
		}
		return params;
	}
	
	public ApplicationParams newParamsFromEnvironment(){
		return enrichFromEnv(new ApplicationParams());
	}
	
	private ApplicationParams enrichFromEnv(ApplicationParams params){
		for (ParamEnum pe : ParamEnum.values()){
			String val = (String) System.getProperty(pe.toString());
			if (null == val){
				continue;
			}
			
			params.setParam(pe, val.trim());
		}
		return params;
	}
	


}
