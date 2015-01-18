package foo.bar;

import java.util.Date;

public interface LogEvent {

    String getLevel();

    String getThreadName();

    String getName();

    String getMessage();

    Object getPayload();

    Date getTime();

    float getDuration();

    Object getException();

}
