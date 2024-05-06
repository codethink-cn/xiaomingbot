package cn.chuanwise.xiaoming.event;

public class SimpleXiaoMingCancellableEvent extends SimpleXiaoMingEvent implements XiaoMingCancellableEvent {
    protected volatile boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public synchronized void cancel() {
        cancelled = true;
    }
}
