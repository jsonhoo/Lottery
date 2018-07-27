package com.wyzk.lottery.video.linkmic;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wangsu.wsrtc.utils.ALog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;

/**
 * 被{@link LinkMicManager}类使用，用于控制连麦窗口的添加、删除、位置调整（移动、大小窗切换、新窗口显示位置）等功能；<br/>
 * 该类只是定义了统一的接口，具体添加、删除、移动等功能有子类实现。<br/>
 * 该类会将所创建的{@link WSSurfaceView}存放在{@link #mSurfaceViews}集合中,<br/>
 * 其中，key为用户id，value为视图所对应的{@link WSSurfaceView}。<br/>
 * 用户加入连麦后，通过{@link #getRemoteView(String)}方法为指定id的用户分配一个用于预览或推流的{@link WSSurfaceView};<br/>
 * 用户推流连麦后，通过{@link #restoreRtcView(String)}方法将指定id的用户{@link WSSurfaceView}从界面中删除；<br/>
 * 通过{@link #loadVideoView()}为观众分配一个用于播放主播视频流的{@link IjkVideoView};<br/>
 * 通过{@link #unloadVideoView(IjkVideoView)}将{@link IjkVideoView}从界面中移除；<br/>
 * 通过{@link #switchView(String, String)}将两个view大小和位置进行切换；<br/>
 * 通过{@link #resetRtcViewLayout()}将view的大小和位置恢复到默认；<br/>
 */
public abstract class WSChatViewManager {
    private static final String TAG = "ChatViewManager";
    /**
     * 记录初始化时主屏view的id，用于{@link #resetRtcViewLayout()}时将view大小和位置恢复到默认
     */
    protected final String mInitedMainViewId;
    /**
     * 存放所有已创建{@link WSSurfaceView}的集合
     */
    protected Map<String, WSSurfaceView> mSurfaceViews = new ConcurrentHashMap<String, WSSurfaceView>();
    protected Handler mUiHandler = new Handler(Looper.getMainLooper());
    /**
     * 记录当前主屏view的id
     */
    protected String mCurrentMainViewId = null;
    protected WSSurfaceView mMainView = null;
    protected int mMainViewWidth = 0;
    protected int mMainViewHeigth = 0;
    protected ViewGroup mInitedMainViewParent = null;
    private LayoutParams mInitedMainViewLayoutParams = null;
    private int mInitedMainViewIndex = -1;
    private boolean mInitedMainViewAutoCameraControl = true;

    /**
     * @param mainViewId 主屏view对应的用户id，一般是当前用户的用户id
     */
    public WSChatViewManager(String mainViewId) {
        mInitedMainViewId = mainViewId;
        mCurrentMainViewId = mainViewId;
        if (mInitedMainViewId == null) {
            throw new IllegalArgumentException("the mainViewId must not be null");
        }
    }

    /**
     * 为指定id的用户创建一个{@link WSSurfaceView},并将改view添加到界面中显示
     *
     * @param userId 需要创建view的用户id
     * @return 创建好的view
     */
    public WSSurfaceView getRemoteView(String userId) {
        ALog.i(TAG, "getRemoteView : " + userId);
        WSSurfaceView spSurfaceView = mSurfaceViews.get(userId);
        if (spSurfaceView != null) return spSurfaceView;
        spSurfaceView = loadRemoteView(userId);
        mSurfaceViews.put(userId, spSurfaceView);
        return spSurfaceView;
    }

    /**
     * 将指定id的用户对应的{@link WSSurfaceView}从界面中移除
     *
     * @param userId 需要移除view的用户id
     */
    public void restoreRtcView(String userId) {
        ALog.i(TAG, "restoreRtcView : " + userId);
        WSSurfaceView spSurfaceView = mSurfaceViews.remove(userId);
        if (spSurfaceView != null) {
            unloadRtcView(userId, spSurfaceView);
            if (isCurrentMainViewId(userId)) {
                restoreInitedMainViewState(mSurfaceViews.get(mInitedMainViewId));
            }
        }
    }

    /**
     * 判断当前用户id对应的view是否在主屏中显示
     *
     * @param userId
     * @return
     */
    public boolean isCurrentMainViewId(String userId) {
        if (mCurrentMainViewId == null) return false;
        return mCurrentMainViewId.equals(userId);
    }

    public boolean isInitedMainView(String userId) {
        return mInitedMainViewId.equals(userId);
    }

    public boolean isViewCreated(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        return mSurfaceViews.get(userId) != null;
    }

    /**
     * 子类实现，为指定用户加载一个{@link WSSurfaceView}
     *
     * @param userId
     * @return
     */
    abstract protected WSSurfaceView loadRemoteView(final String userId);

    /**
     * 子类实现，将指定用户对应的{@link WSSurfaceView}从界面中移除
     *
     * @param userId   用于内部查找记录的{@link WSSurfaceView}
     * @param pullView
     */
    abstract protected void unloadRtcView(String userId, final WSSurfaceView pullView);

    /**
     * 为观众分配一个用于播放主播视频流的{@link IjkVideoView};<br/>
     *
     * @return
     */
    abstract protected IjkVideoView loadVideoView();

    /**
     * 将{@link IjkVideoView}从界面中移除；
     *
     * @param view
     */
    abstract protected void unloadVideoView(IjkVideoView view);

    /**
     * 将指定用户id的两个view大小和位置进行切换，本质上是将两个view对应的父节点和布局参数进行切换；
     *
     * @param userId1
     * @param userId2
     */
    public void switchView(final String userId1, final String userId2) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    switchView(userId1, userId2);
                }
            });
            return;
        }
        ALog.i(TAG, "switchView : (" + userId1 + "," + userId2 + ")");
        if (userId1 == null || userId2 == null) {
            Log.e(TAG, "switch view failed,due to userId is null");
            return;
        }
        if (userId1.equals(userId2)) {
            Log.e(TAG, "switch view failed,due to duplicate userId");
            return;
        }

        View switchView1 = getSwitchView(userId1);
        View switchView2 = getSwitchView(userId2);
        if (switchView1 == null || switchView2 == null) {
            ALog.e(TAG, "switch view failed,due to not find the cerrect view " + switchView1 + ", " + switchView2);
            return;
        }
        WSSurfaceView switchSurfaceView1 = null;
        WSSurfaceView switchSurfaceView2 = null;
        if (switchView1 instanceof WSSurfaceView) {
            switchSurfaceView1 = (WSSurfaceView) switchView1;
        } else {
            switchSurfaceView1 = mSurfaceViews.get(userId1);
        }
        if (switchView2 instanceof WSSurfaceView) {
            switchSurfaceView2 = (WSSurfaceView) switchView2;
        } else {
            switchSurfaceView2 = mSurfaceViews.get(userId2);
        }

        if (switchSurfaceView1 == null || switchSurfaceView2 == null) {
            ALog.e(TAG, "switch view failed,due to not find the cerrect SurfaceView view1: " + switchSurfaceView1
                    + ", view2: " + switchSurfaceView2);
            return;
        }

        ViewGroup switchView1LayoutParent = (ViewGroup) switchView1.getParent();
        ViewGroup switchView2LayoutParent = (ViewGroup) switchView2.getParent();
        int switchView1Index = switchView1LayoutParent.indexOfChild(switchView1);
        int switchView2Index = switchView2LayoutParent.indexOfChild(switchView2);
        if (switchView1Index == -1 || switchView2Index == -1) {
            ALog.i(TAG, "not found the switch view in parent view group");
            return;
        }
        LayoutParams switchView1LayoutParams = switchView1.getLayoutParams();
        LayoutParams switchView2LayoutParams = switchView2.getLayoutParams();

        switchSurfaceView1.setZOrderMediaOverlay(true);
        switchSurfaceView2.setZOrderMediaOverlay(true);
        if (mCurrentMainViewId != null && mCurrentMainViewId.equals(userId1)) {
            switchSurfaceView2.setZOrderMediaOverlay(false);
            mCurrentMainViewId = userId2;
        } else if (mCurrentMainViewId != null && mCurrentMainViewId.equals(userId2)) {
            switchSurfaceView1.setZOrderMediaOverlay(false);
            mCurrentMainViewId = userId1;
        }
        switchView1LayoutParent.removeView(switchView1);
        switchView2LayoutParent.removeView(switchView2);
        //由于后添加的Surface层级在先添加的Surface下面，为了保证布局视图与Surface的层级保持一致，故每次将SPSurfaceView添加到布局树中时都将布局视图添加到position 0的位置
        switchView1LayoutParent.addView(switchView2, 0, switchView1LayoutParams);
        switchView2LayoutParent.addView(switchView1, 0, switchView2LayoutParams);
        ALog.i(TAG, "switchView : (" + userId1 + "," + userId2 + ") finish");
    }

    /**
     * 获取用户大小窗切换的view，子类复写该方法可控制{@link #switchView(String, String)}切换的布局节点;
     *
     * @param userId
     * @return
     */
    protected View getSwitchView(String userId) {
        return mSurfaceViews.get(userId);
    }

    /**
     * 恢复到默认布局
     */
    public void resetRtcViewLayout() {
        ALog.i(TAG, "resetRtcViewLayout ...");
        if (Looper.getMainLooper() != Looper.myLooper()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    resetRtcViewLayout();
                }
            });
            return;
        }

        for (String playerKey : mSurfaceViews.keySet()) {
            if (!mInitedMainViewId.equals(playerKey)) {
                restoreRtcView(playerKey);
            }
        }
        restoreInitedMainViewState(mSurfaceViews.get(mInitedMainViewId));
    }

    /**
     * 保存主窗口信息，用于主窗口布局位置等信息的恢复
     *
     * @param surfaceView
     */
    protected void saveInitedMainViewState(final WSSurfaceView surfaceView) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    saveInitedMainViewState(surfaceView);
                }
            });
            return;
        }

        View switchView = getSwitchView(mInitedMainViewId);
        if (switchView == null) return;
        mInitedMainViewParent = (ViewGroup) switchView.getParent();
        mInitedMainViewLayoutParams = switchView.getLayoutParams();
        mInitedMainViewIndex = mInitedMainViewParent.indexOfChild(switchView);
//        if (surfaceView != null && surfaceView instanceof SPSurfaceView) {
//            mInitedMainViewAutoCameraControl = ((WSSurfaceView) surfaceView).isAutoCameraControl();
//        }
    }

    /**
     * 恢复{@link #saveInitedMainViewState(WSSurfaceView)}方法保存的信息;
     *
     * @param surfaceView
     */
    protected void restoreInitedMainViewState(final WSSurfaceView surfaceView) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    restoreInitedMainViewState(surfaceView);
                }
            });
            return;
        }
        if (mInitedMainViewId.equals(mCurrentMainViewId)) {
            ALog.i(TAG, "restoreInitedMainViewState success, no need restore");
            return;
        }
        if (surfaceView == null) {
            ALog.e(TAG, "restoreInitedMainViewState failed, surfaceView is null");
            return;
        }

        if (mInitedMainViewParent == null || mInitedMainViewLayoutParams == null || mInitedMainViewIndex < 0) {
            ALog.e(TAG, "restoreInitedMainViewState error,lack some params");
        }
        View switchView = getSwitchView(mInitedMainViewId);
        if (switchView == null) return;
        ALog.i(TAG, "restoreInitedMainViewState ");
        ViewGroup parent = (ViewGroup) switchView.getParent();
        parent.removeView(switchView);
        switchView.setOnTouchListener(null);
        surfaceView.setZOrderMediaOverlay(false);
        mInitedMainViewParent.addView(switchView, mInitedMainViewIndex, mInitedMainViewLayoutParams);
        mCurrentMainViewId = mInitedMainViewId;
//        if (surfaceView instanceof SPSurfaceView) {
//            ((SPSurfaceView) surfaceView).setAutoCameraControl(mInitedMainViewAutoCameraControl);
//        }
    }
}
