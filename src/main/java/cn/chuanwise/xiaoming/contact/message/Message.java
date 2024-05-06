package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.object.XiaoMingObject;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;

import java.io.IOException;
import java.util.concurrent.Future;

public interface Message extends XiaoMingObject {
    /**
     * 获取消息概要（会压缩信息显示。所有的图片变为 [图片] 等）
     *
     * @return 消息概要字符串
     */
    default String summary() {
        return getMessageChain().contentToString();
    }

    String serialize();

    String serializeOriginalMessage();

    MessageChain getMessageChain();

    void setMessageChain(MessageChain messageChain);

    MessageChain getOriginalMessageChain();

    void setOriginalMessageChain(MessageChain messageChain);

    long getTime();

    default void saveResources() throws IOException {
        getXiaoMingBot().getResourceManager().saveResources(this);
    }

    default Future<Boolean> asyncSaveResources() {
        return getXiaoMingBot().getScheduler().run(() -> {
            saveResources();
            return true;
        });
    }

    int[] getInternalMessageCode();

    int[] getMessageCode();

    /**
     * 撤回消息
     */
    default void recall() {
        MessageSource.recall(getOriginalMessageChain());
    }
}