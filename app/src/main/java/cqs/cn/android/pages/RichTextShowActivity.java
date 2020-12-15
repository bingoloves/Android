package cqs.cn.android.pages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gyf.immersionbar.ImmersionBar;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.baselib.base.BaseActivity;
import cn.cqs.baselib.http.MMKVHelper;
import cn.cqs.baselib.utils.AnyLayerUtils;
import cn.cqs.baselib.utils.DensityUtils;
import cn.cqs.baselib.utils.log.LogUtils;
import cn.cqs.baselib.widget.CustomToolbar;
import cqs.cn.android.R;
import cqs.cn.android.utils.CommonUtil;
import cqs.cn.android.utils.ImageUtils;
import cqs.cn.android.utils.MyGlideEngine;
import cqs.cn.android.utils.PermissionsUtils;
import cqs.cn.android.utils.RichTextEditor;
import cqs.cn.android.utils.RichTextView;
import cqs.cn.android.utils.SDCardUtil;
import cqs.cn.android.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.autosize.utils.ScreenUtils;
import per.goweii.anylayer.DialogLayer;

import static cqs.cn.android.utils.StringUtils.IMAGE;

/**
 * 富文本页面
 */
public class RichTextShowActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.et_title)
    EditText titleEt;
    @BindView(R.id.rich_text)
    RichTextEditor richText;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richtext_show);
        customToolbar.back(v -> finish());

        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);

        //initRichTextEditorListener();
        String content = MMKVHelper.decodeString(RichTextActivity.NODE);
        richText.post(() -> showEditData(content));
    }
    protected void showEditData(String content) {
        richText.clearAllLayout();
        List<String> textList = StringUtils.cutStringByImgTag(content);
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            if (text.contains("<img")) {
                String imagePath = StringUtils.getSrc(text,IMAGE);
                int width = CommonUtil.getScreenWidth(this);
                int height = CommonUtil.getScreenHeight(this);
                richText.measure(0,0);
                Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, width, height);
                if (bitmap != null){
                    //richText.addImageViewAtIndex(richText.getLastIndex(), bitmap, imagePath);
                } else {
                    richText.addEditTextAtIndex(richText.getLastIndex(), text);
                }
                richText.addEditTextAtIndex(richText.getLastIndex(), text);
            }
        }
    }
    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
