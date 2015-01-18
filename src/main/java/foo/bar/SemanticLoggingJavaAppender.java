package foo.bar;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyProc;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.Block;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SemanticLoggingJavaAppender extends RubyObject {

    public SemanticLoggingJavaAppender(Ruby ruby, RubyClass rubyClass) {
        super(ruby, rubyClass);
    }

    @JRubyMethod(name = "log", required = 1)
    public void handleLogEvent(ThreadContext context, IRubyObject log) {

        LogEvent logEvent = (LogEvent) JavaEmbedUtils.rubyToJava(context.getRuntime(), log, LogEvent.class);

        LogRecord logRecord = createJULLogRecordFromLogEvent(logEvent, context);

        Logger.getLogger(logEvent.getName()).log(logRecord);
    }

    private LogRecord createJULLogRecordFromLogEvent(LogEvent logEvent, ThreadContext context) {
        Level level;
        switch (logEvent.getLevel()) {
            case "trace":
                level = Level.FINEST;
                break;
            case "debug":
                level = Level.FINE;
                break;
            case "info":
                level = Level.INFO;
                break;
            case "warn":
            case "error":
                level = Level.WARNING;
                break;
            case "fatal":
                level = Level.SEVERE;
                break;
            default:
                throw new IllegalArgumentException("Level: " + logEvent.getLevel());
        }
        LogRecord logRecord = new LogRecord(level, logEvent.getMessage());
        logRecord.setLoggerName(logEvent.getName());
        logRecord.setSourceClassName(logEvent.getName());
        return logRecord;
    }

    @JRubyMethod(name = "initialize", required = 0, optional = 2)
    public void initObject(ThreadContext context, IRubyObject[] args, Block block) {

        Helpers.invokeSuper(context, this, getMetaClass(), "initialize", args, block);

    }

    public static void registerType(Ruby ruby) {

        RubyModule semanticLoggerModule = ruby.getModule("SemanticLogger");

        RubyModule appenderModule = semanticLoggerModule.defineOrGetModuleUnder("Appender");

        RubyClass baseClass = appenderModule.getClass("Base");

        RubyClass extendedClass = semanticLoggerModule.defineClassUnder(
                "JavaSemanticLoggerAppender",
                baseClass,
                new ObjectAllocator() {
                    @Override
                    public IRubyObject allocate(Ruby ruby, RubyClass rubyClass) {
                        return new SemanticLoggingJavaAppender(ruby, rubyClass);
                    }
                });

        extendedClass.defineAnnotatedMethods(SemanticLoggingJavaAppender.class);

    }
}


