package com.xiongdwm.fiberGDB.support.binlogSync.manager;

import java.io.*;

public class BinlogPositionManager {

    private static final String POSITION_FILE = "binlog_position.dat";

    public static void savePosition(String binlogFilename, long position) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(POSITION_FILE))) {
            dos.writeUTF(binlogFilename);
            dos.writeLong(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BinlogPosition loadPosition() {
        File file = new File(POSITION_FILE);
        if (!file.exists()) {
            System.out.println("position NOT FOUND");
            return null;
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            String binlogFilename = dis.readUTF();
            long position = dis.readLong();
            return new BinlogPosition(binlogFilename, position);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public record BinlogPosition(String binlogFilename, long position) {

        public String getBinlogFilename() {
            return binlogFilename;
        }

        public long getPosition() {
            return position;
        }
    }
}
