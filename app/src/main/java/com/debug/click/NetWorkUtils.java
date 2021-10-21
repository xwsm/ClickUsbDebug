package com.debug.click;
public class NetWorkUtils {
    /**
     * 检查网络是否可用
     *
     * @param paramContext
     * @return
     */
    public static boolean checkEnable(android.content.Context paramContext) {
        boolean i = false;
        android.net.NetworkInfo localNetworkInfo = ((android.net.ConnectivityManager) paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(android.content.Context context) {
        try {

            android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
            android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }
//GPRS连接下的ip
public String getLocalIpAddress() {
    try {
        for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            java.net.NetworkInterface intf = en.nextElement();
            for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
    } catch (java.net.SocketException ex) {
        android.util.Log.e("WifiPreference", ex.toString());
    }
    return null;
}}