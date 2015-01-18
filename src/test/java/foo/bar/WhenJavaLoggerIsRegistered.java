package foo.bar;

import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WhenJavaLoggerIsRegistered {

    private static Ruby ruby;

    private static Logger rootLogger = Logger.getLogger("");

    @BeforeClass
    public static void setupJRuby() {
        ScriptingContainer scriptingContainer = new ScriptingContainer();
        ruby = scriptingContainer.getProvider().getRuntime();

        ruby.evalScriptlet("require 'semantic_logger'");
        SemanticLoggingJavaAppender.registerType(ruby);
    }

    @Before
    public void setup() throws Exception {
        ruby.evalScriptlet("SemanticLogger.add_appender SemanticLogger::JavaSemanticLoggerAppender.new(:info) {|log| log.message}");
    }

    @After
    public void cleanup() {
        ruby.evalScriptlet("SemanticLogger.appenders.each {|appender| SemanticLogger.remove_appender appender}");
        Stream.of(rootLogger.getHandlers()).filter(handler -> handler instanceof TestHandler).forEach(rootLogger::removeHandler);
    }

    @Test
    public void should_invoke_appender() throws Exception {
        // Given:
        TestHandler testHandler = new TestHandler();
        LogManager.getLogManager().getLogger("").addHandler(testHandler);
        // When:
        ruby.evalScriptlet("SemanticLogger['Hello'].info 'Hello World'");

        ruby.evalScriptlet("SemanticLogger.flush");

        // Then:
        assertThat(testHandler.getAllLogRecords(), hasSize(1));
        assertThat(testHandler.getAllLogRecords().get(0).getLevel(), is(Level.INFO));
        assertThat(testHandler.getAllLogRecords().get(0).getMessage(), is("Hello World"));
        assertThat(testHandler.getAllLogRecords().get(0).getLoggerName(), is("Hello"));
    }

}
