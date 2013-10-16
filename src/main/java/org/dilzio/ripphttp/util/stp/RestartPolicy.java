package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public interface RestartPolicy {
	String ONE_FOR_ONE = "ONE_FOR_ONE";
	void apply(ExecutorService _internalPool, ConcurrentMap<Thread, RunnableWrapper> _internalMap);

	void apply2(RunnableWrapper rw);

}
