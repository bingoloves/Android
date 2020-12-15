package cqs.cn.android.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.cqs.baselib.adapter.recyclerview.CommonAdapter;
import cn.cqs.baselib.adapter.recyclerview.base.ViewHolder;
import cn.cqs.baselib.adapter.recyclerview.utils.CustomItemDecoration;
import cn.cqs.baselib.adapter.recyclerview.utils.GridSpacingItemDecoration;
import cn.cqs.baselib.base.BaseFragment;
import cn.cqs.baselib.utils.DensityUtils;
import cn.cqs.baselib.utils.log.LogUtils;
import cn.cqs.baselib.widget.CustomToolbar;
import cqs.cn.android.R;
import cqs.cn.android.bean.Bookmark;
import cqs.cn.android.pages.bookmark.WebActivity;

/**
 * Created by bingo on 2020/12/15.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 书签页
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/15
 */
public class BookmarkFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.commonRv)
    RecyclerView commonRv;

    private CommonAdapter adapter;
    private List<Bookmark> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bookmark;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        initData();
        initAdapter();
        commonRv.setLayoutManager(new GridLayoutManager(getContext(),2));
        commonRv.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(getContext(),12),DensityUtils.dp2px(getContext(),15),false));
        commonRv.setAdapter(adapter);
    }

    private void initData() {
        list.add(new Bookmark("Android","测试地址1","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址2","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址3","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址4","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址5","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址6","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址7","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址8","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址9","http://www.baidu.com"));
        list.add(new Bookmark("Android","测试地址10","http://www.baidu.com"));
    }
    private int[] bgColors = {R.color.color_E1F5F1,R.color.color_E7F2FF,R.color.color_E6F0FE,R.color.color_FCECE3};
    private int[] textColors = {R.color.color_00A783,R.color.color_308EFD,R.color.color_355FE7,R.color.color_FF671D};
    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new CommonAdapter<Bookmark>(getContext(),R.layout.layout_bookmark_item,list) {
            @Override
            protected void convert(ViewHolder holder, Bookmark bean, int position) {
                holder.setText(R.id.tv_name,bean.name);
//                int random = (int)(300+Math.random()*400);
//                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//                layoutParams.height = random;//DensityUtils.dp2px(getContext(),random);
                //随机颜色
                int randomColor = (int)(Math.random()* 4);
                holder.setBackgroundColorRes(R.id.root,bgColors[randomColor]);
                holder.setTextColorRes(R.id.tv_name,textColors[randomColor]);
                holder.setOnClickListener(R.id.root, v -> WebActivity.openWeb(getContext(),bean.path));
            }
        };
    }
}
