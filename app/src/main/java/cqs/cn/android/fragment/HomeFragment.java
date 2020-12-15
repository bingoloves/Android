package cqs.cn.android.fragment;

import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.baselib.base.BaseFragment;
import cn.cqs.baselib.widget.CustomToolbar;
import cqs.cn.android.R;
import cqs.cn.android.pages.RichTextActivity;
import cqs.cn.android.pages.RichTextShowActivity;

/**
 * Created by bingo on 2020/12/15.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/15
 */
public class HomeFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @OnClick({R.id.btn_richText_editor,R.id.btn_richText_show})
    public void clickEvent(View v){
        switch (v.getId()){
            case R.id.btn_richText_editor:
                navigateTo(RichTextActivity.class);
                break;
            case R.id.btn_richText_show:
                navigateTo(RichTextShowActivity.class);
                break;
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
