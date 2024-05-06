package cn.chuanwise.xiaoming.language.variable;

@FunctionalInterface
public interface VariableRequester<T> {
    Object request(T value, String identifier);

    default boolean apply(T value, String identifier) {
        return true;
    }
}
