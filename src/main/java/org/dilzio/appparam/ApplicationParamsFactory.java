package org.dilzio.appparam;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ApplicationParamsFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationParamsFactory.class);
    private static final ApplicationParamsFactory INSTANCE = new ApplicationParamsFactory();

    private ApplicationParamsFactory() {/*no op private constructor*/}

    public static ApplicationParamsFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new ApplicationParams from a standard java properties file.  If overlayEnvVars is true then any
     * param in the property file will be overridden by any properties set with -D when the JVM is started. So precedence of prop
     * setting is -D env var > property file > default hardcoded.
     *
     * @param path
     * @param overlayEnvVars
     * @return
     */
    public ApplicationParams newParams(final Enum[] enumList, final String path, final boolean overlayEnvVars) {

        if (null == enumList || enumList.length == 0) {
            throw new IllegalArgumentException("Please provide non-empty parameter enum list");
        }
        ApplicationParams params = new ApplicationParams();
        Properties p = loadPropertiesFromConfig(path);

        if (null != p) {
            loadParamsFromProperties(enumList, params, p);
        }

        if (overlayEnvVars) {
            enrichFromEnv(enumList, params);
        }
        return params;

    }

    private void loadParamsFromProperties(final Enum[] enumList, final ApplicationParams params, final Properties p) {
        for (Enum e : enumList) {
            String val = (String) p.get(e.toString());
            if (null == val) {
                continue;
            }

            params.setParam((DefaultingEnum) e, val.trim());
        }
    }

    private Properties loadPropertiesFromConfig(final String path) {
        if (Strings.isNullOrEmpty(path)) {
            LOG.warn("Config file path is empty");
            return null;
        }
        Properties p = new Properties();

        //if path arg is null only load from env
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            p.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file.", e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {  //NOPMD
                    //ignore
                }
            }
        }
        return p;
    }


    private ApplicationParams enrichFromEnv(final Enum[] enumList, final ApplicationParams<DefaultingEnum> params) {
        for (int i = 0; i < enumList.length; i++) {
            DefaultingEnum item = (DefaultingEnum) enumList[i];
            String val = System.getProperty(item.toString());

            if (null == val) {
                continue;
            } else {
                params.setParam(item, val.trim());
            }
        }

        return params;
    }


}
