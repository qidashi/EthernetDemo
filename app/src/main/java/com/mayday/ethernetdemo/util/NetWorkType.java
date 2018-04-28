package com.mayday.ethernetdemo.util;

/**
 * Created by chenfujin on 17-11-10.
 */

public class NetWorkType {
    private String ip ;
    private EnumNetWorkType type ;

    public NetWorkType(String ip, EnumNetWorkType type) {
        this.ip = ip;
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public EnumNetWorkType getType() {
        return type;
    }

    public void setType(EnumNetWorkType type) {
        this.type = type;
    }
}
