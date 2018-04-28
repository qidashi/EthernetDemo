package com.mayday.ethernetdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.mayday.ethernetdemo.JzxApplication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function:
 * Created by TianMing.Xiong on 2017/9/23.
 */

public class IpUtil {
    private static Context context;
    /**
     * 将得到的int类型的IP转换为String类型
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 得到有线网关的IP地址
     * @return
     */
    public static NetWorkType getNetWorkType() {
        context = JzxApplication.getAppContext();
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            //手机网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return new NetWorkType(inetAddress.getHostAddress(),EnumNetWorkType.CONN_3G);
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }else if(info.getType() == ConnectivityManager.TYPE_WIFI) {
                //wifi网络
                try {
                    // 获取本地设备的所有网络接口
                    Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                            .getNetworkInterfaces();
                    while (enumerationNi.hasMoreElements()) {
                        NetworkInterface networkInterface = enumerationNi.nextElement();
                        String interfaceName = networkInterface.getDisplayName();
                        Log.i("tag", "网络名字" + interfaceName);
                        // 如果是无线网卡
                        if (interfaceName.equals("wlan0")) {
                            Enumeration<InetAddress> enumIpAddr = networkInterface
                                    .getInetAddresses();

                            while (enumIpAddr.hasMoreElements()) {
                                // 返回枚举集合中的下一个IP地址信息
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                // 不是回环地址，并且是ipv4的地址
                                if (!inetAddress.isLoopbackAddress()
                                        && inetAddress instanceof Inet4Address) {
                                    Log.i("tag", inetAddress.getHostAddress() + "   ");
                                    return new NetWorkType(inetAddress.getHostAddress(),EnumNetWorkType.CONN_WIFI);
                                }
                            }
                        }

                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }else if(info.getType() == ConnectivityManager.TYPE_ETHERNET){
                //有线网络
                try {
                    // 获取本地设备的所有网络接口
                    Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                            .getNetworkInterfaces();
                    while (enumerationNi.hasMoreElements()) {
                        NetworkInterface networkInterface = enumerationNi.nextElement();
                        String interfaceName = networkInterface.getDisplayName();
                        Log.i("tag", "网络名字" + interfaceName);

                        // 如果是有线网卡
                        if (interfaceName.equals("eth0")) {
                            Enumeration<InetAddress> enumIpAddr = networkInterface
                                    .getInetAddresses();

                            while (enumIpAddr.hasMoreElements()) {
                                // 返回枚举集合中的下一个IP地址信息
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                // 不是回环地址，并且是ipv4的地址
                                if (!inetAddress.isLoopbackAddress()
                                        && inetAddress instanceof Inet4Address) {
                                    Log.i("tag", inetAddress.getHostAddress() + "   ");

                                    return new NetWorkType(inetAddress.getHostAddress(), EnumNetWorkType.CONN_WIRE);
                                }
                            }
                        }
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }

        }
        //无网络
        return new NetWorkType("无法获取到IP",EnumNetWorkType.CONN_NO);
    }

    /**
     * 判断IP地址的合法性
     * return true合法
     * */
    public static boolean ipCheck(String addr) {
        if(TextUtils.isEmpty(addr)) return false;
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        return ipAddress;
    }
}
