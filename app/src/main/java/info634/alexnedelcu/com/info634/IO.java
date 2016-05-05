package info634.alexnedelcu.com.info634;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import info634.alexnedelcu.com.info634.metrics.Metric;

/**
 * Created by Alex on 4/26/2016.
 */
public class IO {

    public static void save(String csv, String filename) {
        File logFile = new File("/storage/sdcard0/"+filename);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(csv);
            buf.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void remove(String filename) {
        File logFile = new File("/storage/sdcard0/"+filename);
        if (logFile.exists())
        {
            logFile.delete();
        }
    }
}
