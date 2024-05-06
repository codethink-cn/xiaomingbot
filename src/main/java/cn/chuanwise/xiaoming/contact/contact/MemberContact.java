package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.user.MemberXiaoMingUser;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public interface MemberContact extends XiaoMingContact<NormalMember> {
    @Override
    default Optional<Message> nextMessage(long timeout, Predicate<Message> filter) throws InterruptedException {
        return getXiaoMingBot()
                .getContactManager()
                .nextMessageEvent(timeout,
                        x -> x.getUser() instanceof MemberXiaoMingUser
                                && (((MemberXiaoMingUser) x.getUser()).getGroupCode() == getGroupCode()
                                && x.getUser().getCode() == getCode()
                                && filter.test(x.getMessage())))
                .map(MessageEvent::getMessage);
    }

    default Account getAccount() {
        return getXiaoMingBot().getAccountManager().createAccount(getCode());
    }

    @Override
    default String getName() {
        return this.getMiraiContact().getNick();
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAliasAndCode() {
        return "「" + getGroupContact().getAliasAndCode() + "」" + getName() + "（" + getCodeString() + "）";
    }

    @Override
    default String getAlias() {
        final Account account = getAccount();
        return Objects.nonNull(account) && Objects.nonNull(account.getAlias()) ? account.getAlias() : getName();
    }

    default String getNick() {
        return this.getMiraiContact().getNick();
    }

    default String getNameCard() {
        return this.getMiraiContact().getNameCard();
    }

    default void setNameCard(String nameCard) {
        this.getMiraiContact().setNameCard(nameCard);
    }

    default MemberPermission getPermission() {
        return this.getMiraiContact().getPermission();
    }

    default GroupInformation getGroupInformation() {
        return getGroupContact().getGroupInformation();
    }

    default void mute(long timeMillis) {
        getMiraiContact().mute(((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
    }

    default void unmute() {
        this.getMiraiContact().unmute();
    }

    default void nudge() {
        this.getMiraiContact().nudge();
    }

    GroupContact getGroupContact();

    default long getGroupCode() {
        return getGroupContact().getCode();
    }

    default String getGroupCodeString() {
        return getGroupContact().getCodeString();
    }

    default String getSpecialTitle() {
        return this.getMiraiContact().getSpecialTitle();
    }

    default void setSpecialTitle(String specialTitle) {
        this.getMiraiContact().setSpecialTitle(specialTitle);
    }

    default long getJoinTime() {
        return this.getMiraiContact().getJoinTimestamp();
    }

    default long getLastSpeakTime() {
        return this.getMiraiContact().getLastSpeakTimestamp();
    }

    default long getRemainMuteTime() {
        return this.getMiraiContact().getMuteTimeRemaining();
    }

    default boolean isMuted() {
        return this.getMiraiContact().isMuted();
    }

    default void kick(String reason) {
        this.getMiraiContact().kick(reason);
    }

    default UserProfile getUserProfile() {
        return this.getMiraiContact().queryProfile();
    }

    default MemberXiaoMingUser getUser() {
        return getXiaoMingBot().getReceptionistManager().getReceptionist(getCode()).getMemberXiaoMingUser(getGroupCode()).orElseThrow(NoSuchElementException::new);
    }

    @Override
    default Set<String> getTags() {
        return getXiaoMingBot().getAccountManager().getTags(getCode());
    }
}
