package org.nomanscode.visualstreamer.common;

import org.nomanscode.visualstreamer.common.*;

import java.util.Date;
import java.util.UUID;

public class LogEnvelope {
    public UUID id = UUID.randomUUID();
    public String title;
    public ErrorLevel level;
    public String cause;
    public String nodeName;
    public Date date = new Date();

    public LogEnvelope(final String nodeName, final String title, final ErrorLevel level) {
        this.nodeName = nodeName;
        this.title = title;
        this.level = level;
    }

    public LogEnvelope(final String nodeName, final String title, final String cause, final ErrorLevel level) {
        this.nodeName = nodeName;
        this.title = title;
        this.cause = cause;
        this.level = level;
    }

    public LogEnvelope(final UUID id, final String nodeName, final String title, final String cause, final ErrorLevel level, final Date date) {
        this.id = id;
        this.nodeName = nodeName;
        this.title = title;
        this.cause = cause;
        this.level = level;
        this.date = date;
    }

    public static LogEnvelope create(final UUID id, final String nodeName, final String title, final String cause, final ErrorLevel level, final Date date){
        try {
            return new LogEnvelope(id, nodeName, title, cause, level, date);
        }
        catch(Exception e) {
            return null;
        }
    }

}
