package com.mercury.discovery.utils;

import java.lang.management.ManagementFactory;
import java.net.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import java.util.UUID;

public class IDGenerator {
    public static void main(String[] args) {
        System.out.println(getServerIdentity());

        Snowflake s = new Snowflake(275);
        for (int i = 0; i < 300; i++) {
            long id = s.nextId();
            long []p = s.parse(id);
            System.out.println(id +"\t"+ p[0] +"\t"+p[1]+"\t"+p[2]);
        }
    }

    private static String SERVER_IDENTITY;

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getShortUUID() {
        long longValue = java.nio.ByteBuffer.wrap(IDGenerator.getUUID().getBytes()).getLong();
        return Long.toString(longValue, Character.MAX_RADIX);
    }

    public static String getServerIdentity() {
        if (SERVER_IDENTITY == null) {
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(ManagementFactory.getRuntimeMXBean().getName());

                InetAddress ip = getFirstNonLoopbackAddress(true, false);
                if (ip != null) {
                    String hostAddress = ip.getHostAddress();
                    sb.append("|").append(hostAddress);
                }


                /*
                //Not working on linux
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                byte[] mac = network.getHardwareAddress();
                String macAddress = String.format("%02X:%02X:%02X:%02X:%02X:%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
                sb.append("|").append(macAddress);
                */
            } catch (SocketException e) {
                e.printStackTrace();
            }

            SERVER_IDENTITY = sb.toString();
        }

        return SERVER_IDENTITY;
    }

    private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }

    public static class Snowflake {
        private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
        private static final int EPOCH_BITS = 41;
        private static final int NODE_ID_BITS = 10;
        private static final int SEQUENCE_BITS = 12;

        private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
        private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

        // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
        private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;

        private final long nodeId;
        private final long customEpoch;

        private volatile long lastTimestamp = -1L;
        private volatile long sequence = 0L;

        // Let Snowflake generate a nodeId
        public Snowflake() {
            this.nodeId = createNodeId();
            this.customEpoch = DEFAULT_CUSTOM_EPOCH;
        }

        // Create Snowflake with a nodeId
        public Snowflake(long nodeId) {
            this(nodeId, DEFAULT_CUSTOM_EPOCH);
        }

        // Create Snowflake with a nodeId and custom epoch
        public Snowflake(long nodeId, long customEpoch) {
            if (nodeId < 0 || nodeId > maxNodeId) {
                throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, maxNodeId));
            }
            this.nodeId = nodeId;
            this.customEpoch = customEpoch;
        }

        public synchronized long nextId() {
            long currentTimestamp = timestamp();

            if (currentTimestamp < lastTimestamp) {
                throw new IllegalStateException("Invalid System Clock!");
            }

            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) & maxSequence;
                if (sequence == 0) {
                    // Sequence Exhausted, wait till next millisecond.
                    currentTimestamp = waitNextMillis(currentTimestamp);
                }
            } else {
                // reset sequence to start with zero for the next millisecond
                sequence = 0;
            }

            lastTimestamp = currentTimestamp;

            return currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
                    | (nodeId << SEQUENCE_BITS)
                    | sequence;
        }


        // Get current timestamp in milliseconds, adjust for the custom epoch.
        private long timestamp() {
            return Instant.now().toEpochMilli() - customEpoch;
        }

        // Block and wait till next millisecond
        private long waitNextMillis(long currentTimestamp) {
            while (currentTimestamp == lastTimestamp) {
                currentTimestamp = timestamp();
            }
            return currentTimestamp;
        }

        private long createNodeId() {
            long nodeId;
            try {
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null) {
                        for (byte macPort : mac) {
                            sb.append(String.format("%02X", macPort));
                        }
                    }
                }
                nodeId = sb.toString().hashCode();
            } catch (Exception ex) {
                nodeId = (new SecureRandom().nextInt());
            }
            nodeId = nodeId & maxNodeId;
            return nodeId;
        }

        public long[] parse(long id) {
            long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
            long maskSequence = (1L << SEQUENCE_BITS) - 1;

            long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS)) + customEpoch;
            long nodeId = (id & maskNodeId) >> SEQUENCE_BITS;
            long sequence = id & maskSequence;

            return new long[]{timestamp, nodeId, sequence};
        }

        @Override
        public String toString() {
            return "Snowflake Settings [EPOCH_BITS=" + EPOCH_BITS + ", NODE_ID_BITS=" + NODE_ID_BITS
                    + ", SEQUENCE_BITS=" + SEQUENCE_BITS + ", CUSTOM_EPOCH=" + customEpoch
                    + ", NodeId=" + nodeId + "]";
        }

    }
}
