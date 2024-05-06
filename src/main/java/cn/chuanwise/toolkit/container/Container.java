
package cn.chuanwise.toolkit.container;

import cn.chuanwise.toolkit.box.Box;
import cn.chuanwise.util.ObjectUtil;
import cn.chuanwise.util.Preconditions;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
@Data
@EqualsAndHashCode
public final class Container<T> {
    private static final Container<?> EMPTY = new Container();
    private static final Container<?> NULL = new Container();
    private final T value;

    private Container(T value) {
        Preconditions.namedArgumentNonNull(value, "若要构造装有 null 值的容器，请使用 Container.ofNull()");
        this.value = value;
    }

    private Container() {
        this.value = null;
    }

    public static <U> Container<U> ofOptional(Optional<U> optional) {
        Preconditions.namedArgumentNonNull(optional, "optional");
        return (Container) optional.map(Container::of).orElse(empty());
    }

    public static <U> Container<U> ofBox(Box<U> box) {
        Preconditions.namedArgumentNonNull(box, "box");
        return (Container) box.map(Container::of).orElse(empty());
    }

    public static <U> Container<U> of(U value) {
        return Objects.isNull(value) ? (Container<U>) NULL : new Container(value);
    }

    public static <U> Container<U> ofNull() {
        return (Container<U>) NULL;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static <U> Container<U> ofNotNull(U value) {
        return Objects.isNull(value) ? empty() : of(value);
    }

    public static <U> Container<U> ofNonNull(U value) {
        return ofNotNull(value);
    }

    public static <U> Container<U> empty() {
        return (Container<U>) EMPTY;
    }

    public T get() throws Throwable {
        return this.orElseThrow();
    }

    public boolean isSet() {
        return this != EMPTY;
    }

    public boolean isPresent() {
        return this.isSet() && this.value != null;
    }

    public boolean isEmpty() {
        return !this.isSet();
    }

    public boolean isNull() {
        return this == NULL;
    }

    public Container<T> ifPresent(Consumer<T> action) throws Throwable {
        Preconditions.namedArgumentNonNull(action, "action");
        if (this.isPresent()) {
            action.accept(this.get());
        }

        return this;
    }

    public Container<T> ifPresent(Runnable action) {
        Preconditions.namedArgumentNonNull(action, "action");
        if (this.isPresent()) {
            action.run();
        }

        return this;
    }

    public Container<T> ifSet(Consumer<T> action) {
        Preconditions.namedArgumentNonNull(action, "action");
        if (this.isSet()) {
            action.accept(this.value);
        }

        return this;
    }

    public Container<T> ifSet(Runnable action) {
        Preconditions.namedArgumentNonNull(action, "action");
        if (this.isSet()) {
            action.run();
        }

        return this;
    }

    public Container<T> ifEmpty(Runnable action) {
        Preconditions.namedArgumentNonNull(action, "action");
        if (this.isEmpty()) {
            action.run();
        }

        return this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Container<T> ifPresentOrEmpty(Consumer<T> action1, Runnable action2) throws Throwable {
        Preconditions.namedArgumentNonNull(action1, "action when the container is present");
        Preconditions.namedArgumentNonNull(action2, "action when the container isn't present");
        if (this.isPresent()) {
            action1.accept(this.get());
        } else {
            action2.run();
        }

        return this;
    }

    public <U> Container<U> map(Function<T, U> mapper) throws Throwable {
        Preconditions.namedArgumentNonNull(mapper, "mapper");
        return this.isSet() ? of(mapper.apply(this.get())) : empty();
    }

    public Container<T> filter(Predicate<T> filter) {
        Preconditions.namedArgumentNonNull(filter, "filter");
        return this.isSet() && filter.test(this.value) ? this : empty();
    }

    public <U> Container<U> flatMap(Function<T, Container<U>> mapper) {
        return this.isSet() ? (Container) mapper.apply(this.value) : empty();
    }

    public Container<T> or(Container<T> container) {
        Preconditions.namedArgumentNonNull(container, "container");
        return this.isEmpty() ? container : this;
    }

    public Optional<T> toOptional() throws Throwable {
        return (Optional) this.map(Optional::ofNullable).orElseGet(Optional::empty);
    }

    public Box<T> toBox() throws Throwable {
        return (Box) this.map(Box::of).orElseGet(Box::empty);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <U> Container<U> to(Class<U> clazz) throws Throwable {
        Preconditions.namedArgumentNonNull(clazz, "class");
        return !this.isPresent() ? empty() : ObjectUtil.cast(this.get(), clazz);
    }

    public T orElse(T defaultValue) throws Throwable {
        return this.orElseGet(() -> {
            return defaultValue;
        });
    }

    public T orElseGet(Supplier<T> supplier) throws Throwable {
        Preconditions.namedArgumentNonNull(supplier, "supplier");
        return this.isSet() ? this.get() : supplier.get();
    }

    public T orElseThrow() throws Throwable {
        return this.orElseThrow(NoSuchElementException::new);
    }

    public T orElseThrow(String message) throws Throwable {
        Preconditions.namedArgumentNonNull(message, "message");
        return this.orElseThrow(() -> {
            return new NoSuchElementException(message);
        });
    }

    public <E extends Throwable> T orElseThrow(Supplier<E> supplier) throws Throwable {
        Preconditions.namedArgumentNonNull(supplier, "supplier");
        if (this.isSet()) {
            return this.value;
        } else {
            throw (Throwable) supplier.get();
        }
    }
}
