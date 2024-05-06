//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.chuanwise.util;

import cn.chuanwise.util.Arrays;
import cn.chuanwise.util.Collections;
import cn.chuanwise.util.Indexs;
import cn.chuanwise.util.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
@Data
@EqualsAndHashCode
public class Preconditions {
    public Preconditions() {
    }

    public static void nonNull(Object ref, String message) {
        if (ref == null) {
            throw new NullPointerException(message);
        }
    }

    public static void nonNull(Object ref) {
        if (ref == null) {
            throw new NullPointerException();
        }
    }

    public static void namedNonNull(Object ref, String objectName) {
        nonNull(ref, objectName + " is null");
    }

    public static void namedIsNull(Object ref, String objectName) {
        nonNull(ref, objectName + " isn't null");
    }

    public static void state(boolean legal, String message) {
        if (!legal) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean legal) {
        if (!legal) {
            throw new IllegalStateException();
        }
    }

    public static void stateNonNull(Object ref, String message) {
        state(ref != null, message);
    }

    public static void stateNonNull(Object ref) {
        state(ref != null);
    }

    public static void stateIsNull(Object ref, String message) {
        state(ref == null, message);
    }

    public static void stateIsNull(Object ref) {
        state(ref == null);
    }


    public static void argument(boolean legal, String message) {
        if (!legal) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void argument(boolean legal) {
        if (!legal) {
            throw new IllegalArgumentException();
        }
    }

    public static void argumentNonNull(Object ref, String message) {
        argument(ref != null, message);
    }

    public static void argumentNonNull(Object ref) {
        argument(ref != null);
    }


    public static void namedArgumentNonNull(Object ref, String objectName) {
        argument(ref != null, objectName + " is null");
    }

    public static void namedArgumentNonEmpty(Collection<?> collection, String objectName) {
        argument(Collections.nonEmpty(collection), objectName + " is empty");
    }

    public static void namedArgumentNonEmpty(String string, String objectName) {
        argument(Strings.nonEmpty(string), objectName + " is empty");
    }

    public static <T> void namedArgumentNonEmpty(T[] array, String objectName) {
        argument(Arrays.nonEmpty(array), objectName + " is empty");
    }

    public static void index(boolean legal) {
        if (!legal) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void index(boolean legal, String message) {
        if (!legal) {
            throw new IndexOutOfBoundsException(message);
        }
    }

    public static void indexNonNull(Object object) {
        index(Objects.nonNull(object));
    }

    public static void indexNonNull(Object object, String message) {
        index(Objects.nonNull(object), message);
    }

    public static void indexIsNull(Object object) {
        index(Objects.isNull(object));
    }

    public static void indexIsNull(Object object, String message) {
        index(Objects.isNull(object), message);
    }


    public static void index(int index, int bound, String message) {
        index(Indexs.isLegal(index, bound), message);
    }

    public static void operation(boolean legal) {
        if (!legal) {
            throw new UnsupportedOperationException();
        }
    }

    public static void operation(boolean legal, String message) {
        if (!legal) {
            throw new UnsupportedOperationException(message);
        }
    }

    public static void operationNonNull(Object object) {
        operation(Objects.nonNull(object));
    }

    public static void operationNonNull(Object object, String message) {
        operation(Objects.nonNull(object), message);
    }

    public static void operationIsNull(Object object) {
        operation(Objects.isNull(object));
    }

    public static void operationIsNull(Object object, String message) {
        operation(Objects.isNull(object), message);
    }

    public static void element(boolean legal) {
        if (!legal) {
            throw new NoSuchElementException();
        }
    }

    public static void element(boolean legal, String message) {
        if (!legal) {
            throw new NoSuchElementException(message);
        }
    }

    public static void elementNonNull(Object ref) {
        element(Objects.nonNull(ref));
    }

    public static void elementNonNull(Object ref, String message) {
        element(Objects.nonNull(ref), message);
    }

    public static void elementIsNull(Object ref) {
        element(Objects.isNull(ref));
    }

    public static void elementIsNull(Object ref, String message) {
        element(Objects.isNull(ref), message);
    }

    public static void memory(boolean legal) {
        if (!legal) {
            throw new OutOfMemoryError();
        }
    }

    public static void memory(boolean legal, String message) {
        if (!legal) {
            throw new OutOfMemoryError(message);
        }
    }
}
