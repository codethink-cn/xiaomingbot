package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileInteractorCustomizer
        extends SimplePreservable<Plugin>
        implements InteractorCustomizer {

    Map<String, InteractorInfo> interactorInfo = new HashMap<>();

    transient boolean changed = false;

    @Transient
    public boolean isChanged() {
        return changed;
    }

    @Override
    public Optional<InteractorInfo> getInteractorInfo(String name) {
        return Optional.ofNullable(interactorInfo.get(name));
    }

    @Override
    public void registerInteractorInfo(String name, InteractorInfo interactorInfo) {
        changed = true;
        this.interactorInfo.put(name, interactorInfo);
    }

    public void saveIfChanged() {
        if (changed) {
            readyToSave();
        }
    }
}
