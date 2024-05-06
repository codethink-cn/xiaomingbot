package cn.chuanwise.xiaoming.user;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaoMingContact;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.recept.ReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 小明的使用者对象
 *
 * @author Chuanwise
 */
public abstract class XiaoMingUserImpl<C extends XiaoMingContact<?>>
        extends ModuleObjectImpl implements XiaoMingUser<C> {
    @Getter
    @Setter
    Receptionist receptionist;

    @Getter
    @Setter
    ReceptionTask<XiaoMingUser<C>> receptionTask;

    @Getter
    InteractorContext interactorContext;

    public XiaoMingUserImpl(XiaoMingBot xiaoMingBot, long qq) {
        super(xiaoMingBot);
        this.receptionist = getXiaoMingBot().getReceptionistManager().getReceptionist(qq);

        setProperty(PropertyType.QQ, qq);
        setProperty(PropertyType.AT, new At(qq));
    }

    @Override
    public void setInteractorContext(InteractorContext interactorContext) {
        ConditionUtil.checkCallerSuperClass(Interactor.class);
        this.interactorContext = interactorContext;
    }

    @Override
    public boolean onNextMessage(MessageChain messages) {
        return onNextMessage(new MessageImpl(xiaoMingBot, messages));
    }

    @Override
    public Map<PropertyType, Object> getProperties() {
        return getReceptionist().getProperties();
    }

    @Override
    public <T> Container<T> getProperty(PropertyType<T> type) {
        return Optional.ofNullable(receptionist)
                .map(e -> e.getProperty(type))
                .orElseGet(Container::empty);
    }

    @Override
    public <T> Container<T> waitProperty(PropertyType<T> type, long timeout) throws InterruptedException {
        if (Objects.nonNull(receptionist)) {
            return receptionist.waitProperty(type, timeout);
        } else {
            return Container.empty();
        }
    }

    @Override
    public <T> Container<T> removeProperty(PropertyType<T> type) {
        return Optional.ofNullable(receptionist)
                .map(r -> r.removeProperty(type))
                .orElseGet(Container::empty);
    }

    @Override
    public <T> void setProperty(PropertyType<T> type, T value) {
        if (Objects.nonNull(receptionist)) {
            receptionist.setProperty(type, value);
        }
    }

    @Override
    public void flush() {
        getAccount().flush();
    }

    @Override
    public boolean addTag(String tag) {
        return getAccount().addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return getAccount().hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return getAccount().removeTag(tag);
    }
}