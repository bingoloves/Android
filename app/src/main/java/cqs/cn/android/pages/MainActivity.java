package cqs.cn.android.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;

import butterknife.BindView;
import cn.cqs.baselib.adapter.BaseViewPagerAdapter;
import cn.cqs.baselib.base.BaseActivity;
import cn.cqs.baselib.widget.CustomViewPager;
import cqs.cn.android.R;
import cqs.cn.android.bean.TabEntity;
import cqs.cn.android.fragment.BookmarkFragment;
import cqs.cn.android.fragment.HomeFragment;
import cqs.cn.android.fragment.MineFragment;

public class MainActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.bottomTab)
    CommonTabLayout bottomTab;

    private String[] mTitles = {"首页", "书签", "我的"};
    private int[] mIconUnselectIds = {R.mipmap.ic_tabbar_home, R.mipmap.ic_tabbar_functions, R.mipmap.ic_tabbar_mine};
    private int[] mIconSelectIds = {R.mipmap.ic_tabbar_home_select, R.mipmap.ic_tabbar_functions_select, R.mipmap.ic_tabbar_mine_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        mViewPager.setAdapter(new BaseViewPagerAdapter(getSupportFragmentManager(),mFragments,mTitles));
        bottomTab.setTabData(mTabEntities);
        bottomTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position,false);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomTab.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initData() {
        if (mTabEntities != null){
            for (int i = 0; i < mTitles.length; i++) {
                mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            }
        }
        if (mFragments != null){
            mFragments.add(new HomeFragment());
            mFragments.add(new BookmarkFragment());
            mFragments.add(new MineFragment());
        }
    }


    @Override
    protected void setActivityEnterAnimation() {

    }

    @Override
    protected void setActivityExitAnimation() {

    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }
}
