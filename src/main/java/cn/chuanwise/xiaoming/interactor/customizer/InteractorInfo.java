package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.permission.Permission;
import lombok.Data;

@Data
public class InteractorInfo {

    protected String name;

    protected String[] formats = new String[0];
    protected Permission[] permissions = new Permission[0];
    protected String usage = null;
    protected String[] requireGroupTags = new String[0];
    protected String[] requireAccountTags = new String[0];
}
