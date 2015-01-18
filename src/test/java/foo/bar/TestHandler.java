package foo.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TestHandler extends Handler {

    private List<LogRecord> allLogRecords = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        allLogRecords.add(record);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {
    }

    public List<LogRecord> getAllLogRecords() {
        return allLogRecords;
    }
}
