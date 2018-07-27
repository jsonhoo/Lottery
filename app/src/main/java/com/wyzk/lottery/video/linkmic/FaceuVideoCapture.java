package com.wyzk.lottery.video.linkmic;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;

import com.wangsu.wsrtc.faceunity.filter.FaceuYuvFilter;
import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wangsu.wsrtc.utils.ALog;

import org.webrtc.SurfaceTextureHelper;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FaceuVideoCapture extends CustomVideoCapture implements Handler.Callback {
    private static final String TAG = "FaceuVideoCapture";

    private static final int HANDLE_RENDER_YUV = 0;
    private Handler mGlHandler = null;
    private HandlerThread mGlThread = null;
    private FaceuYuvFilter mFaceuFilter = null;
    private ConcurrentLinkedQueue<YuvFrame> mYuvFrameQueue = null;
    private boolean mHasEglContext = false;

    public FaceuVideoCapture(Context applicationContext, WSSurfaceView previewView) {
        super(applicationContext, previewView);
        if (mFaceuFilter == null) {
            mFaceuFilter = new FaceuYuvFilter(applicationContext);
        }
        if (mYuvFrameQueue == null) {
            mYuvFrameQueue = new ConcurrentLinkedQueue<>();
        }
    }

    public FaceuYuvFilter getFaceuFilter() {
        return mFaceuFilter;
    }

    //初始化自定义视频源，这个方法是被API内部调用的，
    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        super.initialize(surfaceTextureHelper, context, capturerObserver);
        if (mGlHandler == null) {
            mGlThread = new HandlerThread("Faceu_Render_Thread");
            mGlThread.start();
            mGlHandler = new Handler(mGlThread.getLooper(), this);
        }
        initFilter();
    }

    /**
     * Start capturing frames in a format that is as close as possible to |width| x |height| and
     * |framerate|.
     */
    @Override
    public void startCapture(int width, int height, int framerate) {
        super.startCapture(width, height, framerate);
    }

    @Override
    public void switchCamera(CameraSwitchHandler cameraSwitchHandler) {
        super.switchCamera(cameraSwitchHandler);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_RENDER_YUV:
                YuvFrame frame = (YuvFrame) msg.obj;
                if (!mHasEglContext || state != SessionState.RUNNING) {
                    ALog.i(TAG, "Invalid frame mHasEglContext " + mHasEglContext + ", state " + state);
                    mYuvFrameQueue.offer(frame);
                    return true;
                }
                byte[] proccessData = mFaceuFilter.renderToNV21Image(frame.data, frame.width, frame.height, frame.cameraId);
                if (proccessData != null) {
                    final long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    capturerObserver.onByteBufferFrameCaptured(proccessData, frame.width, frame.height, frame.rotation, captureTimeNs);
                }
                mYuvFrameQueue.offer(frame);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void stopCapture() {
        super.stopCapture();
    }

    @Override
    public void dispose() {
        super.dispose();
        destroyFilter();
        if (mGlHandler != null) {
            mGlHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    mGlThread.quit();
                }
            });
            //尝试等待线程结束
            try {
                mGlThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mGlHandler = null;
        }
    }

    protected byte[] processNV21Image(byte[] image, int width, int height, int cameraId, int frameRotation) {
        YuvFrame yuvFrame = mYuvFrameQueue.poll();
        if (yuvFrame == null) {
//            ALog.i(TAG, "yuv frame null");
            return null;
        }
        if (yuvFrame.data == null || yuvFrame.width != width || yuvFrame.height != height) {
            yuvFrame.data = new byte[image.length];
            yuvFrame.width = width;
            yuvFrame.height = height;
        }
        image = super.processNV21Image(image, width, height, cameraId, frameRotation);
        System.arraycopy(image, 0, yuvFrame.data, 0, image.length);
        yuvFrame.cameraId = cameraId;
        yuvFrame.rotation = frameRotation;
        Handler glHandler = mGlHandler;
        if (glHandler != null) {
            Message msg = glHandler.obtainMessage(HANDLE_RENDER_YUV);
            msg.obj = yuvFrame;
            glHandler.sendMessage(msg);
        }
        return null;
    }


    private void initFilter() {
        ALog.i(TAG, "initFilter ...");
        //缓存两帧
        for (int i = 0; i < 2; i++) {
            mYuvFrameQueue.offer(new YuvFrame());
        }
        if (mGlHandler != null) {
            mGlHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    mFaceuFilter.createEGLContext();
                    mFaceuFilter.init();
                    mHasEglContext = true;
                    ALog.i(TAG, "initFilter done");
                }
            });
        }
    }

    private void destroyFilter() {
        ALog.i(TAG, "destroyFilter ...");
        if (mGlHandler != null) {
            final CountDownLatch barrier = new CountDownLatch(1);
            //清空item创建队列
            mGlHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    mFaceuFilter.destroy();
                    mFaceuFilter.releaseEGLContext();
                    mHasEglContext = false;
                    barrier.countDown();
                    ALog.i(TAG, "destroyFilter done");
                }
            });
            try {
                barrier.await();
            } catch (InterruptedException e) {
            }
        }
        mYuvFrameQueue.clear();
    }

    private class YuvFrame {
        byte[] data;
        int width;
        int height;
        int cameraId;
        int rotation;
    }

}
