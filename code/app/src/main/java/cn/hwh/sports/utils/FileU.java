package cn.hwh.sports.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2016/6/6.
 */
public class FileU {


    /**
     * 写入日志文件
     * @param log 日志内容
     * @param name 目录/文件名称
     */
    public static void writeLog(final String log, final String name) {
        String timestamp = System.currentTimeMillis() + "";
        String filename = name + timestamp;
        try {
            FileOutputStream stream = new FileOutputStream(filename);
            OutputStreamWriter output = new OutputStreamWriter(stream);
            BufferedWriter bw = new BufferedWriter(output);

            bw.write(log);
            bw.newLine();
            bw.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入日志文件
     * @param log 日志内容
     * @param name 目录/文件名称
     */
    public static void writeTxt(final String log, final String name) {
        String timestamp = System.currentTimeMillis() + ".txt";
        String filename = name + timestamp;
        try {
            FileOutputStream stream = new FileOutputStream(filename);
            OutputStreamWriter output = new OutputStreamWriter(stream);
            BufferedWriter bw = new BufferedWriter(output);

            bw.write(log);
            bw.newLine();
            bw.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     * @param file
     * @return
     */
    public static String ReadTxtFile(final File file) {
        String content = ""; //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("ReadTxtFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (FileNotFoundException e) {
                Log.d("ReadTxtFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("ReadTxtFile", e.getMessage());
            }
        }
        return content;
    }


    /***
     * 保存图片
     *
     * @param bm 图片文件
     * @return
     */
    public static File saveBigBitmap(Bitmap bm) {
        String folderPath = Environment.getExternalStorageDirectory() + File.separator;

        File f = new File(folderPath, System.currentTimeMillis() + "png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

}
