package com.helpdesk.listener;

import com.helpdesk.db.DBConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(ApplicationLifecycleListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DBConnection.ensureSchema();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Database is not reachable during startup. The web app will still start, but DB-backed pages may fail until the connection is fixed.", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.shutdown();
    }
}
