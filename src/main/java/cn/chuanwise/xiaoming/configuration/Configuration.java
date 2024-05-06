package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.object.XiaoMingObject;

/**
 * 小明配置文件数据
 */
public interface Configuration extends Preservable, XiaoMingObject {
    String getStorageEncoding();

    void setStorageEncoding(String storageEncoding);

    String getStorageDecoding();

    void setStorageDecoding(String storageEncoding);

    boolean isDebug();

    void setDebug(boolean debug);

    boolean isEnableStartLog();

    void setEnableStartLog(boolean enableStartLog);

    int getMaxIterateTime();

    void setMaxIterateTime(int maxIterateTime);

    boolean isAutoSplitMessage();

    void setAutoSplitMessage(boolean autoSplitMessage);

    int getTextPerPage();

    void setTextPerPage(int textPerPage);

    int getMiraiCodePerPage();

    void setMiraiCodePerPage(int miraiCodePerPage);

    int getMaxRecentMessageBufferSize();

    void setMaxRecentMessageBufferSize(int maxRecentMessageBufferSize);

    int getMaxUserAttributeQuantity();

    void setMaxUserAttributeQuantity(int maxUserAttributeQuantity);

    int getMaxReceptionistQuantity();

    void setMaxReceptionistQuantity(int MaxReceptionistQuantity);

    int getMaxGroupUserQuantityInReceptionist();

    void setMaxGroupUserQuantityInReceptionist(int maxGroupUserQuantityInReceptionist);

    int getMaxMemberUserQuantityInReceptionist();

    void setMaxMemberUserQuantityInReceptionist(int maxMemberUserQuantityInReceptionist);

    long getMaxUserInputTimeout();

    void setMaxUserInputTimeout(long time);

    long getMaxUserPrivateInputTimeout();

    void setMaxUserPrivateInputTimeout(long time);

    long getMaxUserGroupInputTimeout();

    void setMaxUserGroupInputTimeout(long time);

    long getSavePeriod();

    void setSavePeriod(long savePeriod);

    long getOptimizePeriod();

    void setOptimizePeriod(long optimizePeriod);

    boolean isSaveFileDirectly();

    void setSaveFileDirectly(boolean saveFileDirectly);

    boolean isAutoAcceptFriendAddRequest();

    void setAutoAcceptFriendAddRequest(boolean autoAcceptFriendAddRequest);

    boolean isAutoAcceptGroupInvite();

    void setAutoAcceptGroupInvite(boolean autoAcceptGroupInvite);

    boolean isTrimMessage();

    void setTrimMessage(boolean trimMessage);

    int getMaxMainThreadPoolSize();

    long getSendMessagePeriod();

    void setSendMessagePeriod(long sendMessagePeriod);

    boolean isDelayWrite();

    void setDelayWrite(boolean delayWrite);

    boolean isEnablePrivateInteractors();

    void setEnablePrivateInteractors(boolean enablePrivateInteractors);

    boolean isEnableGroupInteractors();

    void setEnableGroupInteractors(boolean enableGroupInteractors);

    boolean isEnableMemberInteractors();

    void setEnableMemberInteractors(boolean enableMemberInteractors);

    boolean isEnablePrivateSend();

    void setEnablePrivateSend(boolean enablePrivateSend);

    boolean isEnableGroupSend();

    void setEnableGroupSend(boolean enableGroupSend);

    boolean isEnableMemberSend();

    void setEnableMemberSend(boolean enableMemberSend);

    long getSendMessageTimeout();

    void setSendMessageTimeout(long sendMessageTimeout);

    String getForwardGroupTag();

    void setForwardGroupTag(String forwardGroupTag);
}
