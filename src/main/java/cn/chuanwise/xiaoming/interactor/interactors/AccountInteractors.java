package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.List;

/**
 * 和用户账号相关的指令处理器
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public class AccountInteractors extends SimpleInteractors<Plugin> {
    AccountManager accountManager;

    @Override
    public void onRegister() {
        accountManager = getXiaoMingBot().getAccountManager();
    }

    @Filter(CommandWords.LET + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.LET + CommandWords.OPERATOR + " {qq}")
    @Filter(CommandWords.OPERATOR + " {qq}")
    @Required("core.account.user.administrator.grant")
    public void op(XiaoMingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else if (account.isAdministrator()) {
            user.sendMessage("该用户已经是管理员了");
        } else {
            account.setAdministrator(true);
            xiaoMingBot.getFileSaver().readyToSave(accountManager);

            user.sendMessage("成功授予" + account.getAliasAndCode() + "管理员权限");
        }
    }

    @Filter(CommandWords.REVOKE + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.REVOKE + CommandWords.OPERATOR + " {qq}")
    @Filter(CommandWords.CANCEL + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.CANCEL + CommandWords.OPERATOR + " {qq}")
    @Filter(CommandWords.DEOPERATOR + " {qq}")
    @Required("core.account.user.administrator.revoke")
    public void deop(XiaoMingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else if (account.isAdministrator()) {
            account.setAdministrator(false);
            xiaoMingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功收回授予" + account.getAliasAndCode() + "的管理员权限");
        } else {
            user.sendMessage("该用户并不是管理员");
        }
    }

    @Filter(CommandWords.BAN + " {qq}")
    @Required("core.account.user.ban")
    public void banUser(XiaoMingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else {
            account.setBanned(true);
            xiaoMingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功封禁" + account.getAliasAndCode());
        }
    }

    @Filter(CommandWords.UNBAN + " {qq}")
    @Required("core.account.user.unban")
    public void unbanUser(XiaoMingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            account.setBanned(false);
            xiaoMingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功解禁" + account.getAliasAndCode());
        } else {
            user.sendError("该用户并未被封禁");
        }
    }

    @Filter(CommandWords.ALIAS + " {qq} {r:备注}")
    @Filter(CommandWords.SET + CommandWords.ALIAS + " {qq} {r:备注}")
    @Required("core.account.user.alias.set")
    public void setUserAlias(XiaoMingUser user,
                             @FilterParameter("qq") long qq,
                             @FilterParameter("备注") String alias) {
        final Account account = accountManager.createAccount(qq);
        account.setAlias(alias);
        user.sendMessage("成功将该用户的备注设置为「" + alias + "」");
        getXiaoMingBot().getFileSaver().readyToSave(accountManager);
    }

    @Filter(CommandWords.ALIAS + " {qq}")
    @Required("core.account.user.alias.look")
    public void lookUserAlias(XiaoMingUser user,
                              @FilterParameter("qq") long qq) {
        user.sendMessage("该用户的备注是「" + xiaoMingBot.getAccountManager().getAliasOrCode(qq) + "」");
    }

    @Filter(CommandWords.TAG + " {qq} {标记}")
    @Required("core.account.user.tag.add")
    public void addUserTag(XiaoMingUser user,
                           @FilterParameter("qq") Account account,
                           @FilterParameter("标记") String accountTag) {
        if (account.hasTag(accountTag)) {
            user.sendError("该用户已经具备标记「" + accountTag + "」了");
        } else {
            account.addTag(accountTag);
            user.sendMessage("成功为用户添加了标记「" + accountTag + "」");
            getXiaoMingBot().getFileSaver().readyToSave(accountManager);
        }
    }

    @Filter(CommandWords.TAGGED + CommandWords.USER + " {标记}")
    @Filter(CommandWords.TAGGED + CommandWords.ACCOUNT + " {标记}")
    @Required("core.account.user.tag.search")
    public void searchAccountsByTag(XiaoMingUser user,
                                    @FilterParameter("标记") String accountTag) {
        final List<Account> accounts = xiaoMingBot.getAccountManager().searchAccountsByTag(accountTag);
        if (accounts.isEmpty()) {
            user.sendError("没有任何用户带有标记「" + accountTag + "」");
        } else {
            user.sendMessage("带有「」标记的用户有：\n" +
                    CollectionUtil.toIndexString(accounts, Account::getAliasAndCode));
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {标签}")
    @Required("core.account.user.tag.add")
    public void removeUserTag(XiaoMingUser user,
                              @FilterParameter("qq") Account account,
                              @FilterParameter("标签") String accountTag) {
        if (account.isOriginalTag(accountTag)) {
            user.sendError("「" + accountTag + "」是原生标记，不能删除");
            return;
        }
        if (account.hasTag(accountTag)) {
            account.removeTag(accountTag);
            user.sendMessage("成功删除了用户的标记「" + accountTag + "」");
            getXiaoMingBot().getFileSaver().readyToSave(accountManager);
        } else {
            user.sendError("该用户并没有「" + accountTag + "」这个标记哦");
        }
    }
}