package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class App {
    private static InteractiveShell interactiveShell;
    private App(){};
    private static final Logger logger = LogManager.getLogger("App");

    public static void main(String args[]) throws Exception {
        logger.info("Initializing Parking System");
        interactiveShell.loadInterface();
    }
}
