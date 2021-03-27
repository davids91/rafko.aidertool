package Models;

import Services.NetUtil;

public class Stats {
    public enum ContentDefinition{
        LAN_IP, WAN_IP, CONTENTS_SIZE;
        private static final ContentDefinition[] valuesArr = values();
        public static ContentDefinition get(int index){ return valuesArr[index];}
        public static String getName(int index){return get(index).name();}
    };
    private final String[] contents;

    public Stats() {
        contents = new String[ContentDefinition.CONTENTS_SIZE.ordinal()];
        contents[ContentDefinition.LAN_IP.ordinal()] = NetUtil.getLANIP();
        contents[ContentDefinition.WAN_IP.ordinal()] = NetUtil.getWANIP();
    }

    public String getContent(int index){
        return contents[index];
    }
}
