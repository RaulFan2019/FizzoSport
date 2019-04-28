package cn.hwh.sports.activity.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raul.Fan on 2017/3/31.
 */

public class TestEntranceActivity extends Activity {

    private ListView listView;
    //private List<String> data = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        listView = new ListView(this);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
        setContentView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class c = null;
                switch (i){
                    case 0:
//                        c = PermissionTestActivity.class;
                        break;
                    case 1:
//                        c = ToastTestActivity.class;
                        break;
                    case 2:
//                        c = FizzoSDKTestScanActivity.class;
                        break;
                    case 3:
                        c = WxTestActivity.class;
                        break;
                    case 4:
//                        c = EffortSelectTestActivity.class;
                        break;
                    case 5:
                        c = TestDashBoardViewActivity.class;
                        break;
                    case 6:
                        c = TestRunningBtnsAnimActivity.class;
                                break;

                }
                Intent intent = new Intent(TestEntranceActivity.this,c);
                startActivity(intent);
            }
        });
    }



    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        data.add("权限功能测试");
        data.add("Toast功能测试");
        data.add("Fizzo SDK 功能测试");
        data.add("微信功能测试");
        data.add("运动模式选择");
        data.add("DashBoard");
        data.add("室外跑步按钮");

        return data;
    }
}
