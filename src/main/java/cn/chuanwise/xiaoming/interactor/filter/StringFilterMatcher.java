package cn.chuanwise.xiaoming.interactor.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public abstract class StringFilterMatcher extends FilterMatcher {
    String string;

    @Override
    public String toUsage() {
        return string;
    }
}
