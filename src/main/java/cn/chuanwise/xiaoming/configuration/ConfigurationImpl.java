package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ConfigurationImpl extends AbstractPreservable implements Configuration {
    transient XiaoMingBot xiaoMingBot;

    boolean debug = false;

    int maxIterateTime = 20;

    /**
     * 自动切割消息
     */
    boolean autoSplitMessage = true;
    int textPerPage = 3000;
    int miraiCodePerPage = 10;

    /**
     * 存储数据所用编码类型
     */
    String storageEncoding = "UTF-8";
    String storageDecoding = "UTF-8";

    /**
     * 自动同意添加好友、加群申请
     */
    boolean autoAcceptFriendAddRequest = true;
    boolean autoAcceptGroupInvite = false;

    boolean trimMessage = true;

    /**
     * 主线程池最大容量
     */
    int maxMainThreadPoolSize = 20;

    /**
     * 启动时在日志群发消息
     */
    boolean enableStartLog = false;
    boolean saveFileDirectly = true;

    /**
     * 和用户
     */
    long maxUserInputTimeout = TimeUnit.MINUTES.toMillis(10);
    long maxUserPrivateInputTimeout = TimeUnit.MINUTES.toMillis(10);
    long maxUserGroupInputTimeout = TimeUnit.MINUTES.toMillis(10);

    long optimizePeriod = TimeUnit.MINUTES.toMillis(10);
    long savePeriod = TimeUnit.MINUTES.toMillis(30);

    int maxMemberUserQuantityInReceptionist = 3;
    int maxGroupUserQuantityInReceptionist = 10;
    int maxRecentMessageBufferSize = 10;
    int maxUserAttributeQuantity = 20;
    int maxReceptionistQuantity = 50;

    long sendMessagePeriod = TimeUnit.SECONDS.toMillis(3);

    String forwardGroupTag = Tags.ALL;
    long sendMessageTimeout = TimeUnit.MINUTES.toMillis(2);

    boolean delayWrite = false;
    boolean enablePrivateInteractors = true;
    boolean enablePrivateSend = true;
    boolean enableGroupInteractors = true;
    boolean enableGroupSend = true;
    boolean enableMemberInteractors = true;
    boolean enableMemberSend = true;
}
