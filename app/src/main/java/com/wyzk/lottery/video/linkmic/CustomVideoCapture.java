package com.wyzk.lottery.video.linkmic;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import android.view.WindowManager;

import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wangsu.wsrtc.utils.ALog;

import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.Size;
import org.webrtc.SurfaceTextureHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangb2 on 17.9.14.
 */

public class CustomVideoCapture implements CameraVideoCapturer {
    private static final String TAG = "CustomVideoCapture";
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    protected int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    protected Context applicationContext;
    protected CapturerObserver capturerObserver;
    protected SessionState state;
    protected WSSurfaceView mPreviewSurfaceView = null;
    SurfaceTexture surfaceTexture;
    private Camera camera;
    private Camera.CameraInfo info;
    private CaptureFormat captureFormat;
    private byte[] processedNV21Image = null;
    private int videoWidth;
    private int videoHeight;
    private int fps;
    private boolean isDisposed = false;
    private Handler cameraHandler;

    public CustomVideoCapture(Context applicationContext, WSSurfaceView previewView) {
        this.applicationContext = applicationContext;
        mPreviewSurfaceView = previewView;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    private void checkNotDisposed() {
        if (isDisposed) {
            throw new RuntimeException("capturer is disposed.");
        }
    }

    /**
     * 初始化自定义视频源，这个方法是被API内部调用的，
     */
    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        if (capturerObserver == null) {
            throw new RuntimeException("capturerObserver not set.");
        }
        this.applicationContext = context;
        this.capturerObserver = capturerObserver;

        cameraHandler = surfaceTextureHelper.getHandler();
        surfaceTexture = surfaceTextureHelper.getSurfaceTexture();
        ALog.d(TAG, "initialize");
    }

    /**
     * Start capturing frames in a format that is as close as possible to |cameraWidth| x |cameraHeight| and
     * |framerate|.
     */
    @Override
    public void startCapture(final int width, final int height, final int framerate) {
        ALog.d(TAG, "startCapture");
        checkNotDisposed();
        this.videoWidth = width;
        this.videoHeight = height;
        this.fps = framerate;

        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                startCaptureInternal();
            }
        });
    }

    private void createCapture(int width, int height, int framerate) {
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            ALog.e(TAG, e.getMessage());
            return;
        }

        //在个别机型如小米4等，如果不设置setPreviewTexture或setPreviewDisplay,那么setPreviewCallbackWithBuffer不会出帧
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            ALog.e(TAG, "setPreviewTexture error ", e);
        }

        info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        final Camera.Parameters parameters = camera.getParameters();

        captureFormat = findClosestCaptureFormat(parameters, width, height, framerate);
        final Size pictureSize = findClosestPictureSize(parameters, width, height);
        updateCameraParameters(camera, parameters, captureFormat, pictureSize, false);

        // Initialize the capture buffers.
        final int frameSize = captureFormat.frameSize();
        for (int i = 0; i < NUMBER_OF_CAPTURE_BUFFERS; ++i) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
            camera.addCallbackBuffer(buffer.array());
        }
        // Calculate orientation manually and send it as CVO insted.
        camera.setDisplayOrientation(0 /* degrees */);
    }

    private void startCaptureInternal() {
        ALog.i(TAG, "startCaptureInternal " + cameraId);
        createCapture(videoWidth, videoHeight, fps);

        camera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }
                stopCameraInternal();
                if (error == Camera.CAMERA_ERROR_EVICTED) {
                    ALog.e(TAG, "camera disconnected.");
                } else {
                    ALog.e(TAG, errorMessage);
                }
            }
        });

        listenForBytebufferFrames();

        state = SessionState.RUNNING;
        try {
            camera.startPreview();
        } catch (RuntimeException e) {
            stopCameraInternal();
            ALog.e(TAG, e.getMessage());
        }

        capturerObserver.onCapturerStarted(true);
        if (mPreviewSurfaceView != null) {
            /**
             * 关闭预览mirror，
             * 该方法必须在camera每次启动后调用，
             * 否则会使用sdk内部默认值（前置预览镜像，后置不镜像）
             * 自定义视频源编码镜像由yuv方向决定，预览是否镜像由该方法控制。
             */
            mPreviewSurfaceView.setMirror(false);
        }
        ALog.d(TAG, "startCapture done");
    }

    @Override
    public void stopCapture() {
        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                stopCameraInternal();
            }
        });
    }

    @Override
    public void changeCaptureFormat(int width, int height, int framerate) {

    }

    @Override
    public void dispose() {
        ALog.d(TAG, "dispose");
        isDisposed = true;
    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    @Override
    public void switchCamera(CameraSwitchHandler cameraSwitchHandler) {
        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                switchCameraInternal(null);
            }
        });
    }

    @Override
    public void switchCameraFlashMode(CameraFlashModeSwitchHandler cameraFlashModeSwitchHandler) {

    }

    @Override
    public void switchCameraFocusMode(CameraFocusModeSwitchHandler cameraFocusModeSwitchHandler) {

    }

    @Override
    public void handleFocusMetering(Rect rect) {

    }

    @Override
    public void handleZoom(boolean b) {

    }

    @Override
    public void addMediaRecorderToCamera(MediaRecorder mediaRecorder, MediaRecorderHandler mediaRecorderHandler) {

    }

    @Override
    public void removeMediaRecorderFromCamera(MediaRecorderHandler mediaRecorderHandler) {

    }

    protected byte[] processNV21Image(byte[] image, int width, int height, int cameraId, int frameRotation) {
        //后置摄像头不做mirror操作
        if (cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return image;
        }
        //防止不断实例化byte数组，减少内存gc
        if (processedNV21Image == null || processedNV21Image.length != image.length) {
            processedNV21Image = new byte[image.length];
        }
        /**
         * mPreviewSurfaceView.setMirror(false),
         * 同时此处做mirror处理，则视频流和本地预览均镜像
         */
        if (frameRotation % 180 == 0) {//0或180yuv做左右翻转
            nv21FlipHorizontal(image, processedNV21Image, width, height);
        } else {//90或270做上下翻转
            nv21FlipVertical(image, processedNV21Image, width, height);
        }
        return processedNV21Image;
    }

    private void stopCameraInternal() {
        ALog.d(TAG, "Stop internal");
        state = SessionState.STOPPED;
        camera.setPreviewCallbackWithBuffer(null);
        // Note: stopPreview or other driver code might deadlock. Deadlock in
        // android.hardware.Camera._stopPreview(Native Method) has been observed on
        // Nexus 5 (hammerhead), OS version LMY48I.
        camera.stopPreview();
        camera.release();
        ALog.d(TAG, "Stop done");
    }

    private void switchCameraInternal(final CameraSwitchHandler switchEventsHandler) {
        ALog.d(TAG, "switchCamera internal");

        int cameraNumber = Camera.getNumberOfCameras();
        if (cameraNumber < 2) {
            if (switchEventsHandler != null) {
                switchEventsHandler.onCameraSwitchError("No camera to switch to.");
            }
            return;
        }

        stopCameraInternal();
        cameraId = (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
        startCaptureInternal();

        ALog.d(TAG, "switchCamera done");
    }

    private void listenForBytebufferFrames() {
        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera callbackCamera) {
                if (state != SessionState.RUNNING) {
                    ALog.d(TAG, "Bytebuffer frame captured but camera is no longer running.");
                    return;
                }
                if (callbackCamera != camera) {
                    ALog.e(TAG, "Callback from a different camera. This should never happen.");
                    return;
                }

                if (data == null) {
                    ALog.e(TAG, "data is null");
                    return;
                }

                int frameRotation = getFrameOrientation();
                byte[] proccessData = processNV21Image(data, captureFormat.width, captureFormat.height, cameraId, frameRotation);
                if (proccessData != null) {
                    final long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    capturerObserver.onByteBufferFrameCaptured(proccessData, captureFormat.width, captureFormat.height, frameRotation, captureTimeNs);
                }
                camera.addCallbackBuffer(data);
            }
        });
    }

    private int getDeviceOrientation() {
        int orientation = 0;

        WindowManager wm = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        switch (wm.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_180:
                orientation = 180;
                break;
            case Surface.ROTATION_270:
                orientation = 270;
                break;
            case Surface.ROTATION_0:
            default:
                orientation = 0;
                break;
        }
        return orientation;
    }

    protected int getFrameOrientation() {
        int rotation = getDeviceOrientation();
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            rotation = 360 - rotation;
        }
        return (info.orientation + rotation) % 360;
    }

    private void nv21FlipHorizontal(byte[] src, byte[] dst, int width, int height) {
        //镜像 最左与最右互换
        int srcPos = 0;
        int dstPos = 0;
        //Y
        for (int i = 0; i < height; ++i) {
            dstPos = srcPos + width - 1;
            for (int j = 0; j < width; ++j) {
                dst[dstPos] = src[srcPos];
                --dstPos;
                ++srcPos;
            }
        }
        //uv
        for (int i = 0; i < height / 2; ++i) {
            dstPos = srcPos + width - 2;
            for (int j = 0; j < width / 2; ++j) {
                dst[dstPos] = src[srcPos];
                dst[dstPos + 1] = src[srcPos + 1];
                dstPos -= 2;
                srcPos += 2;
            }
        }

    }

    private void nv21FlipVertical(byte[] src, byte[] dst, int width, int height) {
        //上下翻转，行交换
        int srcPos = width * (height - 1);
        int dstPos = 0;
        //Y
        for (int i = 0; i < height; ++i) {
            System.arraycopy(src, srcPos, dst, dstPos, width);
            dstPos += width;
            srcPos -= width;
        }
        //uv
        srcPos = width * (height * 3 / 2 - 1);
        for (int i = 0; i < height / 2; ++i) {
            System.arraycopy(src, srcPos, dst, dstPos, width);
            dstPos += width;
            srcPos -= width;
        }
    }

    private void updateCameraParameters(Camera camera,
                                        Camera.Parameters parameters, CaptureFormat captureFormat, Size pictureSize,
                                        boolean captureToTexture) {
        ALog.d(TAG, "preWidth:" + captureFormat.width + " preHeight:" + captureFormat.height + " picWidth:" + pictureSize.width + " picHeight:" + pictureSize.height);
        final List<String> focusModes = parameters.getSupportedFocusModes();

        parameters.setPreviewFpsRange(captureFormat.framerate.min, captureFormat.framerate.max);
        parameters.setPreviewSize(captureFormat.width, captureFormat.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        if (!captureToTexture) {
            parameters.setPreviewFormat(captureFormat.imageFormat);
        }

        if (Build.VERSION.SDK_INT >= 15) {
            if (parameters.isVideoStabilizationSupported()) {
                parameters.setVideoStabilization(true);
            }
        }
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        camera.setParameters(parameters);
    }

    private CaptureFormat findClosestCaptureFormat(
            Camera.Parameters parameters, int width, int height, int framerate) {
        // Find closest supported format for |width| x |height| @ |framerate|.
        final List<CaptureFormat.FramerateRange> supportedFramerates =
                convertFramerates(parameters.getSupportedPreviewFpsRange());

        final CaptureFormat.FramerateRange fpsRange =
                CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);

        final Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize2(
                convertSizes(parameters.getSupportedPreviewSizes()), width, height);

        return new CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    private Size findClosestPictureSize(
            Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(
                convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    // Convert from android.hardware.Camera.Size to Size.
    private List<Size> convertSizes(List<Camera.Size> cameraSizes) {
        final List<Size> sizes = new ArrayList<Size>();
        for (Camera.Size size : cameraSizes) {
            sizes.add(new Size(size.width, size.height));
        }
        return sizes;
    }

    // Convert from int[2] to CaptureFormat.FramerateRange.
    private List<CaptureFormat.FramerateRange> convertFramerates(List<int[]> arrayRanges) {
        final List<CaptureFormat.FramerateRange> ranges = new ArrayList<CaptureFormat.FramerateRange>();
        for (int[] range : arrayRanges) {
            ranges.add(new CaptureFormat.FramerateRange(
                    range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                    range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]));
        }
        return ranges;
    }

    public static enum SessionState {RUNNING, STOPPED}

}
