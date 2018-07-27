package com.wangsu.wsrtc.faceunity.view;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wangsu.wsrtc.faceunity.R;
import com.wangsu.wsrtc.faceunity.adapter.FilterAdapter;
import com.wangsu.wsrtc.faceunity.adapter.StickerAdapter;
import com.wangsu.wsrtc.faceunity.filter.FaceuYuvFilter;

public class FaceuFilterDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private static final String TAG = "FaceuFilterDiLog";

    private FaceuYuvFilter mFaceFilter = null;
    private RecyclerView mEffectRecyclerView;
    private StickerAdapter mStickerAdapter;
    private RecyclerView mFilterRecyclerView;
    private FilterAdapter mFilterAdapter;

    private int mUnselectGray;
    private int mSelectYellow;
    private Button mFaceShape0;
    private Button mFaceShape1;
    private Button mFaceShape2;
    private Button mFaceShape3;

    public FaceuFilterDialog() {
        super();
    }

    public void setActiveFilter(FaceuYuvFilter filter){
        mFaceFilter = filter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE,0);

        if(mFaceFilter == null) {
            mFaceFilter = new FaceuYuvFilter(getActivity().getApplicationContext());
        }
    }
    
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = inflater.inflate(R.layout.faceu_filter_content_view, container, false);

        //磨皮等级blur_level_group
        ViewGroup blurLevelGroup = (ViewGroup) rootView.findViewById(R.id.blur_level_group);
        TextView blurOptionText = (TextView) blurLevelGroup.findViewById(R.id.text_option);
        blurOptionText.setVisibility(View.GONE);
        TextView blurStyleStrength = (TextView) blurLevelGroup.findViewById(R.id.tv_strength);
        blurStyleStrength.setText((int)(mFaceFilter.getBeautyBlurLevel())+"");
        SeekBar blurSeekBar = (SeekBar) blurLevelGroup.findViewById(R.id.seek_bar_option);
        blurSeekBar.setMax(6);
        blurSeekBar.setProgress((int)(mFaceFilter.getBeautyBlurLevel()));
        blurSeekBar.setOnSeekBarChangeListener(this);

        //红润
        ViewGroup redLevelGroup = (ViewGroup) rootView.findViewById(R.id.red_level_group);
        TextView redOptionText = (TextView) redLevelGroup.findViewById(R.id.text_option);
        redOptionText.setVisibility(View.GONE);
        TextView redStyleStrength = (TextView) redLevelGroup.findViewById(R.id.tv_strength);
        redStyleStrength.setText((int)(mFaceFilter.getBeautyRedLevel()*100)+"");
        SeekBar redSeekBar = (SeekBar) redLevelGroup.findViewById(R.id.seek_bar_option);
        redSeekBar.setMax(100);
        redSeekBar.setProgress((int)(mFaceFilter.getBeautyRedLevel()*100));
        redSeekBar.setOnSeekBarChangeListener(this);

        //脸型
        mUnselectGray = getResources().getColor(R.color.unselect_gray);
        mSelectYellow = getResources().getColor(R.color.faceunityYellow);
        mFaceShape0 = (Button) rootView.findViewById(R.id.face_shape_0_nvshen);
        mFaceShape1 = (Button) rootView.findViewById(R.id.face_shape_1_wanghong);
        mFaceShape2 = (Button) rootView.findViewById(R.id.face_shape_2_ziran);
        mFaceShape3 = (Button) rootView.findViewById(R.id.face_shape_3_default);
        mFaceShape0.setOnClickListener(this);
        mFaceShape1.setOnClickListener(this);
        mFaceShape2.setOnClickListener(this);
        mFaceShape3.setOnClickListener(this);
        mFaceShape0.setBackgroundColor(mUnselectGray);
        mFaceShape1.setBackgroundColor(mUnselectGray);
        mFaceShape2.setBackgroundColor(mUnselectGray);
        mFaceShape3.setBackgroundColor(mUnselectGray);
        int shapeType = mFaceFilter.getFaceShapeType();
        if(shapeType == 0) {//女神
            mFaceShape0.setBackgroundColor(mSelectYellow);
        }else if(shapeType == 1) {//网红
            mFaceShape1.setBackgroundColor(mSelectYellow);
        }
        else if(shapeType == 2) {//自然
            mFaceShape2.setBackgroundColor(mSelectYellow);
        }else if(shapeType == 3) {//默认
            mFaceShape3.setBackgroundColor(mSelectYellow);
        }
        //程度
        ViewGroup faceShapeLevelGroup = (ViewGroup) rootView.findViewById(R.id.face_shape_level_group);
        TextView faceShapeOptionText = (TextView) faceShapeLevelGroup.findViewById(R.id.text_option);
        faceShapeOptionText.setText("程度");
        TextView faceShapeStyleStrength = (TextView) faceShapeLevelGroup.findViewById(R.id.tv_strength);
        faceShapeStyleStrength.setText((int)(mFaceFilter.getFaceShapeLevel()*100)+"");
        SeekBar faceShapeSeekBar = (SeekBar) faceShapeLevelGroup.findViewById(R.id.seek_bar_option);
        faceShapeSeekBar.setMax(100);
        faceShapeSeekBar.setProgress((int)(mFaceFilter.getFaceShapeLevel()*100));
        faceShapeSeekBar.setOnSeekBarChangeListener(this);
        //大眼
        ViewGroup eyeLevelGroup = (ViewGroup) rootView.findViewById(R.id.large_eye_level_group);
        TextView eyeOptionText = (TextView) eyeLevelGroup.findViewById(R.id.text_option);
        eyeOptionText.setText("大眼");
        TextView eyeStyleStrength = (TextView) eyeLevelGroup.findViewById(R.id.tv_strength);
        eyeStyleStrength.setText((int)(mFaceFilter.getBeautyEnlargeEyeLevel()*100)+"");
        SeekBar eyeSeekBar = (SeekBar) eyeLevelGroup.findViewById(R.id.seek_bar_option);
        eyeSeekBar.setMax(100);
        eyeSeekBar.setProgress((int)(mFaceFilter.getBeautyEnlargeEyeLevel()*100));
        eyeSeekBar.setOnSeekBarChangeListener(this);
        //瘦脸
        ViewGroup cheekLevelGroup = (ViewGroup) rootView.findViewById(R.id.cheek_thin_level_group);
        TextView cheekOptionText = (TextView) cheekLevelGroup.findViewById(R.id.text_option);
        cheekOptionText.setText("瘦脸");
        TextView cheekStyleStrength = (TextView) cheekLevelGroup.findViewById(R.id.tv_strength);
        cheekStyleStrength.setText((int)(mFaceFilter.getBeautyCheekThinLevel()*100)+"");
        SeekBar cheekSeekBar = (SeekBar) cheekLevelGroup.findViewById(R.id.seek_bar_option);
        cheekSeekBar.setMax(100);
        cheekSeekBar.setProgress((int)(mFaceFilter.getBeautyCheekThinLevel()*100));
        cheekSeekBar.setOnSeekBarChangeListener(this);

        //初始化道具布局
        mEffectRecyclerView = (RecyclerView) rootView.findViewById(R.id.effect_recycle_view);
        mEffectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mStickerAdapter = new StickerAdapter(getActivity());
        for(int i = 0; i < StickerAdapter.EFFECT_ITEM_FILE_NAME.length; i++) {
            if(StickerAdapter.EFFECT_ITEM_FILE_NAME[i].equals(mFaceFilter.getEffect())) {
                mStickerAdapter.setSelectedPosition(i);
            }
        }
        // change sticker
        mStickerAdapter.setClickStickerListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
                Log.d(TAG, "effect item selected " + position);
                mStickerAdapter.setSelectedPosition(position);
                mFaceFilter.setEffect(StickerAdapter.EFFECT_ITEM_FILE_NAME[position]);
                mStickerAdapter.notifyDataSetChanged();
            }
        });
        mEffectRecyclerView.setAdapter(mStickerAdapter);

        //初始化滤镜布局
        ViewGroup filterLevelGroup = (ViewGroup) rootView.findViewById(R.id.filter_style_level_group);
        TextView filterOptionText = (TextView) filterLevelGroup.findViewById(R.id.text_option);
        filterOptionText.setVisibility(View.GONE);
        final TextView filterStyleStrength = (TextView) filterLevelGroup.findViewById(R.id.tv_strength);
//        filterStyleStrength.setText((int)(mSenseFilter.getCurrentFilterStyleStrength()*100)+"");
        SeekBar filterLevelSeekBar = (SeekBar) filterLevelGroup.findViewById(R.id.seek_bar_option);
        filterLevelSeekBar.setMax(100);
//        mFilterLevelSeekBar.setProgress((int)(mSenseFilter.getCurrentFilterStyleStrength()*100));
        filterLevelSeekBar.setOnSeekBarChangeListener(this);

        mFilterRecyclerView = (RecyclerView) rootView.findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mFilterAdapter = new FilterAdapter(getActivity());
        for(int i = 0; i < FilterAdapter.FILTERS_NAME.length; i++) {
            if(FilterAdapter.FILTERS_NAME[i].equals(mFaceFilter.getFilter())) {
                mFilterAdapter.setSelectedPosition(i);
            }
        }
        mFilterAdapter.setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
                Log.d(TAG, "filter item selected " + position);
                mFilterAdapter.setSelectedPosition(position);
                mFaceFilter.setFilter(FilterAdapter.FILTERS_NAME[position]);
                mFilterAdapter.notifyDataSetChanged();
            }
        });
        mFilterRecyclerView.setAdapter(mFilterAdapter);

        return rootView;
    }
    
    @Override
    public void onStart() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float ratio = dm.widthPixels < dm.heightPixels ? 0.95f : 0.65f;
        getDialog().getWindow().setLayout((int)(dm.widthPixels * ratio), getDialog().getWindow().getAttributes().height);

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;

        window.setAttributes(windowParams);
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setListViewHeight(ListView listView) {
        int totalHeight = 0;
        android.widget.ListAdapter adapter = listView.getAdapter();

        //遍历控件
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listView);
            //测量一下子控件的高度
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        //控件之间的间隙
        totalHeight += listView.getDividerHeight() * (listView.getCount() - 1);
        totalHeight += listView.getPaddingBottom() + listView.getPaddingTop();

        //2、赋值给ListView的LayoutParams对象
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!fromUser) return;
        View parent = (View) seekBar.getParent().getParent();
        if(parent == null) {
            Log.e(TAG,"");
        }
        TextView strengthTv = (TextView) parent.findViewById(R.id.tv_strength);
        if(strengthTv != null) {
            strengthTv.setText(progress+"");
        }
        int parentId = parent.getId();
        if(parentId == R.id.filter_style_level_group) {
            float value = (float) progress / 100;
            if(mFaceFilter != null){
                mFaceFilter.setBeautyColorLevel(value);
            }
        }else if(parentId == R.id.blur_level_group) {
            if(mFaceFilter != null){
                //0~6
                mFaceFilter.setBeautyBlurLevel(progress);
            }
        }else if(parentId == R.id.red_level_group) {
            float value = (float) progress / 100;
            if(mFaceFilter != null){
                mFaceFilter.setBeautyRedLevel(value);
            }
        }else if(parentId == R.id.face_shape_level_group) {
            float value = (float) progress / 100;
            if(mFaceFilter != null){
                mFaceFilter.setFaceShapeLevel(value);
            }
        }else if(parentId == R.id.large_eye_level_group) {
            float value = (float) progress / 100;
            if(mFaceFilter != null){
                mFaceFilter.setBeautyEnlargeEyeLevel(value);
            }
        }else if(parentId == R.id.cheek_thin_level_group) {
            float value = (float) progress / 100;
            if(mFaceFilter != null){
                mFaceFilter.setBeautyCheekThinLevel(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        mFaceShape0.setBackgroundColor(mUnselectGray);
        mFaceShape1.setBackgroundColor(mUnselectGray);
        mFaceShape2.setBackgroundColor(mUnselectGray);
        mFaceShape3.setBackgroundColor(mUnselectGray);
        int vId = v.getId();
        if(vId == R.id.face_shape_0_nvshen) {
            mFaceShape0.setBackgroundColor(mSelectYellow);
            mFaceFilter.setFaceShapeType(0);//女神
        }else if(vId == R.id.face_shape_1_wanghong) {
            mFaceShape1.setBackgroundColor(mSelectYellow);
            mFaceFilter.setFaceShapeType(1);//网红
        }else if(vId == R.id.face_shape_2_ziran) {
            mFaceShape2.setBackgroundColor(mSelectYellow);
            mFaceFilter.setFaceShapeType(2);//自然
        }else if(vId == R.id.face_shape_3_default) {
            mFaceShape3.setBackgroundColor(mSelectYellow);
            mFaceFilter.setFaceShapeType(3);//默认
        }
    }
}
