package com.wyzk.lottery.video.linkmic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wangsu.wsrtc.utils.ALog;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;

/**
 * 继承自{@link WSChatViewManager}类，实现父类定义的连麦窗口添加、删除、位置调整（移动、大小窗切换、新窗口显示位置）等功能；<br/>
 * 该类生成的简单推流界面布局如下：<br/>
 * <pre>
 * --RelativeLayout（全屏根布局）
 *   +--RelativeLayout（全屏根布局内，预览主窗口布局）
 *      +--WSSurfaceView（预览主窗口布局内用于显示的SurfaceView）
 *   +--RelativeLayout（给连麦小窗口预留的布局跟节点）
 * 连麦主播布局如下：
 * --RelativeLayout（全屏根布局）
 *   +--RelativeLayout（全屏根布局内，预览主窗口布局）
 *      +--WSSurfaceView（预览主窗口布局内用于显示的SurfaceView）
 *   +--RelativeLayout（连麦小窗口布局根节点，多观众连麦时会有多个子视图添加到该节点中）
 *      +--RelativeLayout（连麦小窗SurfaceView的嵌套布局）
 *         +--WSSurfaceView（用于播放连麦观众视频的SurfaceView）
 * 普通观众未连麦时布局
 * --RelativeLayout（全屏根布局）
 *   +--RelativeLayout（全屏根布局内，预览主窗口布局，设置GONE）
 *      +--WSSurfaceView
 *   +--RelativeLayout（连麦小窗口布局根节点，多观众连麦时会有多个子视图添加到该节点中）
 *   +--RelativeLayout（嵌套全屏播放视图,此时此窗口遮挡界面中原来的推流窗口）
 *      +--IjkVideoView（播放主播流的视图）
 * 观众连麦时布局
 * --RelativeLayout（全屏根布局）
 *   +--RelativeLayout（全屏根布局内，预览主窗口布局，设置GONE）
 *      +--WSSurfaceView
 *   +--RelativeLayout（连麦小窗口布局根节点，多观众连麦时会有多个子视图添加到该节点中）
 * +--RelativeLayout（连麦小窗SurfaceView的嵌套布局）
 *         +--WSSurfaceView（用于播放主播或其他连麦观众视频的SurfaceView）
 *   +--RelativeLayout（嵌套全屏播放视图,此时此窗口遮挡界面中原来的推流窗口）
 *      +--IjkVideoView（播放主播流的视图）
 * </pre>
 */
public class WSDefaultChatViewManager extends WSChatViewManager {
    private static final String TAG = "WSDefaultChatViewManager";
    /**
     * 小窗的根布局与父节点的间距
     */
    private static final int SMALLVIEW_ROOT_RELATIVELAYOUT_MARGIN = 3; //dp
    /**
     * 小窗的根布局与父节点的间距
     */
    private static final int SMALLVIEW_RELATIVELAYOUT_MARGIN = 2; //dp
    IjkVideoView videoView = null;
    private Map<String, WSSurfaceView> mLoadedSurfaceViews = new ConcurrentHashMap<>();
    /**
     * 全屏根布局
     */
    private RelativeLayout mGlobalRootRelativeLayout = null;
    /**
     * 连麦小窗口布局根节点
     */
    private RelativeLayout mSmallViewRootRelativeLayout = null;
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;
    private DisplayMetrics mDisplayMetrics = null;

    /**
     * 初始化布局，并向全局根布局中添加小窗根布局
     *
     * @param mainViewId 主屏view对应的用户id，一般是当前用户的用户id
     * @param mainView   主屏view
     * @param isPushView
     */
    public WSDefaultChatViewManager(String mainViewId, WSSurfaceView mainView, boolean isPushView) {
        super(mainViewId);
        mMainView = mainView;
        if (mMainView == null) {
            throw new IllegalArgumentException("SurfaceView must not be null");
        }
        View mainViewParent = (View) mMainView.getParent();
        if (mainViewParent == null) {
            throw new IllegalArgumentException("SurfaceView must has a parent");
        }
        mainViewParent.setTag(mainViewId);
        mMainView.setZOrderMediaOverlay(false);
        saveInitedMainViewState(mainView);
        mSurfaceViews.put(mainViewId, mainView);
        mLoadedSurfaceViews.put(mainViewId, mainView);
        try {
            mGlobalRootRelativeLayout = (RelativeLayout) mainViewParent.getParent();
        } catch (Exception e) {
            throw new IllegalArgumentException("SurfaceView's parent's parent must be RelativeLayout!");
        }

        final Context context = mainView.getContext();
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        if (mSmallViewRootRelativeLayout == null) {
            mSmallViewRootRelativeLayout = new RelativeLayout(context);
//            mSurfaceViewRelativeLayout.setBackgroundColor(Color.RED);
            int margin = (int) mDisplayMetrics.density * SMALLVIEW_ROOT_RELATIVELAYOUT_MARGIN;
            final RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParam.setMargins(margin, margin, margin, margin);
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    ALog.i(TAG, "add the small view container ");
                    mGlobalRootRelativeLayout.addView(mSmallViewRootRelativeLayout, rlParam);
                }
            });
        }

        mOnLayoutChangeListener = new View.OnLayoutChangeListener() {//this listhener is belong to view ,no need unregister
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                float mainViewWidth = (float) v.getWidth();
                float mainViewHeight = (float) v.getHeight();
                if (mainViewWidth == 0 || mainViewHeight == 0) {
                    ALog.i(TAG, "current main view width and height is 0, ignore");
                    return;
                }
                if (mMainViewWidth == mainViewWidth && mMainViewHeigth == mainViewHeight) {
                    ALog.i(TAG, "current main view width and height is same as previous, ignore");
                    return;
                }
                mMainViewWidth = (int) mainViewWidth;
                mMainViewHeigth = (int) mainViewHeight;
//                mUiHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ALog.i(TAG, "main view size changed, addjust small view . ");
//                        ViewGroup.LayoutParams layoutParam = mSurfaceViewRelativeLayout.getLayoutParams();
//                        if (layoutParam != null) {
//                            layoutParam.width = mMainViewWidth / 4;
//                            mSurfaceViewRelativeLayout.setLayoutParams(layoutParam);
//                        }
//                    }
//                });
            }
        };

        mGlobalRootRelativeLayout.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    /**
     * 为指定用户加载一个{@link WSSurfaceView}，并将该view显示在界面上
     *
     * @param userId
     * @return
     */
    @Override
    protected WSSurfaceView loadRemoteView(final String userId) {
        WSSurfaceView surfaceView = mLoadedSurfaceViews.get(userId);
        if (surfaceView == null) {
            surfaceView = createView(userId, ViewType.REMOTE);
            mLoadedSurfaceViews.put(userId, surfaceView);
        }
        return surfaceView;
    }

    /**
     * 创建对应类型view，并根据{@link #getAvailableArea(int, int)}返回的位置信息将view放置在指定位置；
     *
     * @param userId
     * @param viewType
     * @return
     */
    protected WSSurfaceView createView(final String userId, ViewType viewType) {
        ALog.i(TAG, "createView : userId : " + userId + ", viewType : " + viewType);
        Context context = mMainView.getContext();
        int margin = (int) mDisplayMetrics.density * SMALLVIEW_RELATIVELAYOUT_MARGIN;
        int smallViewLayoutWidth = mMainViewWidth / 4 + 2 * margin;
        int smallViewLayoutHeight = mMainViewHeigth / 4 + 2 * margin;
        final RelativeLayout.LayoutParams relativelParam = new RelativeLayout.LayoutParams(smallViewLayoutWidth, smallViewLayoutHeight);
        Rect location = getAvailableArea(smallViewLayoutWidth, smallViewLayoutHeight);
        relativelParam.setMargins(location.left + margin, location.top - margin, 0, 0);
        final RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (viewType == ViewType.PUSH) {
            rlParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        rlParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        WSSurfaceView surfaceView = new WSSurfaceView(context);
        surfaceView.setZOrderMediaOverlay(true);
        //surfaceView.setBackgroundColor(Color.parseColor("#33FF0000"));

        relativeLayout.setTag(userId);
        relativeLayout.setOnTouchListener(new SmallViewOnTouchListener());
        relativeLayout.addView(surfaceView, rlParam);//给Surface套一个RelativeLayout

        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                //由于后添加的Surface层级在先添加的Surface下面，为了保证布局视图与Surface的层级保持一致，故每次将SPSurfaceView添加到布局树中时都将布局视图添加到position 0的位置
                mSmallViewRootRelativeLayout.addView(relativeLayout, 0, relativelParam);
            }
        });

        return surfaceView;
    }

    @Override
    protected void unloadRtcView(String userId, final WSSurfaceView view) {
        if (mInitedMainViewId.equals(userId)) {
            return;
        }
        ALog.i(TAG, "unloadRtcView : userId : " + userId);
        final WSSurfaceView surfaceView = mLoadedSurfaceViews.remove(userId);
        if (surfaceView == null)
            return;
        final ViewGroup layout = (ViewGroup) surfaceView.getParent();
        if (layout == null)
            return;
        final ViewGroup layoutParent = (ViewGroup) layout.getParent();
        if (layoutParent == null)
            return;
        if (surfaceView == view) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    ALog.i(TAG, "unloadRtcView ... ");
                    layoutParent.removeView(layout);
                }
            });
        }
    }

    @Override
    public IjkVideoView loadVideoView() {
        ALog.i(TAG, "loadVideoView ... ");
        final CountDownLatch barrier = new CountDownLatch(1);
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                ALog.i(TAG, "2 loadVideoView ... ");
                Context context = mMainView.getContext();

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.BLACK);
                RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                rlParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                videoView = new IjkVideoView(context);
//                videoView.setBackgroundColor(Color.parseColor("#33FF0000"));
                relativeLayout.addView(videoView, rlParam);
                mGlobalRootRelativeLayout.addView(relativeLayout);
                barrier.countDown();
            }
        });

        try {
            barrier.await();
        } catch (InterruptedException ex) {
            ALog.e(TAG, ex.getMessage());
        }
        return videoView;
    }

    @Override
    public void unloadVideoView(IjkVideoView view) {
        final ViewGroup layout = (ViewGroup) view.getParent();
        if (layout == null)
            return;
        final ViewGroup layoutParent = (ViewGroup) layout.getParent();
        if (layoutParent == null)
            return;

        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                ALog.i(TAG, "unloadVideoView ... ");
                layoutParent.removeView(layout);
            }
        });
    }

    /**
     * 返回指定用户对应的WSSurfaceView上一层RelativeLayout视图，用于大小窗切换
     *
     * @param userId
     * @return
     */
    @Override
    protected View getSwitchView(String userId) {
        View surfaceView = super.getSwitchView(userId);
        ALog.i(TAG, "getSwitchView userId:" + userId + ", surfaceView:" + surfaceView);
        if (surfaceView != null) {
            return (View) surfaceView.getParent();
        }
        return null;
    }

    @Override
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
        String mPreMainViewId = mCurrentMainViewId;
        super.switchView(userId1, userId2);
        if (mCurrentMainViewId != null && !mCurrentMainViewId.equals(mPreMainViewId)) {
            WSSurfaceView surfaceView1 = mLoadedSurfaceViews.get(userId1);
            WSSurfaceView surfaceView2 = mLoadedSurfaceViews.get(userId2);
            if (surfaceView1 != null) {
                if (surfaceView1 instanceof WSSurfaceView) {
                    ((WSSurfaceView) surfaceView1).setAutoCameraControl(false);
                }
                View parent = (View) surfaceView1.getParent();
                if (parent != null) {
                    parent.setOnTouchListener(new SmallViewOnTouchListener());
                }
            }
            if (surfaceView2 != null) {
                if (surfaceView2 instanceof WSSurfaceView) {
                    ((WSSurfaceView) surfaceView2).setAutoCameraControl(false);
                }
                View parent = (View) surfaceView2.getParent();
                if (parent != null) {
                    parent.setOnTouchListener(new SmallViewOnTouchListener());
                }
            }
            if (mCurrentMainViewId.equals(userId1)) {
                if (surfaceView1 != null) {
                    if (surfaceView1 instanceof WSSurfaceView) {
                        ((WSSurfaceView) surfaceView1).setAutoCameraControl(true);
                    }
                    View parent = (View) surfaceView1.getParent();
                    if (parent != null) {
                        parent.setOnTouchListener(null);
                    }
                }
            }
            if (mCurrentMainViewId.equals(userId2)) {
                if (surfaceView2 != null) {
                    if (surfaceView2 instanceof WSSurfaceView) {
                        ((WSSurfaceView) surfaceView2).setAutoCameraControl(true);
                    }
                    View parent = (View) surfaceView2.getParent();
                    if (parent != null) {
                        parent.setOnTouchListener(null);
                    }
                }
            }
        }

    }

    /**
     * 未实现
     *
     * @return
     */
    @Deprecated
    private Bitmap takeChatViewShot() {
        Bitmap globalRootRelativeLayoutBmp = getViewDrawingCache(mGlobalRootRelativeLayout);
        if (globalRootRelativeLayoutBmp == null) {
            return null;
        }
        Bitmap bitmapCaptured = Bitmap.createBitmap(mGlobalRootRelativeLayout.getWidth(), mGlobalRootRelativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCaptured);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(globalRootRelativeLayoutBmp
                , null
                , new Rect(0, 0, mGlobalRootRelativeLayout.getWidth(), mGlobalRootRelativeLayout.getHeight())
                , paint);//draw layout
        globalRootRelativeLayoutBmp.recycle();
        int[] globalRootLocation = new int[2];
        mGlobalRootRelativeLayout.getLocationOnScreen(globalRootLocation);//相对屏幕坐标，0是left，1是top
        WSSurfaceView mainSurface = mLoadedSurfaceViews.get(mCurrentMainViewId);
        if (mainSurface != null) {
            Bitmap mainSurfaceBmp = null;
//            if (mainSurface instanceof SPSurfaceView) {
//                ((SPSurfaceView)mainSurface).takeSurfaceShot();
//            }
            if (mainSurfaceBmp != null) {
                int[] mainSurfaceViewLocation = new int[2];
                mainSurface.getLocationOnScreen(mainSurfaceViewLocation);
                int positionX = mainSurfaceViewLocation[0] - globalRootLocation[0];
                int positionY = mainSurfaceViewLocation[1] - globalRootLocation[1];
                canvas.drawBitmap(mainSurfaceBmp
                        , null
                        , new Rect(positionX, positionY, positionX + mainSurface.getWidth(), positionY + mainSurface.getHeight())
                        , paint);//draw surface
                mainSurfaceBmp.recycle();
            }
        }
        //small root view is transparent, no need to draw
        for (int i = 0; i < mSmallViewRootRelativeLayout.getChildCount(); i++) {
            ViewGroup smallBgView = (ViewGroup) mSmallViewRootRelativeLayout.getChildAt(i);
            if (smallBgView == null) {
                continue;
            }
            Bitmap smallViewBgBmp = getViewDrawingCache(smallBgView);
            if (smallViewBgBmp != null) {
                int[] smallViewLocation = new int[2];
                smallBgView.getLocationOnScreen(smallViewLocation);
                int positionX = smallViewLocation[0] - globalRootLocation[0];
                int positionY = smallViewLocation[1] - globalRootLocation[1];
                canvas.drawBitmap(smallViewBgBmp
                        , null
                        , new Rect(positionX, positionY, positionX + smallBgView.getWidth(), positionY + smallBgView.getHeight())
                        , paint);//draw small view bg
                smallViewBgBmp.recycle();
            }
            WSSurfaceView surfaceView = (WSSurfaceView) smallBgView.getChildAt(0);//0 is SurfaceView
            Bitmap surfaceBmp = null;
//            if (surfaceView instanceof SPSurfaceView) {
//                ((SPSurfaceView)surfaceView).takeSurfaceShot();
//            }
            if (surfaceBmp != null) {
                int[] surfaceViewLocation = new int[2];
                surfaceView.getLocationOnScreen(surfaceViewLocation);
                int positionX = surfaceViewLocation[0] - globalRootLocation[0];
                int positionY = surfaceViewLocation[1] - globalRootLocation[1];
                canvas.drawBitmap(surfaceBmp
                        , null
                        , new Rect(positionX, positionY, positionX + surfaceView.getWidth(), positionY + surfaceView.getHeight())
                        , paint);//draw small surface bg
                surfaceBmp.recycle();
            }
        }
        return bitmapCaptured;
    }

    private Bitmap getViewDrawingCache(View view) {
        if (view == null) {
            return null;
        }
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        if (viewWidth == 0 && viewHeight == 0) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        if (bitmap == null) {
            ALog.e(TAG, "getViewDrawingCache failed, cached null");
            return null;
        }
        bitmap = bitmap.copy(bitmap.getConfig(), true);//copy this bitmap before destroyDrawingCache() method recycle it
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 获取新增小窗的可用位置，改方法默认实现的小窗排布顺序为左下->左上->右下->右上<br/>
     * 实现思路：<br/>
     * 1.开始时可用区域为整个屏幕，即（0, 0, mRootRelativeLayoutWidth, mRootRelativeLayoutHeigth）<br/>
     * 2.如果界面中添加了小窗，则小窗会将屏幕可用区域切割成上下左右四个部分，将比请求的宽高小的区域从可用区域中剔除，
     * 将符合条件的区域按下、左、右、上的顺序添加到可用区域列表中；<br/>
     * 3.重复2过程，知道从可用区域中选择出可用位置；
     * 4.最后从符合条件的列表中挑选一个最靠下，最靠左的区域，用于放置新小窗的位置；<br/>
     * <br/>
     * 修改小船放置位置思路：<br/>
     * 1.可用区域可能大于目标窗口的大小，此时需要控制小窗要显示在此区域的具体位置（如左下角）<br/>
     * 2.最终选择的区域位置跟可用区域的排列顺序和选择规则均有关系，两者需要配合调整才能达到预期效果
     *
     * @param requestWidth  请求区域的宽度
     * @param requestHeight 请求区域的高度
     * @return 可用区域
     */
    private Rect getAvailableArea(int requestWidth, int requestHeight) {
        int mRootRelativeLayoutWidth = mSmallViewRootRelativeLayout.getWidth();
        int mRootRelativeLayoutHeigth = mSmallViewRootRelativeLayout.getHeight();
        // 默认左下角
        Rect defaultRect = new Rect(0, mRootRelativeLayoutHeigth - requestHeight, requestWidth, mRootRelativeLayoutHeigth);
        LinkedList<Rect> availableAreas = new LinkedList<>();
        availableAreas.add(new Rect(0, 0, mRootRelativeLayoutWidth, mRootRelativeLayoutHeigth));
        LinkedList<Rect> splitedAreas = new LinkedList<>();
        Set<Entry<String, WSSurfaceView>> mapEntrySet = mLoadedSurfaceViews.entrySet();
        Rect surfaceRect = new Rect();
        for (Entry<String, WSSurfaceView> setEntry : mapEntrySet) {
            String userId = setEntry.getKey();
            if (isCurrentMainViewId(userId)) {
                continue;
            }
            waitForSurfaceCreated(setEntry.getValue());
            View surfaceContainer = (View) setEntry.getValue().getParent();
            surfaceRect.left = surfaceContainer.getLeft();
            surfaceRect.top = surfaceContainer.getTop();
            surfaceRect.right = surfaceContainer.getRight();
            surfaceRect.bottom = surfaceContainer.getBottom();
            if (surfaceRect.left == surfaceRect.right || surfaceRect.top == surfaceRect.bottom) {
                continue;
            }
            for (Rect area : availableAreas) {
                if (!checkPointInRect(surfaceRect.left, surfaceRect.top, area)
                        && !checkPointInRect(surfaceRect.right, surfaceRect.top, area)
                        && !checkPointInRect(surfaceRect.left, surfaceRect.bottom, area)
                        && !checkPointInRect(surfaceRect.right, surfaceRect.bottom, area)) {
                    splitedAreas.add(area);
                    continue;
                }
                if (area.bottom - surfaceRect.bottom >= requestHeight) {//下
                    splitedAreas.add(new Rect(area.left, surfaceRect.bottom, area.right, area.bottom));
                }
                if (surfaceRect.left - area.left >= requestWidth) {//左
                    splitedAreas.add(new Rect(area.left, area.top, surfaceRect.left, area.bottom));
                }
                if (area.right - surfaceRect.right >= requestWidth) {//右
                    splitedAreas.add(new Rect(surfaceRect.right, area.top, area.right, area.bottom));
                }
                if (surfaceRect.top - area.top >= requestHeight) {//上
                    splitedAreas.add(new Rect(area.left, area.top, area.right, surfaceRect.top));
                }
            }
            LinkedList<Rect> temp = availableAreas;
            availableAreas = splitedAreas;
            splitedAreas = temp;
            splitedAreas.clear();
        }
        Rect result = null;
        for (Rect area : availableAreas) {//从可用区域挑选出最靠下，最靠左的
            if (result == null) {
                result = area;
                continue;
            }
            if (area.bottom > result.bottom) {
                result.bottom = area.bottom;
                result.left = area.left;
                continue;
            }
            if (area.left < result.left) {
                result.bottom = area.bottom;
                result.left = area.left;
                continue;
            }
        }
        if (result != null) {
            result.right = result.left + requestWidth;
            result.top = result.bottom - requestHeight;
        } else {
            result = defaultRect;
        }

        return result;
    }

    /**
     * 等待SurfaceView视图被添加到视图树中
     *
     * @param view
     */
    private void waitForSurfaceCreated(WSSurfaceView view) {
        // 不阻塞主线程
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return;
        }
        // 快速添加窗口时，可能出现系统未给窗口分配位置信息
        View container = (View) view.getParent();
        for (int i = 0; i < 10; i++) {
            if (container == null || container.getLeft() == container.getRight()
                    || container.getTop() == container.getBottom()) {
                ALog.i(TAG, "waitForSurfaceCreated " + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                continue;
            }
            break;
        }
    }

    private boolean checkPointInRect(int x, int y, Rect rect) {
        return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
    }

    public enum ViewType {
        PUSH,
        PLAY,
        REMOTE,
    }

    /**
     * 给小窗添加OnTouch事件，用于大小窗切换和窗口自由移动
     */
    private class SmallViewOnTouchListener implements View.OnTouchListener {

        int lastX, lastY;
        int touchParamLeftMargin, touchParamTopMargin;
        private GestureDetector mGestureDetector = null;
        private RelativeLayout mTouchedView = null;
        private RelativeLayout.LayoutParams mLayoutParams = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v instanceof RelativeLayout) {
                mTouchedView = (RelativeLayout) v;
            } else {
                throw new IllegalArgumentException("the touched view must be RelativeLayout");
            }
            if (mTouchedView.getParent() instanceof RelativeLayout) {
                mLayoutParams = (RelativeLayout.LayoutParams) mTouchedView.getLayoutParams();
            } else {
                throw new IllegalArgumentException("the RelativeLayout must be included in a RelativeLayout");
            }
            if (mGestureDetector == null) {
                initGestureDetector(mTouchedView.getContext());
            }
            return mGestureDetector.onTouchEvent(event);
        }

        public void initGestureDetector(Context context) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent event) {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    touchParamLeftMargin = mLayoutParams.leftMargin;
                    touchParamTopMargin = mLayoutParams.topMargin;
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    //TODO switchView
                    String tag = (String) mTouchedView.getTag();
                    if (tag != null) {
                        WSSurfaceView preSurface = mLoadedSurfaceViews.get(mCurrentMainViewId);
                        if (preSurface == null) {
                            ALog.e(TAG, "can't find current main view ,ignore switchView action");
                            return true;
                        }
                        ((View) preSurface.getParent()).removeOnLayoutChangeListener(mOnLayoutChangeListener);
                        switchView(tag, mCurrentMainViewId);
                        WSSurfaceView folSurface = mLoadedSurfaceViews.get(mCurrentMainViewId);
                        if (folSurface == null) {
                            ALog.e(TAG, "can't find main view after switch ,ignore addOnLayoutChangeListener action");
                            return true;
                        }
                        ((View) folSurface.getParent()).addOnLayoutChangeListener(mOnLayoutChangeListener);
                    }
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    int dx = (int) e2.getRawX() - lastX;
                    int dy = (int) e2.getRawY() - lastY;
                    int newLeftMargin = touchParamLeftMargin + dx;
                    int newTopMargin = touchParamTopMargin + dy;
                    View parent = (View) mTouchedView.getParent();
                    if (newLeftMargin < 0) {
                        newLeftMargin = 0;
                    }
                    if (newLeftMargin > (parent.getWidth() - mLayoutParams.width)) {
                        newLeftMargin = parent.getWidth() - mLayoutParams.width;
                    }
                    if (newTopMargin < 0) {
                        newTopMargin = 0;
                    }
                    if (newTopMargin > (parent.getHeight() - mLayoutParams.height)) {
                        newTopMargin = parent.getHeight() - mLayoutParams.height;
                    }
                    mLayoutParams.leftMargin = newLeftMargin;
                    mLayoutParams.topMargin = newTopMargin;
                    // 更新小窗位置
                    mTouchedView.setLayoutParams(mLayoutParams);
                    return true;
                }
            });

        }

    }


}
