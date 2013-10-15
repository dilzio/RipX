package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public interface RestartPolicy {

	void apply(ExecutorService _internalPool, ConcurrentMap<Thread, RunnableWrapper> _internalMap);

}
