package cn.chuanwise.xiaoming.account;

import cn.chuanwise.api.AbstractOriginalTagMarkable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AccountImpl
        extends AbstractOriginalTagMarkable
        implements Account {
    long code;
    String alias;
    boolean administrator;
    boolean banned;
}
