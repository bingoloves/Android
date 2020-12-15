package cqs.cn.android.fragment;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import cn.cqs.baselib.base.BaseFragment;
import cn.cqs.baselib.widget.CustomToolbar;
import cqs.cn.android.R;

/**
 * Created by bingo on 2020/12/15.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/15
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
