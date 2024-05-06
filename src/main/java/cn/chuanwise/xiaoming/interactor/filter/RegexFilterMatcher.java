package cn.chuanwise.xiaoming.interactor.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public abstract class RegexFilterMatcher extends FilterMatcher {
    public static final String NORMAL_VARIABLE_REGEX = "\\S+?";
    public static final String REMAIN_VARIABLE_REGEX = "[\\s\\S]+";
    /**
     * 提取指令参数时的正则表达式
     */
    public static final Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");
    Pattern pattern;

    @Override
    public String toUsage() {
        final StringBuilder builder = new StringBuilder(pattern.pattern()
                .replaceAll(Pattern.quote("(?"), "")
                .replaceAll(Pattern.quote(NORMAL_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote(REMAIN_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote("\\s+"), " ")
                .replaceAll(Pattern.quote("\\s*"), " ")
                .replaceAll(Pattern.quote("\\s"), " ")
                .replaceAll("\\s+", "  ")
                .replaceAll(Pattern.quote("\\[mirai:at:"), "@")
                .replaceAll(Pattern.quote("\\]"), ""));

        Matcher matcher = PARAMETER_REGEX.matcher(builder);
        while (matcher.find()) {
            builder.replace(matcher.start(), matcher.end(), matcher.group("fst"));
            matcher = PARAMETER_REGEX.matcher(builder);
        }
        return builder.toString();
    }
}
