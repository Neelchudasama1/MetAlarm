package com.metalarm.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by qtm-kalpesh on 21/7/16.
 */
public class MetalarmSync_Log {
    public static String file = "Metalarm_log.txt";
    public static String filePath = Environment.getExternalStorageDirectory()
            + File.separator + "Metalarm" + File.separator;

    private static SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault());

    public static void writeToLog(String data) {
        try {
            data = "\n\n\n" + sdf.format(new Date(Calendar.getInstance().getTimeInMillis())) + ", " + data;
            File mFolder = new File(filePath);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            File f = new File(filePath, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fOut = new FileOutputStream(f, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // function to clear log file data.
    public static void clearLogData() {
        File f = new File(filePath, file);
        if (f.exists()) {
            boolean isDeleted = f.delete();
            Log.e("File", isDeleted ? "File is deleted." : "Not able to delete it.");
        } else {
            Log.e("File", "File does not exists.");
        }
    }

    public static void writeToLog(Exception data) {
        try {
            File f = new File(filePath, file);
            FileOutputStream fOut = new FileOutputStream(f, true);

            PrintStream ps = new PrintStream(fOut, false);
            data.printStackTrace(ps);
            fOut.flush();
            fOut.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}