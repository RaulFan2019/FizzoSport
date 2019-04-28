package cn.hwh.sports.config;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Raul.Fan on 2016/11/6.
 */

public class FileConfig {

    /**
     * 文件总目录
     */
    public final static String DEFAULT_PATH = Environment.getExternalStorageDirectory() + File.separator
            + "fizo" + File.separator;

    /**
     * 捕捉Crash文件存放位置
     */
    public final static String CRASH_PATH = DEFAULT_PATH + "crash";

    public final static String SYNC_PATH = DEFAULT_PATH + "sync";
    /**
     * 记录
     */
    public final static String RECORD_PATH = DEFAULT_PATH + "record";

    public final static String DOWNLOAD_PATH = DEFAULT_PATH + "download" + File.separator;
    public final static String DOWNLOAD_UPDATE_DEVICE_ZIP = DOWNLOAD_PATH + "update.zip";

    /**
     * 裁剪图片临时存放位置
     */
    public final static String DEFAULT_SAVE_CUT_BITMAP = DEFAULT_PATH + "pic" + File.separator;
    public final static Uri cutFileUri = Uri.parse("file://" + "/" + DEFAULT_SAVE_CUT_BITMAP + "/" + "cut.jpg");
}
