package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class XiaoMingObjectImpl implements XiaoMingObject {
    protected transient XiaoMingBot xiaoMingBot;
}