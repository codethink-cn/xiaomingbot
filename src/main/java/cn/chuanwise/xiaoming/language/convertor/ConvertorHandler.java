package cn.chuanwise.xiaoming.language.convertor;

import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

@Data
public class ConvertorHandler<T> {
    final Class<T> fromClass;
    final Plugin plugin;
    final Convertor<T> convertor;
}
