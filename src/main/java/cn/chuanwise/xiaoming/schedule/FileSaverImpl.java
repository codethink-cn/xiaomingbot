package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
public class FileSaverImpl extends ModuleObjectImpl implements FileSaver {
    final Map<File, Preservable> preservables = new ConcurrentHashMap<>();
    final AtomicLong lastSaveTime = new AtomicLong(System.currentTimeMillis());
    final AtomicLong lastValidSaveTime = new AtomicLong(System.currentTimeMillis());
    Charset encodeCharset = Charset.defaultCharset();

    public FileSaverImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
    }

    @Override
    public void save() {
        final long timeMillis = System.currentTimeMillis();

        lastSaveTime.set(timeMillis);
        if (getPreservables().isEmpty()) {
            return;
        }

        lastValidSaveTime.set(timeMillis);

        for (Map.Entry<File, Preservable> entry : getPreservables().entrySet()) {
            final File file = entry.getKey();
            final Preservable preservable = entry.getValue();

            if (Objects.isNull(preservable.getFile())) {
                preservable.setFile(file);
            }

            if (saveOrFail(preservable)) {
                getLogger().info("成功保存文件：" + file.getAbsolutePath());
                preservables.remove(file);
            } else {
                getLogger().error("保存文件失败：" + file.getAbsolutePath());
            }
        }
    }

    @Override
    public long getLastSaveTime() {
        return lastSaveTime.get();
    }

    @Override
    public long getLastValidSaveTime() {
        return lastValidSaveTime.get();
    }
}
