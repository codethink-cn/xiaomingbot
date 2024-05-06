package cn.chuanwise.xiaoming.language;

import cn.chuanwise.util.Arguments;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.language.convertor.Convertor;
import cn.chuanwise.xiaoming.language.convertor.ConvertorHandler;
import cn.chuanwise.xiaoming.language.environment.SimpleVariableOperator;
import cn.chuanwise.xiaoming.language.variable.VariableGetter;
import cn.chuanwise.xiaoming.language.variable.VariableHandler;
import cn.chuanwise.xiaoming.language.variable.VariableOperator;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.util.Registers;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Getter
public class LanguageManagerImpl extends ModuleObjectImpl implements LanguageManager {
    final Map<String, VariableHandler> globalVariables = new ConcurrentHashMap<>();
    final List<ConvertorHandler> convertors = new ArrayList<>();
    final List<VariableOperator<?>> operators = new ArrayList<>();

    public LanguageManagerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
    }

    @Override
    public List<VariableOperator<?>> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    @Override
    public Object calculate(Object object, String variable) {
        if (Objects.isNull(object)) {
            return null;
        }
        final List<String> paths = new ArrayList<>();
        boolean translated = false;
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < variable.length(); i++) {
            final char charAt = variable.charAt(i);
            if (translated) {
                stringBuilder.append(charAt);
                translated = false;
                continue;
            }
            if (charAt == '!') {
                translated = true;
            } else if (charAt == '.') {
                paths.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(charAt);
            }
        }
        if (stringBuilder.length() > 0) {
            paths.add(stringBuilder.toString());
        }

        // object 是当前值
        Object value = object;
        boolean operated = false;
        for (final String current : paths) {
            // 寻找一种类型的操作集合，进行所有操作
            operated = false;
            for (VariableOperator operator : forOperators(value.getClass())) {
                if (operator.apply(value, current)) {
                    final Object nextValue = operator.operate(value, current);
                    if (Objects.nonNull(nextValue)) {
                        value = nextValue;
                        operated = true;
                        break;
                    }
                }
            }

            // 如果找不到任何操作，就返回
            if (!operated) {
                break;
            }
        }

        if (operated) {
            return value;
        } else {
            return null;
        }
    }

    public <T> List<VariableOperator<T>> forOperators(Class<T> clazz) {
        final List<VariableOperator<T>> results = new ArrayList<>();
        for (VariableOperator<?> operator : operators) {
            if (operator.getClazz().isAssignableFrom(clazz)) {
                results.add((VariableOperator<T>) operator);
            }
        }
        return results;
    }

    @Override
    public <T> VariableOperator<T> registerOperators(Class<T> clazz, Plugin plugin) {
        final SimpleVariableOperator<T> operator = new SimpleVariableOperator<>(clazz);
        operators.add(operator);
        operator.setPlugin(plugin);
        return operator;
    }

    @Override
    public void unregisterOperators(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "operator");
        operators.removeIf(operator -> (operator.getPlugin() == plugin));
    }

    public Map<String, VariableHandler> getGlobalVariables() {
        return Collections.unmodifiableMap(globalVariables);
    }

    @Override
    public void registerVariable(String name, VariableGetter<?> getter, Plugin plugin) {
        globalVariables.put(name, new VariableHandler(name, getter, plugin));
    }

    @Override
    public void unregisterVariables(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "global variable");
        globalVariables.values().removeIf(handler -> (handler.getPlugin() == plugin));
    }

    @Override
    public void unregisterConvertors(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "convertor");
        convertors.removeIf(convertor -> (convertor.getPlugin() == plugin));
    }

    @Override
    public <T> void registerConvertor(Class<T> fromClass, Convertor<T> convertor, Plugin plugin) {
        Registers.checkRegister(getXiaoMingBot(), plugin, "convertor");
        convertors.add(new ConvertorHandler(fromClass, plugin, convertor));
    }

    @Override
    public String formatContext(String format, Function<String, ?> getter, LanguageRenderContext context) {
        return Arguments.format(format, getXiaoMingBot().getConfiguration().getMaxIterateTime(), variable -> {
            return calculate(baseVariable -> {
                if (Objects.equals(baseVariable, "context")) {
                    return context;
                }
                final Object throughGetter = getter.apply(baseVariable);
                if (Objects.nonNull(throughGetter)) {
                    return throughGetter;
                } else {
                    return getGlobalVariable(baseVariable);
                }
            }, variable);
        }, this::convert);
    }

    @Override
    public String formatAdditional(String format, Function<String, ?> getter, Object... contexts) {
        final String afterReplace = formatContext(format, getter, null);
        final List<String> parameterNames = Arguments.getContextVariableNames(afterReplace);
        return formatContext(afterReplace, getter, new LanguageRenderContext(parameterNames, contexts));
    }
}
