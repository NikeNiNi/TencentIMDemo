package com.tencent.qcloud.tim.uikit.modules.group.info;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;


public class GroupInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_activity);
        GroupInfoFragment fragment = new GroupInfoFragment();
        Bundle bundle = getIntent().getExtras();
        String str= bundle.getString(TUIKitConstants.Group.GROUP_ID);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.group_manager_base, fragment).commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        setResult(1001);
    }
}
