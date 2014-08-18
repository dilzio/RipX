package org.dilzio.riphttp.stp;

public final class RunnableWrapperThreadLocal {
	private static final ThreadLocal<RunnableWrapper> _rwtl = new ThreadLocal<>();
	
	public static final RunnableWrapperThreadLocal _instance = new RunnableWrapperThreadLocal();
	
	private RunnableWrapperThreadLocal(){
		//for singleton
	}
	public void set(final RunnableWrapper rw) {
        _rwtl.set(rw);
    }

    public void unset() {
        _rwtl.remove();
    }

    public RunnableWrapper get() {
        return _rwtl.get();
    }
    
    public static RunnableWrapperThreadLocal getInstance(){
    	return _instance;
    }
}
