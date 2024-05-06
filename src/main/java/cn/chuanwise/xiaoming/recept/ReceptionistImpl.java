package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.util.Maps;
import cn.chuanwise.util.ObjectUtil;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.user.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 小明接待员
 *
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl
        extends ModuleObjectImpl
        implements Receptionist {
    final long code;
    final Map<Long, GroupXiaoMingUser> groupXiaoMingUsers;
    final Map<Long, MemberXiaoMingUser> memberXiaoMingUsers;
    final Map<PropertyType, Object> properties;
    final Map<PropertyType, Object> conditionalVariables;
    /**
     * 私聊接待线程任务
     */
    @Setter
    volatile ReceptionTask<PrivateXiaoMingUser> privateTask;
    PrivateXiaoMingUser privateXiaoMingUser;

    public ReceptionistImpl(XiaoMingBot xiaoMingBot, long code) {
        super(xiaoMingBot);
        this.code = code;

        final Configuration configuration = xiaoMingBot.getConfiguration();
        this.groupXiaoMingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxGroupUserQuantityInReceptionist());
        this.memberXiaoMingUsers = new SizedResidentConcurrentHashMap<>(configuration.getMaxMemberUserQuantityInReceptionist());
        this.properties = new SizedResidentConcurrentHashMap<>(xiaoMingBot.getConfiguration().getMaxUserAttributeQuantity());
        this.conditionalVariables = new SizedResidentConcurrentHashMap<>(xiaoMingBot.getConfiguration().getMaxUserAttributeQuantity());
    }

    @Override
    public Map<PropertyType, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public <T> Container<T> removeProperty(PropertyType<T> type) {
        return Container.of((T) properties.remove(type));
    }

    @Override
    public <T> void setProperty(PropertyType<T> type, T value) {
        synchronized (properties) {
            synchronized (conditionalVariables) {
                properties.put(type, value);

                // 唤醒那些正在等待的线程
                final Object conditionalVariable = conditionalVariables.get(type);
                if (Objects.nonNull(conditionalVariable)) {
                    synchronized (conditionalVariable) {
                        conditionalVariable.notifyAll();
                    }
                    conditionalVariables.remove(type);
                }
            }
        }
    }

    @Override
    public <T> Container<T> waitProperty(PropertyType<T> type, long timeout) throws InterruptedException {
        final Object conditionalVariable = Maps.getOrPutSupply(conditionalVariables, type, Object::new);
        if (ObjectUtil.wait(conditionalVariable, timeout)) {
            return Container.of((T) properties.get(type));
        } else {
            return Container.empty();
        }
    }

    @Override
    public Optional<GroupXiaoMingUser> getGroupXiaoMingUser(long groupCode) {
        final Optional<GroupXiaoMingUser> optionalUser;
        try {
            optionalUser = Maps.get(groupXiaoMingUsers, groupCode).toOptional();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (optionalUser.isPresent()) {
            return optionalUser;
        }

        final Optional<MemberContact> optionalContact = getXiaoMingBot().getContactManager().getMemberContact(groupCode, code);
        if (!optionalContact.isPresent()) {
            return Optional.empty();
        }

        final GroupXiaoMingUser groupXiaoMingUser = new GroupXiaoMingUserImpl(optionalContact.get());
        groupXiaoMingUser.setReceptionist(this);
        return Optional.of(groupXiaoMingUser);
    }

    @Override
    public Optional<MemberXiaoMingUser> getMemberXiaoMingUser(long groupCode) {
        final Optional<MemberXiaoMingUser> optionalUser;
        try {
            optionalUser = Maps.get(memberXiaoMingUsers, groupCode).toOptional();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (optionalUser.isPresent()) {
            return optionalUser;
        }

        final Optional<MemberContact> optionalContact = getXiaoMingBot().getContactManager().getMemberContact(groupCode, code);
        if (!optionalContact.isPresent()) {
            return Optional.empty();
        }

        final MemberXiaoMingUser groupXiaoMingUser = new MemberXiaoMingUserImpl(optionalContact.get());
        groupXiaoMingUser.setReceptionist(this);
        return Optional.of(groupXiaoMingUser);
    }

    @Override
    public Optional<PrivateXiaoMingUser> getPrivateXiaoMingUser() {
        if (Objects.isNull(privateXiaoMingUser)) {
            final Optional<PrivateContact> optionalContact = xiaoMingBot.getContactManager().getPrivateContact(code);
            if (!optionalContact.isPresent()) {
                return Optional.empty();
            }

            privateXiaoMingUser = new PrivateXiaoMingUserImpl(optionalContact.get());
            privateXiaoMingUser.setReceptionist(this);
        }
        return Optional.of(privateXiaoMingUser);
    }

    @Override
    public Map<Long, GroupXiaoMingUser> getGroupXiaoMingUsers() {
        return Collections.unmodifiableMap(groupXiaoMingUsers);
    }

    @Override
    public Map<Long, MemberXiaoMingUser> getMemberXiaoMingUsers() {
        return Collections.unmodifiableMap(memberXiaoMingUsers);
    }
}
