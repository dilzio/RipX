package org.dilzio.riphttp.stp;

public interface RestartPolicy {

	void apply(RunnableWrapper rw);

}
