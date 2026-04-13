package com.helpdesk.listener;

import com.helpdesk.db.DBConnection;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DBConnection.ensureSchema();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.shutdown();
    }
}
