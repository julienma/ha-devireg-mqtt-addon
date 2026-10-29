package io.github.sonic_amiga.opensdg.java;

/**
 * Complete list of the Danfoss SecureDeviceGrid servers.
 *
 * opensdg_java 1.0.0 only knows 4 of them ({@link GridConnection#Danfoss}). Thermostats and the
 * official app use at least 8, all hardcoded (no DNS lookup); this list was captured from their
 * traffic on 2026-09-08. A client limited to the old list fails to reach thermostats registered
 * on the other servers ("Connection refused by grid: 1").
 *
 * This class lives in opensdg's package on purpose: {@link GridConnection.Endpoint}'s constructor
 * is package-private, while {@link GridConnection#connect(GridConnection.Endpoint[])} accepts any list.
 */
public final class DanfossGrid {
    public static final GridConnection.Endpoint[] SERVERS = {
            new GridConnection.Endpoint("77.66.11.90", 443),
            new GridConnection.Endpoint("77.66.11.92", 443),
            new GridConnection.Endpoint("77.66.7.41", 443),
            new GridConnection.Endpoint("77.66.7.42", 443),
            new GridConnection.Endpoint("5.179.92.171", 443),
            new GridConnection.Endpoint("5.179.92.172", 443),
            new GridConnection.Endpoint("5.179.92.180", 443),
            new GridConnection.Endpoint("5.179.92.182", 443) };

    private DanfossGrid() {
    }
}
