package org.dilzio.ripphttp.util.stp;

public interface RestartPolicy {

	void apply(RunnableWrapper rw);

}
