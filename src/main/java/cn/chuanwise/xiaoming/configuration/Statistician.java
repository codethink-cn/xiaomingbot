package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.object.XiaoMingObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public interface Statistician extends Preservable, XiaoMingObject {
    long getCallNumber();

    void increaseCallNumber();

    long getEffectiveCallNumber();

    void increaseEffectiveCallNumber();

    List<RunRecord> getRunRecords();

    long getBeginTime();

    default void onClose() {
        getRunRecords().add(new RunRecord(getBeginTime(), System.currentTimeMillis()));
        getXiaoMingBot().getFileSaver().saveOrFail(this);
    }

    default RunRecord getLastRecord() {
        final List<RunRecord> runRecords = getRunRecords();
        if (runRecords.isEmpty()) {
            return null;
        } else {
            return runRecords.get(runRecords.size() - 1);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class RunRecord {
        public long start;
        public long end;
    }
}
