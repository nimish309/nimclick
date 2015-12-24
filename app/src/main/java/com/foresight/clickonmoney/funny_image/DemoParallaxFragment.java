package com.foresight.clickonmoney.funny_image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DemoParallaxFragment extends Fragment {

    private MainActivity mainActivity;
    private DemoParallaxAdapter mCatsAdapter;

    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    //Image Picker
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ImageView image;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View v = inflater.inflate(R.layout.image_fragment_parallax, container, false);
        image = (ImageView) v.findViewById(R.id.image);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(mainActivity));
        imageLoader.displayImage(Constants.api + getArguments().getString("image"), image, options);
        imageLoader.setDefaultLoadingListener(new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                ((ImageView) v.findViewById(R.id.imageView)).setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ((ImageView) v.findViewById(R.id.imageView)).setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

//        image.setImageResource(getArguments().getInt("image"));
//        image.post(new Runnable() {
//            @Override
//            public void run() {
//                Matrix matrix = new Matrix();
//                matrix.reset();
//
//                float wv = image.getWidth();
//                float hv = image.getHeight();
//
//                float wi = image.getDrawable().getIntrinsicWidth();
//                float hi = image.getDrawable().getIntrinsicHeight();
//
//                float width = wv;
//                float height = hv;
//
//                if (wi / wv > hi / hv) {
//                    matrix.setScale(hv / hi, hv / hi);
//                    width = wi * hv / hi;
//                } else {
//                    matrix.setScale(wv / wi, wv / wi);
//                    height = hi * wv / wi;
//                }
//
//                matrix.preTranslate((wv - width) / 2, (hv - height) / 2);
//                image.setScaleType(ImageView.ScaleType.MATRIX);
//                image.setImageMatrix(matrix);
//            }
//        });


//        scaleGestureDetector = new ScaleGestureDetector(mainActivity, new ScaleListener());

        return v;
    }

    public void setAdapter(DemoParallaxAdapter catsAdapter) {
        mCatsAdapter = catsAdapter;
    }


    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            image.setImageMatrix(matrix);
            return true;
        }
    }
}
