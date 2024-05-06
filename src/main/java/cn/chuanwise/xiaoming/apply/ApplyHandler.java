package cn.chuanwise.xiaoming.apply;

import cn.chuanwise.toolkit.verify.VerifyCodeHandler;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyHandler<T extends Plugin> extends VerifyCodeHandler implements PluginObject<T> {
    protected String[] permissions = new String[0];
    protected T plugin;
    protected XiaoMingBot xiaoMingBot;
    protected String message;
}
