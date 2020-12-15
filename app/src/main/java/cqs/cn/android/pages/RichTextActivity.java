package cqs.cn.android.pages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.baselib.base.BaseActivity;
import cn.cqs.baselib.http.MMKVHelper;
import cn.cqs.baselib.utils.AnyLayerUtils;
import cn.cqs.baselib.utils.DensityUtils;
import cn.cqs.baselib.utils.Utils;
import cn.cqs.baselib.utils.log.LogUtils;
import cn.cqs.baselib.widget.CustomToolbar;
import cqs.cn.android.R;
import cqs.cn.android.utils.CommonUtil;
import cqs.cn.android.utils.ImageUtils;
import cqs.cn.android.utils.MyGlideEngine;
import cqs.cn.android.utils.PermissionsUtils;
import cqs.cn.android.utils.RichTextEditor;
import cqs.cn.android.utils.SDCardUtil;
import cqs.cn.android.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import per.goweii.anylayer.DialogLayer;

/**
 * 富文本页面
 */
public class RichTextActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.et_title)
    EditText titleEt;
    @BindView(R.id.rich_editor)
    RichTextEditor richTextEditor;
    @OnClick({R.id.tv_add_image,R.id.tv_add_video,R.id.tv_save})
    public void clickEvent(View v){
        switch (v.getId()){
            case R.id.tv_add_image:
                openGallery(false);
                break;
            case R.id.tv_add_video:
                openGallery(true);
                break;
            case R.id.tv_save:
                saveNoteData(false);
                break;
        }
    }
    private DialogLayer loading;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richtext);
        customToolbar.back(v -> finish());
        initPermissions();
        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);
        openSoftKeyInput();
        initRichTextEditorListener();
    }
    private String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private void initPermissions() {
        PermissionsUtils.request(this, needPermissions, accept -> {
            if (!accept){
                PermissionsUtils.gotoPermissionSetting(activity);
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            //如果APP处于后台，或者手机锁屏，则保存数据
            if (CommonUtil.isAppOnBackground(getApplicationContext()) ||
                    CommonUtil.isLockScreeen(getApplicationContext())){
                saveNoteData(true);//处于后台时保存数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
    //定义请求码常量
    private static final int REQUEST_CODE_CHOOSE_IMAGE = 23;
    private static final int REQUEST_CODE_CHOOSE_VIDEO = 24;
    public static final String NODE = "node";
    /**
     * 调用图库选择
     */
    private void openGallery(boolean isVideo){
//        //调用系统图库
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");// 相片类型
//        startActivityForResult(intent, 1);

        Matisse matisse = Matisse.from(this);
        SelectionCreator selectionCreator;
        if (isVideo){
            selectionCreator = matisse.choose(MimeType.of(MimeType.MP4));//照片视频全部显示MimeType.allOf()
        } else {
            selectionCreator = matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF));//照片视频全部显示MimeType.allOf()
        }
        selectionCreator.countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(1)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(DensityUtils.dp2px(this,120))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true,"cqs.cn.android.fileprovider"))//存储到哪里
                .forResult(isVideo?REQUEST_CODE_CHOOSE_VIDEO:REQUEST_CODE_CHOOSE_IMAGE);//请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == 1){
                    //处理调用系统图库
                } else if (requestCode == REQUEST_CODE_CHOOSE_IMAGE){
                    //异步方式插入图片
                    insertImagesSync(data);
                } else if (requestCode == REQUEST_CODE_CHOOSE_VIDEO){
                    //异步方式插入视频
                    insertVideoSync(data);
                }
            }
        }
    }

    /**
     * 异步方式插入图片
     */
    private void insertImagesSync(final Intent data){
        if (loading == null){
            loading = AnyLayerUtils.loading();
        }
        if (!loading.isShow()){
            loading.show();
        }
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try{
                richTextEditor.measure(0, 0);
                List<Uri> mSelected = Matisse.obtainResult(data);
                // 可以同时插入多张图片
                for (Uri imageUri : mSelected) {
                    String imagePath = SDCardUtil.getFilePathFromUri(activity,imageUri);
                    //Log.e(TAG, "###path=" + imagePath);
                    Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, screenWidth, screenHeight);//压缩图片
                    //bitmap = BitmapFactory.decodeFile(imagePath);
                    imagePath = SDCardUtil.saveToSdCard(bitmap);
                    //Log.e(TAG, "###imagePath="+imagePath);
                    emitter.onNext(imagePath);
                }
                // 测试插入网络图片 http://pics.sc.chinaz.com/files/pic/pic9/201904/zzpic17414.jpg
                //emitter.onNext("http://pics.sc.chinaz.com/files/pic/pic9/201903/zzpic16838.jpg");
                //emitter.onNext("http://b.zol-img.com.cn/sjbizhi/images/10/640x1136/1572123845476.jpg");
                //emitter.onNext("https://img.ivsky.com/img/tupian/pre/201903/24/richu_riluo-013.jpg");
                emitter.onComplete();
            }catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                        if (loading != null && loading.isShow()) {
                            loading.dismiss();
                        }
                        toast("图片插入成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading != null && loading.isShow()) {
                            loading.dismiss();
                        }
                        toast("图片插入失败:"+e.getMessage());
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String imagePath) {
                        richTextEditor.insertImage(imagePath);
                    }
                });
    }
    /**
     * 异步方式插入图片
     */
    private void insertVideoSync(final Intent data){
        if (loading == null){
            loading = AnyLayerUtils.loading();
        }
        if (!loading.isShow()){
            loading.show();
        }
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try{
                richTextEditor.measure(0, 0);
                List<Uri> mSelected = Matisse.obtainResult(data);
                for (Uri videoUri : mSelected) {
                    LogUtils.e(videoUri.getPath());
                    //String videoPath = SDCardUtil.getFilePathFromUri(activity,videoUri);
                    //Log.e(TAG, "###path=" + imagePath);
                    //Bitmap bitmap = ImageUtils.getSmallBitmap(videoPath, screenWidth, screenHeight);//压缩图片
                    //bitmap = BitmapFactory.decodeFile(imagePath);
                    //videoPath = SDCardUtil.saveToSdCard(bitmap);
                    //Log.e(TAG, "###imagePath="+imagePath);
                    //emitter.onNext(videoPath);
                }
                emitter.onNext("https://mov.bn.netease.com/open-movie/nos/mp4/2016/06/22/SBP8G92E3_hd.mp4");
                // 测试插入网络图片 http://pics.sc.chinaz.com/files/pic/pic9/201904/zzpic17414.jpg
                //emitter.onNext("http://pics.sc.chinaz.com/files/pic/pic9/201903/zzpic16838.jpg");
                //emitter.onNext("http://b.zol-img.com.cn/sjbizhi/images/10/640x1136/1572123845476.jpg");
                //emitter.onNext("https://img.ivsky.com/img/tupian/pre/201903/24/richu_riluo-013.jpg");
                emitter.onComplete();
            }catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                        if (loading != null && loading.isShow()) {
                            loading.dismiss();
                        }
                        toast("视频插入成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading != null && loading.isShow()) {
                            loading.dismiss();
                        }
                        toast("视频插入失败:"+e.getMessage());
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String videoPath) {
                        LogUtils.e(videoPath);
                        richTextEditor.insertJzvdStd(videoPath,"","");
                    }
                });
    }
    private void initRichTextEditorListener(){
        // 图片删除事件
        richTextEditor.setOnRtImageDeleteListener(imagePath -> {
            if (!TextUtils.isEmpty(imagePath)) {
                boolean isOK = SDCardUtil.deleteFile(imagePath);
                if (isOK) {
                    toast("删除成功：" + imagePath);
                }
            }
        });
        // 图片点击事件
        richTextEditor.setOnRtImageClickListener((view, imagePath) -> {
            try {
                String myContent = getEditData();
                if (!TextUtils.isEmpty(myContent)){
                    List<String> imageList = StringUtils.getTextFromHtml(myContent, true);
                    if (!TextUtils.isEmpty(imagePath)) {
                        int currentPosition = imageList.indexOf(imagePath);
                        toast("点击图片：" + currentPosition + "：" + imagePath);

                        List<Uri> dataList = new ArrayList<>();
                        for (int i = 0; i < imageList.size(); i++) {
                            dataList.add(ImageUtils.getUriFromPath(imageList.get(i)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private String getEditData() {
        StringBuilder content = new StringBuilder();
        try {
            List<RichTextEditor.EditData> editList = richTextEditor.buildEditData();
            for (RichTextEditor.EditData itemData : editList) {
                if (itemData.inputStr != null) {
                    content.append(itemData.inputStr);
                } else if (itemData.imagePath != null) {
                    content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
                }else if (itemData.videoPath != null) {
                    content.append("<video controls=\\\"controls\\\" autoplay=\\\"autoplay\\\" width=\\\"100%\\\" height=\\\"auto\\\"><source src=\\\"").append(itemData.videoPath).append("\\\" type=\\\"video/ogg\\\"/>Your browser does not support the video tag.</video>");
                }
                content.append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 保存数据
     */
    private void saveNoteData(boolean isBackground) {
        String noteTitle = titleEt.getText().toString();
        String noteContent = getEditData();
        try {
            MMKVHelper.encode(NODE,noteContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNoteData(false);
    }
    /**
     * 关闭软键盘
     */
    private void closeSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && imm.isActive() && getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //imm.hideSoftInputFromInputMethod();//据说无效
            //imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); //强制隐藏键盘
            //如果输入法在窗口上已经显示，则隐藏，反之则显示
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开软键盘
     */
    private void openSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && !imm.isActive() && richTextEditor != null){
            richTextEditor.requestFocus();
            //第二个参数可设置为0
            //imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);//强制显示
            imm.showSoftInputFromInputMethod(richTextEditor.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
        }
    }
}
