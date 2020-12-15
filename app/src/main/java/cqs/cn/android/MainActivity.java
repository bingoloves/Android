package cqs.cn.android;

import android.os.Bundle;
import android.widget.Button;
import com.gyf.immersionbar.ImmersionBar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.baselib.base.BaseActivity;
import cn.cqs.baselib.bean.PopupMenu;
import cn.cqs.baselib.utils.AnyLayerUtils;
import cn.cqs.baselib.widget.CustomToolbar;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.DragLayout;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.btn_test)
    Button testBtn;

    DialogLayer popupLayer;

    @OnClick(R.id.btn_test)
    public void clickEvent(){
        navigateTo(TwoActivity.class);
//        if (popupLayer.isShow()){
//            popupLayer.dismiss();
//        } else {
//            popupLayer.show();
//        }
//        AnyLayerUtils.showDrawDialog(DragLayout.DragStyle.Top,R.layout.layout_drag_dialog_top,null);
        //AnyLayerUtils.showAlertDialog("提示","消息通知",null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPopup();
    }

    private void initPopup() {
        PopupMenu menu = new PopupMenu(new String[]{"自由房","出租房","空置房"});
        popupLayer = AnyLayerUtils.popupSingleSelect(testBtn, menu,0,R.layout.layout_single_choose_item,(holder, value, position) -> {
            holder.setText(R.id.tv_name,value);
            holder.setTextColorRes(R.id.tv_name,menu.getIndex() == position?R.color.color_218EFF:R.color.color_00172F);
            holder.setVisible(R.id.iv_checked,menu.getIndex() == position);
            holder.setVisible(R.id.line,position != menu.getMenusList().size()-1);
        }, (position, name) -> testBtn.setText(name));
    }

    @Override
    protected void setActivityEnterAnimation() {

    }

    @Override
    protected void setActivityExitAnimation() {

    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
