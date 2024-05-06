package cn.chuanwise.xiaoming.group;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群管理器
 */
@Getter
@NoArgsConstructor
public class GroupInformationManagerImpl extends AbstractPreservable implements GroupInformationManager {
    Set<GroupInformation> groups = new CopyOnWriteArraySet<>();


    transient XiaoMingBot xiaoMingBot;

    @Override
    public synchronized GroupInformation addGroupInformation(long groupCode) {
        return getGroupInformation(groupCode)
                .orElseGet(() -> {
                    final String groupName = xiaoMingBot.getContactManager().getGroupContact(groupCode)
                            .map(GroupContact::getName)
                            .orElse(null);

                    final GroupInformation information = new GroupInformationImpl(groupCode, groupName);
                    information.setXiaoMingBot(xiaoMingBot);
                    if (addGroupInformation(information)) {
                        return information;
                    } else {
                        throw new IllegalStateException();
                    }
                });
    }

    @Override
    public boolean addGroupInformation(GroupInformation information) {
        information.flush();
        final boolean effected = !getGroupInformation(information.getCode()).isPresent();
        if (effected) {
            groups.add(information);
        }
        return effected;
    }

    public void setXiaoMingBot(XiaoMingBot xiaoMingBot) {
        this.xiaoMingBot = xiaoMingBot;
        groups.forEach(x -> x.setXiaoMingBot(xiaoMingBot));
    }
}
