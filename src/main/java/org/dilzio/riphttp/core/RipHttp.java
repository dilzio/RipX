package org.dilzio.riphttp.core;

import org.dilzio.appparam.ApplicationParams;
import org.dilzio.appparam.ApplicationParamsFactory;
import org.dilzio.ripx.RipX;
import org.dilzio.ripx.RipXProperties;

/**
 * User: Matt C.
 * Date: 3/12/14
 *
 * Http Server built on RipX server framework.
 *
 * On construction will take application parameters + a list of Routes which define mappings between HTTP request paths
 * and handlers.
 *
 * Lifecycle methods (start(),stop(),join(),isRunning()) delegate to wrappered RipX instance
 */
public class RipHttp {

    private final RipX<HttpConnectionEvent,HttpWorker> _ripxServer;

    public RipHttp(final String configFilepath, final Route... routes) {
       ApplicationParams<RipHttpProperties> ripHttpProps = ApplicationParamsFactory.getInstance().newParams(RipHttpProperties.values(), configFilepath, true);
        validateParams(ripHttpProps, routes);
        _ripxServer = new RipX<>(configFilepath,getHttpConnectionEventFactory(), getHttpWorkerFactory(ripHttpProps, routes), getListenerDaemonFactory(ripHttpProps));
    }

    public RipHttp(final ApplicationParams<RipHttpProperties> ripHttpProps, final ApplicationParams<RipXProperties> ripxProps,
                   final Route... routes) {
        validateParams(ripHttpProps, routes);
        _ripxServer = new RipX<>(ripxProps,getHttpConnectionEventFactory(), getHttpWorkerFactory(ripHttpProps, routes), getListenerDaemonFactory(ripHttpProps));
    }


    private ListenerDaemonFactory getListenerDaemonFactory(final ApplicationParams<RipHttpProperties> ripHttpParams) {
        return new ListenerDaemonFactory(ripHttpParams);
    }

    private HttpConnectionEventFactory getHttpConnectionEventFactory() {
        return new HttpConnectionEventFactory();
    }

    private HttpWorkerFactory getHttpWorkerFactory(final ApplicationParams<RipHttpProperties> ripHttpProps, final Route[] routes) {
        return new HttpWorkerFactory(ripHttpProps, routes);
    }

    private void validateParams(final ApplicationParams<RipHttpProperties> ripHttpProps, final Route[] routes) {
        if (null == ripHttpProps) {
            throw new IllegalArgumentException("appParams cannot be null");
        }

        if (null == routes || routes.length == 0){
            throw new IllegalArgumentException("routes cannot be empty. Please configure routes");
        }
    }

    public void start(){
        _ripxServer.start();
    }

    public void stop(){
        _ripxServer.stop();
    }

    public void join() throws InterruptedException {
        _ripxServer.join();
    }

    public boolean isRunning(){
        return _ripxServer.isRunning();
    }

}
