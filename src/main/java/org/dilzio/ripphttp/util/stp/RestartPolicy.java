package org.dilzio.ripphttp.util.stp;

public interface RestartPolicy {
	String ONE_FOR_ONE = "ONE_FOR_ONE";

	void apply(RunnableWrapper rw);

}
