package cn.hwh.sports.ble;

import android.app.Activity;

import cn.hwh.sports.activity.settings.NotificationActivity;
import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * Created by Raul.Fan on 2017/1/3.
 */

public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return NotificationActivity.class;
    }
}
