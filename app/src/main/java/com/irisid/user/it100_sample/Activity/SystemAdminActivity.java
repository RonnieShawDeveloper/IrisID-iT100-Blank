package com.irisid.user.it100_sample.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;
import com.irisid.user.it100_sample.Settings.SettingsActivity;
import com.irisid.user.it100_sample.UserList.ItemsListActivity;

import static com.irisid.user.it100_sample.IrisApplication.EXTRA_CIRCULAR_REVEAL_X;
import static com.irisid.user.it100_sample.IrisApplication.EXTRA_CIRCULAR_REVEAL_Y;
import static com.irisid.user.it100_sample.UserList.ItemsListFragment.itemArrayList;

public class SystemAdminActivity extends BaseBackgroundActivity implements View.OnClickListener {
    Button user_mgnt_btn;
    Button settings_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_admin);

        IrisApplication.activateBackButton(this, R.id.back_linear);
        ((TextView)findViewById(R.id.title)).setText(getResources().getText(R.string.system_admin));

        rootLayout = findViewById(R.id.rootview);
        videoView = (VideoView)findViewById(R.id.videoView);

        user_mgnt_btn = (Button) findViewById(R.id.user_mgnt_btn);
        settings_btn = (Button) findViewById(R.id.settings_btn);
        user_mgnt_btn.setOnClickListener(this);
        settings_btn.setOnClickListener(this);

        AdminLoginActivity AA = AdminLoginActivity.adminLoginActivity;
        if(AA != null) {
            AA.finish();
        }
        itemArrayList.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_mgnt_btn:
                if(IrisApplication.isActivate) {
                    presentActivity(v);
                } else {
                    //Logger.d("SystemAdminActivity  @user_mgnt_btn click " + IrisApplication.isActivate);
                }
                break;

            case R.id.settings_btn:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

                default:
                    break;
        }
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, ItemsListActivity.class);
        intent.putExtra(EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

}
