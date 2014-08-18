package org.dilzio.ripx;

/**
 * User: Matt C.
 * Date: 3/20/14
 * <p/>
 * <>
 */
public abstract class AbstractRipXEvent {
    private final long _id;
    private long _beginWriteTimestampMicros;
    private long _endWriteTimestampMicros;
    private long _readBeginTimestampMicros;
    private long _readEndTimestampMicros;
    private String _workerName;

    protected abstract void resetExtendedFields();

    protected AbstractRipXEvent(final long id) {
        _id = id;
    }

    public void reset(){
        _endWriteTimestampMicros = 0;
        _readBeginTimestampMicros = 0;
        _readEndTimestampMicros = 0;
        _workerName = null;
        resetExtendedFields();
    }
    public long getId(){
        return _id;
    }

public long getBeginWriteTimestampMicros() {
        return _beginWriteTimestampMicros;
    }

    public void setBeginWriteTimestampMicros(final long beginWriteTimestampMicros) {
        _beginWriteTimestampMicros = beginWriteTimestampMicros;
    }

    public long getEndWriteTimestampMicros() {
        return _endWriteTimestampMicros;
    }

    public void setEndWriteTimestampMicros(final long endWriteTimestampMicros) {
        _endWriteTimestampMicros = endWriteTimestampMicros;
    }

    public long getReadBeginTimestampMicros() {
        return _readBeginTimestampMicros;
    }

    public void setReadBeginTimestampMicros(final long readBeginTimestampMicros) {
        _readBeginTimestampMicros = readBeginTimestampMicros;
    }

    public long getReadEndTimestampMicros() {
        return _readEndTimestampMicros;
    }

    public void setReadEndTimestampMicros(final long readEndTimestampMicros) {
        _readEndTimestampMicros = readEndTimestampMicros;
    }

    public String getWorkerName() {
        return _workerName;
    }

    public void setWorkerName(final String workerName) {
        _workerName = workerName;
    }
}
