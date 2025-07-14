package com.xiongdwm.fiberGDB.support.binlogSync.manager;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class BinlogPositionManager {

    @Resource
    private FrdbConfig frdbConfig;

    public void savePosition(String binlogFilename, long position) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(frdbConfig.getPath() + File.separator + frdbConfig.getLocalname()))) {
            dos.writeUTF(binlogFilename);
            dos.writeLong(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BinlogPosition loadPosition() {
        File file = new File(frdbConfig.getPath() + File.separator + frdbConfig.getLocalname());
        if (!file.exists()) {
            System.out.println("position NOT FOUND");
            savePosition(frdbConfig.getBinname(), frdbConfig.getPosition());
            System.out.println("file created");
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
