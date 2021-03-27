package Models;

import Services.NetUtil;

public class Stats {
    private final String LANIP;
    private final String WANIP;

    public Stats() {
        LANIP = NetUtil.getLANIP();
        WANIP = NetUtil.getWANIP();
    }

    public String getLANIP() {
        return LANIP;
    }

    public String getWANIP() {
        return WANIP;
    }
}
