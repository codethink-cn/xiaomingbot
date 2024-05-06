package cn.chuanwise.xiaoming.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class XiaoMingInitializeException extends XiaoMingRuntimeException {
    public XiaoMingInitializeException(String message) {
        super(message);
    }
}
