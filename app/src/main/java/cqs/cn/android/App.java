package cqs.cn.android;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sendtion.xrichtext.IImageLoader;
import com.sendtion.xrichtext.XRichText;

import cn.cqs.baselib.base.BaseApplication;
import cqs.cn.android.utils.TransformationScale;

/**
 * Created by bingo on 2020/12/15.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/15
 */

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        XRichText.getInstance().setImageLoader(new IImageLoader() {
            @Override
            public void loadImage(final String imagePath, final ImageView imageView, final int imageHeight) {
                Log.e("---", "imageHeight: "+imageHeight);
                //如果是网络图片
                if (imagePath.startsWith("http://") || imagePath.startsWith("https://")){
                    Glide.with(getApplicationContext()).asBitmap().load(imagePath).dontAnimate()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageHeight > 0) {//固定高度
                                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                                FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                                        lp.bottomMargin = 10;//图片的底边距
                                        imageView.setLayoutParams(lp);
                                        Glide.with(getApplicationContext()).asBitmap().load(imagePath).centerCrop()
                                                .placeholder(R.mipmap.img_load_fail).error(R.mipmap.img_load_fail).into(imageView);
                                    } else {//自适应高度
                                        Glide.with(getApplicationContext()).asBitmap().load(imagePath)
                                                .placeholder(R.mipmap.img_load_fail).error(R.mipmap.img_load_fail).into(new TransformationScale(imageView));
                                    }
                                }
                            });
                } else { //如果是本地图片
                    if (imageHeight > 0) {//固定高度
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                        lp.bottomMargin = 10;//图片的底边距
                        imageView.setLayoutParams(lp);

                        Glide.with(getApplicationContext()).asBitmap().load(imagePath).centerCrop()
                                .placeholder(R.mipmap.img_load_fail).error(R.mipmap.img_load_fail).into(imageView);
                    } else {//自适应高度
                        Glide.with(getApplicationContext()).asBitmap().load(imagePath)
                                .placeholder(R.mipmap.img_load_fail).error(R.mipmap.img_load_fail).into(new TransformationScale(imageView));
                    }
                }
            }
        });
    }
}
