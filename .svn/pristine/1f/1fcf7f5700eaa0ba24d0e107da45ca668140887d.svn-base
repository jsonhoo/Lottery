package com.wangsu.wsrtc.faceunity.filter;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.faceunity.wrapper.faceunity;
import com.wangsu.wsrtc.faceunity.adapter.FilterAdapter;
import com.wangsu.wsrtc.faceunity.adapter.StickerAdapter;
import com.wangsu.wsrtc.faceunity.object.authpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guozr on 2017/7/28.
 */

public class FaceuYuvFilter implements Handler.Callback {

    private static final String TAG = "FaceuVideoFilter";

    private static final int HANDLE_CREATE_ITEM = 0;

    private Context mContext = null;

    private Handler mAsyncHandler = null;
    private HandlerThread mAsyncHandlerThread = null;

    private long mStartTime;
    private boolean mFaceuInitialized = false;

    //用户设置的道具
    private int mFaceBeautyItem = 0; //美颜道具
    private int mEffectItem = 0; //贴纸道具
    private int mGestureItem = 0; //手势道具
    //当前加载成功的道具
    private int[] itemsArray = {mFaceBeautyItem, mEffectItem, mGestureItem};

    private int mFrameId = 0;
    private boolean mUseBeauty = true;
    private boolean mUseGesture = false;
    private boolean isNeedEffectItem = true;
    private boolean isInAvatarMode = false;
    private String mEffectFileName = StickerAdapter.EFFECT_ITEM_FILE_NAME[0];
    private String mFilterName = FilterAdapter.FILTERS_NAME[0];

    private float mFaceBeautyColorLevel = 0.2f;
    private float mFaceBeautyBlurLevel = 6.0f;
    private float mFaceBeautyCheekThin = 1.0f;
    private float mFaceBeautyEnlargeEye = 0.5f;
    private float mFaceBeautyRedLevel = 0.5f;
    private int mFaceShapeType = 3;
    private float mFaceShapeLevel = 0.5f;
    private int mYuvWidth, mYuvHeight;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;


    public FaceuYuvFilter(Context context) {
        mContext = context;
    }

    public void createEGLContext() {
        faceunity.fuCreateEGLContext();
    }

    public void init() {
        initFaceu();
        mAsyncHandlerThread = new HandlerThread("SenseFilter_Thread");
        mAsyncHandlerThread.start();
        mAsyncHandler = new Handler(mAsyncHandlerThread.getLooper(),this);
        //尝试重新加载贴纸资源
        String preEffectName = mEffectFileName;
        mEffectFileName = StickerAdapter.EFFECT_ITEM_FILE_NAME[0];
        itemsArray[1] = mEffectItem = 0;
        setEffect(preEffectName);
        Log.i(TAG, "init()");
    }
    public byte[] renderToNV21Image(byte[] data, int width, int height, int cameraId) {
        boolean deviceLost = false;
        /**
         * 1.宽高发生改变时清空缓存，解决宽高发生改变时的花屏和崩溃问题；
         * 2.摄像头id改变时清空缓存，解决摄像头切换时一帧图像会显示颠倒问题；
         */
        if(mYuvWidth != width || mYuvHeight != height || mCameraId != cameraId) {
            mYuvWidth = width;
            mYuvHeight = height;
            mCameraId = cameraId;
            faceunity.fuOnDeviceLost();
            /**
             * 通过deviceLost标志位丢帧，采用此方式规避faceu设计引起的bug,
             * faceu设计会缓存一帧画面，即传入第二帧数据时，fuRenderToNV21Image方法返回第一帧处理好的数据；
             * 由于有这个设计，调用fuOnDeviceLost方法清空缓存后，传入第一帧数据返回的是一块空的buffer，显示为绿屏画面；
             * 丢掉调用fuOnDeviceLost方法后的一帧绿屏数据。
             */
            deviceLost = true;
        }
        mStartTime = SystemClock.elapsedRealtime();
        if (isNeedEffectItem) {
            isNeedEffectItem = false;
            mAsyncHandler.sendEmptyMessage(HANDLE_CREATE_ITEM);
        }
        /**
         * 当滤镜设置为美白滤镜 "nature" 时，通过参数 color_level 来控制美白程度。
         * 当滤镜为其他风格化滤镜时，该参数用于控制风格化程度。
         * 该参数取值为大于等于0的浮点数，0为无效果，1为默认效果，大于1为继续增强效果。
         */
        faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
        //磨皮程度，该参数的推荐取值范围为[0, 6]，0为无效果，对应7个不同的磨皮程度。
        faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
        //设置滤镜类型
        faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
        //控制脸大小。此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。
        faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
        //控制眼睛大小,此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。
        faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
        //目前支持四种基本脸型：默认（3）、女神（0）、网红（1）、自然（2）。
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShapeType);
        /**
         * 用以控制变化到指定基础脸型的程度。该参数的取值范围为[0, 1]。0为无效果，即关闭美型，1为指定脸型。
         * 若要关闭美型，可将 face_shape_level 设置为0。
         */
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
        //控制红润程度，该参数的推荐取值范围为[0, 1]，0为无效果，0.5为默认效果，大于1为继续增强效果
        faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);

        //long fuStartTime = System.currentTimeMillis();
        int fuTex = faceunity.fuRenderToNV21Image(data,
                mYuvWidth, mYuvHeight, mFrameId++, itemsArray, mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X);
        //long fuEndTime = System.currentTimeMillis();
        //等待所有GPU指令执行完毕
        GLES20.glFinish();
        //Log.i(TAG,"faceunity handle one frame cost time : " + (fuEndTime - fuStartTime));

        //Log.i(TAG,"onDrawFrame total cost time : " + (SystemClock.elapsedRealtime() - mStartTime));
        return deviceLost ? null : data;
    }

    public void destroy() {
        if(mAsyncHandler != null) {
            //清空item创建队列
            mAsyncHandler.removeMessages(HANDLE_CREATE_ITEM);
            mAsyncHandlerThread.quit();
            //尝试等待线程结束
            try {
                mAsyncHandlerThread.join();
            } catch (InterruptedException e) {
            }
        }

        //释放faceu相关资源
        destroyFaceu();
        mFaceuInitialized = false;
        Log.i(TAG, "destroy()");

    }

    public void releaseEGLContext() {
        faceunity.fuReleaseEGLContext();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_CREATE_ITEM:
                try {
                    final int tmp = itemsArray[1];
                    if (mEffectFileName.equals("none")) {
                        itemsArray[1] = mEffectItem = 0;
                    } else {
                        InputStream is = mContext.getAssets().open(mEffectFileName);
                        byte[] itemData = new byte[is.available()];
                        int len = is.read(itemData);
                        Log.e("FU", "effect len " + len);
                        is.close();
                        Log.i(TAG, "fuCreateItemFromPackage begin : " + mEffectFileName);
                        itemsArray[1] = mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                        faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
                        /**
                         * 新版制作工具里涉及背景和全屏的元素的道具，所代表的角度；
                         * 使用新版本工具制作道具时需要修改此参数
                         */
                        faceunity.fuItemSetParam(mEffectItem, "rotationAngle",0);
                        Log.i(TAG, "fuCreateItemFromPackage end : " + mEffectFileName);
                    }
                    if (tmp != 0) {
                        faceunity.fuDestroyItem(tmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void setEffect(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        isInAvatarMode = effectItemName.equals("lixiaolong.bundle");
        if(mAsyncHandler != null) {
            mAsyncHandler.removeMessages(HANDLE_CREATE_ITEM);
        }
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
    }

    public String getEffect(){
        return  mEffectFileName;
    }

    public void setFilter(String filterName) {
        mFilterName = filterName;
    }

    public String getFilter(){
        return  mFilterName;
    }

    /**
     * 当滤镜设置为美白滤镜 "nature" 时，通过参数 color_level 来控制美白程度。
     * 当滤镜为其他风格化滤镜时，该参数用于控制风格化程度。
     * 该参数取值为大于等于0的浮点数，0为无效果，1为默认效果，大于1为继续增强效果。
     */
    public void setBeautyColorLevel(float colorLevel){
        mFaceBeautyColorLevel = colorLevel;
    }

    public float getBeautyColorLevel(){
        return mFaceBeautyColorLevel;
    }

    /**
     * 磨皮程度，该参数的推荐取值范围为[0, 6]，0为无效果，对应7个不同的磨皮程度。
     */
    public void setBeautyBlurLevel(float blurLevel){
        mFaceBeautyBlurLevel = blurLevel;
    }

    public float getBeautyBlurLevel(){
        return mFaceBeautyBlurLevel;
    }

    /**
     * 控制脸大小。此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。
     * @param thinLevel
     */
    public void setBeautyCheekThinLevel(float thinLevel){
        mFaceBeautyCheekThin = thinLevel;
    }

    public float getBeautyCheekThinLevel(){
        return mFaceBeautyCheekThin;
    }

    /**
     * 控制眼睛大小,此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。
     * @param eyeLevel
     */
    public void setBeautyEnlargeEyeLevel(float eyeLevel){
        mFaceBeautyEnlargeEye = eyeLevel;
    }

    public float getBeautyEnlargeEyeLevel(){
        return mFaceBeautyEnlargeEye;
    }
    /**
     * 控制红润程度，该参数的推荐取值范围为[0, 1]，0为无效果，0.5为默认效果，大于1为继续增强效果
     * @param redLevel
     */
    public void setBeautyRedLevel(float redLevel){
        mFaceBeautyRedLevel = redLevel;
    }

    public float getBeautyRedLevel(){
        return mFaceBeautyRedLevel;
    }

    /**
     * 目前支持四种基本脸型：默认（3）、女神（0）、网红（1）、自然（2）。
     * @param shapeType
     */
    public void setFaceShapeType(int shapeType){
        mFaceShapeType = shapeType;
    }

    public int getFaceShapeType(){
        return mFaceShapeType;
    }

    /**
     * 用以控制变化到指定基础脸型的程度。该参数的取值范围为[0, 1]。0为无效果，即关闭美型，1为指定脸型。
     * 若要关闭美型，可将 face_shape_level 设置为0。
     * @param shapeLevel
     */
    public void setFaceShapeLevel(float shapeLevel){
        mFaceShapeLevel = shapeLevel;
    }

    public float getFaceShapeLevel(){
        return mFaceShapeLevel;
    }


    private void initFaceu() {
        Log.i(TAG, "initFaceu: ");
        try {
            InputStream is = mContext.getAssets().open("v3.mp3");
            byte[] v3data = new byte[is.available()];
            int len = is.read(v3data);
            is.close();
            faceunity.fuSetup(v3data, null, authpack.A());
            mFaceuInitialized = true;
            //faceunity.fuSetMaxFaces(1);//设置最大识别人脸数目
            Log.e(TAG, "fuSetup v3 len " + len);

            if (mUseBeauty) {
                is = mContext.getAssets().open("face_beautification.mp3");
                byte[] itemData = new byte[is.available()];
                len = is.read(itemData);
                Log.e(TAG, "beautification len " + len);
                is.close();
                mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                itemsArray[0] = mFaceBeautyItem;
            }

            if (mUseGesture) {
                is = mContext.getAssets().open("heart.mp3");
                byte[] itemData = new byte[is.available()];
                len = is.read(itemData);
                Log.e(TAG, "heart len " + len);
                is.close();
                mGestureItem = faceunity.fuCreateItemFromPackage(itemData);
                itemsArray[2] = mGestureItem;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void destroyFaceu() {
        Log.i(TAG, "destroyFaceu: ");
        if(!mFaceuInitialized) {
            Log.i(TAG, "filter has not initialized ignore destroyFaceu request.");
            return;
        }
        boolean needDestroyItem = false;
        for(int i = 0; i < itemsArray.length; i++) {
            if(itemsArray[i] != 0) {
                needDestroyItem = true;
            }
            //reset loaded item flags
            itemsArray[i] = 0;
        }
        if(needDestroyItem) {
            Log.i(TAG, "destroyFaceu: fuDestroyAllItems");
            faceunity.fuDestroyAllItems();
        }
        /**
         * 通知faceunity设备的opengl环境发生改变，
         * 否则第二次创建的opengl环境会关联之前创建的opengl环境,
         * 导致画面卡住
         */
        faceunity.fuOnDeviceLost();
        mFrameId = 0;

    }

    private void save(byte[] data, int offset, int length) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");
            String fileName = simpleDateFormat.format(new Date()) + ".yuv";
            File dir = new File(Environment.getExternalStorageDirectory(), "screen/");
            if(!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, fileName);
            RandomAccessFile mYuvFile = new RandomAccessFile(file, "rw");
            mYuvFile.write(data, offset, length);
            mYuvFile.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "open file exception ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
