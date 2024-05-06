package cn.chuanwise.xiaoming.log;

import cn.chuanwise.toolkit.console.color.ConsoleColor;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NocolorPatternLayout extends PatternLayout {
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected String color(String name) {
//        return "@|" + name;
        return "";
    }

    @Override
    public String format(LoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        // [2021-09-08 23:09:15] [main] 信息

        builder.append(color("gray")).append("[")
                .append(color("cyan")).append(DATE_FORMAT.format(event.getTimeStamp()))
                .append(color("gray")).append("] ")

                .append(color("gray")).append("[")
                .append(color("green")).append(event.getThreadName())
                .append(color("gray")).append("] ")

                .append(color("gray")).append("[")
                .append(level(event.getLevel()))
                .append(color("gray")).append("] ");

        // 获得缩减后的当前类名
        final String shortClassName = requireShortClassName(event.getLoggerName());
//        final int currentLength = builder.length() + shortClassName.length();
//        maxLogHeadLength = Math.max(maxLogHeadLength, currentLength);

//        builder.append(StringUtil.repeat(" ", maxLogHeadLength - currentLength))
//                .append(shortClassName)
//                .append(color("gray"))
//                .append(" : ")
//
//                .append(levelColor(event.getLevel()))
//                .append(event.getMessage());

        builder.append(" ")
                .append(shortClassName)
                .append(color("gray"))
                .append(" : ")

                .append(levelColor(event.getLevel()))
                .append(event.getMessage());


        // 结尾的换行
        builder.append("\r\n").append(ConsoleColor.WHITE);
        return builder.toString();
    }

    protected String level(Level level) {
        switch (level.toInt()) {
            case Level.INFO_INT:
                return color("cyan") + "信息";
            case Level.WARN_INT:
                return color("yellow") + "警告";
            case Level.ERROR_INT:
                return color("red") + "错误";
            case Level.FATAL_INT:
                return color("red") + "严重错误";
            case Level.TRACE_INT:
                return color("gray") + "轨迹";
            case Level.DEBUG_INT:
                return color("blue") + "调试";
            default:
                return color("gray") + level.toString();
        }
    }

    protected String levelColor(Level level) {
        switch (level.toInt()) {
            case Level.INFO_INT:
                return color("cyan");
            case Level.WARN_INT:
                return color("yellow");
            case Level.ERROR_INT:
                return color("red");
            case Level.FATAL_INT:
                return color("red");
            case Level.TRACE_INT:
                return color("gray");
            case Level.DEBUG_INT:
                return color("blue");
            default:
                return color("gray");
        }
    }

    private String requireShortClassName(String clazzName) {
        final int dotPos = clazzName.lastIndexOf('.');
        if (dotPos == -1) {
            return clazzName;
        } else {
            return clazzName.substring(dotPos + 1);
        }
    }
}