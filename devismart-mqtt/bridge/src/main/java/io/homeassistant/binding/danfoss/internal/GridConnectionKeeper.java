package io.homeassistant.binding.danfoss.internal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.sonic_amiga.opensdg.java.Connection;
import io.github.sonic_amiga.opensdg.java.DanfossGrid;
import io.github.sonic_amiga.opensdg.java.GridConnection;

public class GridConnectionKeeper {
    private static final Logger logger = LoggerFactory.getLogger(GridConnectionKeeper.class);
    private static GridConnection g_Conn;
    private static int numUsers = 0;
    private static String privateKey = null;

    public synchronized static GridConnection getConnection()
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if (privateKey == null) {
            privateKey = DanfossBindingConfig.get().privateKey;
        }
        if (g_Conn == null) {
            // Let opensdg use its own dedicated "SDG ping thread". The grid drops clients that
            // stop pinging for ~90 s; sharing the 5-thread "thingHandler" pool with the thing
            // handlers (whose device pings block for up to 10 s each) could starve the keepalive.
            g_Conn = new GridConnection(SDGUtils.ParseKey(privateKey));
        }

        if (g_Conn.getState() != Connection.State.CONNECTED) {
            try {
                g_Conn.connect(DanfossGrid.SERVERS);
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException
                    | RuntimeException e) {
                // Never reuse a GridConnection whose connect() failed: opensdg_java 1.0.0 can
                // leave it outside the CLOSED state (e.g. NPE in closeOnlySocket()), after which
                // every connect() throws and the bridge stays offline until restarted.
                discardConnection();
                throw e;
            }
            logger.info("Successfully connected to Danfoss grid");
        }

        return g_Conn;
    }

    private static void discardConnection() {
        try {
            g_Conn.close();
        } catch (RuntimeException e) {
            logger.debug("Ignoring error while discarding grid connection: {}", e.toString());
        }
        g_Conn = null;
    }

    public static synchronized void UpdatePrivateKey(String newKey) {
        if (g_Conn != null && !newKey.equals(privateKey)) {
            // Will reconnect on demand
            closeConnection();
        }
        privateKey = newKey;
    }

    public static synchronized void AddUser() {
        numUsers++;
    }

    public static synchronized void RemoveUser() {
        if (--numUsers > 0) {
            return;
        }

        if (g_Conn == null) {
            return;
        }

        logger.info("Last user is gone, disconnecting from Danfoss grid");
        closeConnection();
    }

    private static void closeConnection() {
        g_Conn.close();
        g_Conn = null;
        logger.info("Grid connection closed");
    }
}
