package cn.chuanwise.xiaoming.interactor.customizer;

import java.util.Optional;

/**
 * 交互器自定义器
 */
public interface InteractorCustomizer {
    Optional<InteractorInfo> getInteractorInfo(String name);

    void registerInteractorInfo(String name, InteractorInfo interactorInfo);
}