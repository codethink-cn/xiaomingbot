package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.Preconditions;
import cn.chuanwise.util.StaticUtil;

import java.util.Objects;

public class Permissions extends StaticUtil {
    public static boolean isAccessible(String owned, String required) {
        Preconditions.nonNull(owned, "owned permission");
        Preconditions.nonNull(required, "required permission");

        final int spiltter = owned.lastIndexOf("*");
        if (spiltter == -1) {
            return Objects.equals(owned, required);
        } else {
            return required.startsWith(owned.substring(0, spiltter));
        }
    }
}
