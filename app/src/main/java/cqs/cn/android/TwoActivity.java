package cqs.cn.android;

import android.os.Bundle;
import com.gyf.immersionbar.ImmersionBar;
import butterknife.BindView;
import cn.cqs.baselib.base.BaseActivity;
import cn.cqs.baselib.widget.CustomToolbar;

public class TwoActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        customToolbar.back(v -> finish());
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
