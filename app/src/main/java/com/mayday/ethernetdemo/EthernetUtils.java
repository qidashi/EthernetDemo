package com.mayday.ethernetdemo;

import android.net.NetworkUtils;

import java.net.Inet4Address;
import java.util.regex.Pattern;

/**
 * Created by m5_pc on 2017/12/18.
 */

public class EthernetUtils {
    public static int maskStr2InetMask(String maskStr) {
        StringBuffer sb ;
        String str;
        int inetmask = 0;
        int count = 0;
        /*
         * check the subMask format
         */
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
        if (pattern.matcher(maskStr).matches() == false) {
            //Log.e("33333","subMask is error");
            return 0;
        }

        String[] ipSegment = maskStr.split("\\.");
        for(int n =0; n<ipSegment.length;n++) {
            sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
            str = sb.reverse().toString();
            count=0;
            for(int i=0; i<str.length();i++) {
                i=str.indexOf("1",i);
                if(i==-1)
                    break;
                count++;
            }
            inetmask+=count;
        }
        return inetmask;
    }


    public static Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (IllegalArgumentException | ClassCastException e) {
            return null;
        }

    }
}
