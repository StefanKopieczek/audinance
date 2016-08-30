package com.stefankopieczek.audinance.player;

import javax.swing.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingPanel extends JTextArea
{
    public LoggingPanel(Logger logger)
    {
        super();
        setEditable(false);
        setLineWrap(true);
        logger.addHandler(new LogHandler());
    }

    private class LogHandler extends Handler
    {
        public LogHandler()
        {
            super();
        }

        @Override
        public void publish(LogRecord record)
        {
            LoggingPanel.this.append(record.getLevel() + " - ");

            String fullClass = record.getSourceClassName();
            String className = fullClass.substring(fullClass.lastIndexOf('.') + 1);
            LoggingPanel.this.append(className + ":");
            LoggingPanel.this.append(record.getSourceMethodName() + " - ");
            LoggingPanel.this.append(record.getMessage() + "\n\n");
        }

        @Override
        public void flush()
        {
            // No action needed to flush.
        }

        @Override
        public void close()
        {
            // No action needed to close.
        }
    }

}
