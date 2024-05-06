package cn.chuanwise.xiaoming.object;

public interface FormatableObject extends XiaoMingObject {
    String format(String format, Object... contexts);
}
