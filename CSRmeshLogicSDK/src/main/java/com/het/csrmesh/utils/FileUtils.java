package com.het.csrmesh.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileUtils {

    /**
     * Method to write text characters to file on SD card.
     *
     * @param filename
     * @param text
     * @return
     */
    static public File writeToSDFile(String filename, String text) {

        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File(root.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, filename);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(text);
            pw.flush();
            pw.close();
            f.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    /**
     * Write binary data to a file on the SD card.
     * @param filename The name of the file to create.
     * @param data Byte array of data to write.
     * @return File object representing the file that was created, or null if there was a failure.
     */
    static public File writeToSDFile(String filename, byte [] data)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath());

        File returnedFile = null;

        if (dir.mkdirs() || dir.isDirectory()) {
            returnedFile = new File(dir, filename);
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(returnedFile));
                out.write(data);
                out.flush();
                out.close();
            }
            catch (Exception e) {
                returnedFile = null;
            }
        }

        return returnedFile;
    }

    /**
     * Return the absolute path of the external storage directory in Android.
     * @return External storage directory Android path
     */
    static public String getRootAbsolutePath() {
        return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get the extension of a file.
     *
     * @param file
     * @return extension.
     */
    static public String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf("."));

        }
        catch (Exception e) {
            return "";
        }

    }
}
