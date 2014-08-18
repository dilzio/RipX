package org.dilzio.ripx;

/**
 * User: Matt C.
 * Date: 3/12/14
 *
 * Interface to be implemented by producer threads
 */
public interface RipXProducer extends Runnable {
    void stop();
}
