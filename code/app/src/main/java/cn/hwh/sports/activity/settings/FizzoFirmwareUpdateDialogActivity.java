package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;

/**
 * Created by Raul.Fan on 2017/3/9.
 */

public class FizzoFirmwareUpdateDialogActivity extends BaseActivity {


    private String mFtpUrl;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_firmware_update_dialog;
    }

    @OnClick({R.id.btn_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                if(BleManager.getBleManager().mBleConnectE != null
                        && BleManager.getBleManager().mBleConnectE.mIsConnected){
                    Bundle bundle = new Bundle();
                    bundle.putString("ftp",mFtpUrl);
                    startActivity(FizzoDeviceUpdateActivity.class,bundle);
                    finish();
                }else {
                    Toasty.error(FizzoFirmwareUpdateDialogActivity.this,"手环已断开").show();
                }
                break;
        }
    }

    @Override
    protected void initData() {
        mFtpUrl = getIntent().getExtras().getString("ftp");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        LocalApplication.getInstance().needShowUpdateFirmWareDialog = true;
    }


}
