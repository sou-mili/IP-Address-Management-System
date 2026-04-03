package com.ipam.util;

public class IPUtils {

    public static boolean isValidCIDR(String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) return false;

            int prefix = Integer.parseInt(parts[1]);
            return prefix >= 0 && prefix <= 32;
        } catch (Exception e) {
            return false;
        }
    }

    public static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;

        for (int i = 0; i < 4; i++) {
            result = result * 256 + Integer.parseInt(parts[i]);
        }
        return result;
    }

    public static String longToIp(long ip) {
        return ((ip >> 24) & 255) + "." +
               ((ip >> 16) & 255) + "." +
               ((ip >> 8) & 255) + "." +
               (ip & 255);
    }

    public static void calculateSubnetDetails(com.ipam.model.Subnet subnet) {

        String[] parts = subnet.getCidr().split("/");
        String ip = parts[0];
        int prefix = Integer.parseInt(parts[1]);

        long ipLong = ipToLong(ip);

        long mask = ~((1L << (32 - prefix)) - 1);

        long network = ipLong & mask;
        long broadcast = network | ~mask;

        subnet.setNetworkAddress(longToIp(network));
        subnet.setBroadcastAddress(longToIp(broadcast));

        long total = (long) Math.pow(2, (32 - prefix));
        subnet.setTotalIps((int) total);

        subnet.setFirstIp(longToIp(network + 1));
        subnet.setLastIp(longToIp(broadcast - 1));
    }
}