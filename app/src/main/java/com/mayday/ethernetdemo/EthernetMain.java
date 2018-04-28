package com.mayday.ethernetdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by m5_pc on 2018/3/20.
 */

public class EthernetMain {

    private static final String TAG = "MAYDAY";
    private EthernetManager ethernetManager;
    private Context mContext;
    private ContentResolver contentResolver;
    private StaticIpConfiguration mStaticIpConfiguration;
    private IpConfiguration mIpConfiguration;

    public EthernetMain(Context context) {
        this.mContext=context;
        if (ethernetManager == null) {
            ethernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        }
        init();
    }

    private void init() {
        contentResolver = mContext.getContentResolver();
    }

    //打开以太网
    public void openEth() {
        if (ethernetManager.getEthernetIfaceState() == ethernetManager.ETHER_IFACE_STATE_UP) {
            //已打开以太网
            Toast.makeText(mContext, "已打开以太网,不需要调用该函数了！", Toast.LENGTH_SHORT).show();
        } else {
            ethernetManager.setEthernetEnabled(true);
            Toast.makeText(mContext, "已打开以太网！", Toast.LENGTH_SHORT).show();
        }
    }

    //关闭以太网
    public void closeEth() {
        ethernetManager.setEthernetEnabled(false);
        Toast.makeText(mContext, "已关闭以太网！", Toast.LENGTH_SHORT).show();
    }

    //设置为动态ip
    public void dhcpEth() {
        if (ethernetManager.getEthernetIfaceState() != ethernetManager.ETHER_IFACE_STATE_UP) {
            Toast.makeText(mContext, "请先开启以太网开关！", Toast.LENGTH_SHORT).show();
        }else {
            Settings.System.putInt(contentResolver, Settings.System.ETHERNET_USE_STATIC_IP, 0);
            mIpConfiguration=new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE,null, ProxyInfo.buildDirectProxy(null,0));
            ethernetManager.setConfiguration(mIpConfiguration);
            Toast.makeText(mContext, "设置动态IP成功!", Toast.LENGTH_SHORT).show();
        }

    }

    //设置为静态IP
    /**
     *
     * @param ip IP地址
     * @param fix 子网掩码
     * @param dns1 DNS1
     * @param dns2 DNS2
     * @param gw 默认网关
     */
    public void staticEth(String ip, String fix, String dns1,String dns2, String gw) {
        if (ethernetManager.getEthernetIfaceState() != ethernetManager.ETHER_IFACE_STATE_UP) {
            Toast.makeText(mContext, "请先开启以太网开关！", Toast.LENGTH_SHORT).show();
        }else {
            Settings.System.getInt(contentResolver, Settings.System.ETHERNET_USE_STATIC_IP, 1);
            //如果输入的IP地址格式正确
            if (isIpAddress(ip)) {
                mStaticIpConfiguration = new StaticIpConfiguration();
                Inet4Address inetAddr = EthernetUtils.getIPv4Address(ip);
                int prefixLength = EthernetUtils.maskStr2InetMask(fix);
                InetAddress dnsAddr = EthernetUtils.getIPv4Address(dns1);
                InetAddress gatewayAddr = EthernetUtils.getIPv4Address(gw);

                if (inetAddr.getAddress().toString().isEmpty() || prefixLength == 0 || gatewayAddr.toString().isEmpty()
                        || dnsAddr.toString().isEmpty()) {
                    Log.i(TAG, "ip,mask or dnsAddr is wrong");
                    Toast.makeText(mContext, "参数有误!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Class<?> clazz = null;

                try {
                    clazz = Class.forName("android.net.LinkAddress");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Class[] cl = new Class[]{InetAddress.class, int.class};
                Constructor cons = null;
                try {
                    cons = clazz.getConstructor(cl);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                Object[] x = {inetAddr, prefixLength};
                String dnsStr2 = dns2;

                try {
                    mStaticIpConfiguration.ipAddress = (LinkAddress) cons.newInstance(x);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                mStaticIpConfiguration.gateway = gatewayAddr;
                mStaticIpConfiguration.dnsServers.add(dnsAddr);
                if (!dnsStr2.isEmpty()) {
                    mStaticIpConfiguration.dnsServers.add(EthernetUtils.getIPv4Address(dnsStr2));
                }
                mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);
                ethernetManager.setConfiguration(mIpConfiguration);

                //put到系统数据库
                Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_IP, ip);
                Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_NETMASK, fix);
                Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_DNS1, dns1);
                Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_GATEWAY, gw);

                Toast.makeText(mContext, "设置静态IP成功！", Toast.LENGTH_SHORT).show();

            } else {
                Log.i(TAG, "IP地址格式有误...");
            }

        }
    }


    //判断ip地址的正确性
    private boolean isIpAddress(String value) {
        int start = 0;
        int end = value.indexOf('.');
        int numBlocks = 0;

        while (start < value.length()) {
            if (end == -1) {
                end = value.length();
            }

            try {
                int block = Integer.parseInt(value.substring(start, end));
                if ((block > 255) || (block < 0)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }

            numBlocks++;

            start = end + 1;
            end = value.indexOf('.', start);
        }
        return numBlocks == 4;
    }

}
