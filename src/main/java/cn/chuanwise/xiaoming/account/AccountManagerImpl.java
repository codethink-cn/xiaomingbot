package cn.chuanwise.xiaoming.account;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.util.Maps;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaoMingContact;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AccountManagerImpl
        extends AbstractPreservable
        implements AccountManager {
    final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    @Setter
    transient XiaoMingBot xiaoMingBot;
    transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Map<Long, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    @Override
    public Account createAccount(long code) {
        return Maps.getOrPutSupply(accounts, code,
                () -> {
                    final AccountImpl account = new AccountImpl();
                    account.setCode(code);

                    final List<XiaoMingContact> contacts = xiaoMingBot.getContactManager().getPrivateContactPossibly(code);
                    if (!contacts.isEmpty()) {
                        account.setAlias(contacts.get(0).getName());
                    }

                    return account;
                });
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Transient
    @Override
    public XiaoMingBot getXiaoMingBot() {
        return xiaoMingBot;
    }
}
