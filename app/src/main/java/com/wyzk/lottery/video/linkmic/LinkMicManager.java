package com.wyzk.lottery.video.linkmic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.wangsu.wsrtc.chat.MixControlListener;
import com.wangsu.wsrtc.chat.Participant;
import com.wangsu.wsrtc.media.MediaClientManager;
import com.wangsu.wsrtc.media.MediaConstants;
import com.wangsu.wsrtc.media.MediaListener;
import com.wangsu.wsrtc.media.PullPCClient;
import com.wangsu.wsrtc.media.PushPCClient;
import com.wangsu.wsrtc.media.audio.AppRTCUtils;
import com.wangsu.wsrtc.media.drawer.WSDrawer;
import com.wangsu.wsrtc.room.LocalRoom;
import com.wangsu.wsrtc.room.LocalRoomOwner;
import com.wangsu.wsrtc.room.Participator;
import com.wangsu.wsrtc.sdk.WSChatConfig;
import com.wangsu.wsrtc.sdk.WSChatConstants;
import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wangsu.wsrtc.sdk.WSVideoFilter;
import com.wangsu.wsrtc.signal.SignalConstants;
import com.wangsu.wsrtc.signal.SignalListener;
import com.wangsu.wsrtc.signal.SignalManager;
import com.wangsu.wsrtc.signal.SignalManager.ChatStateListener;
import com.wangsu.wsrtc.utils.ALog;
import com.wangsu.wsrtc.utils.CameraUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.VideoCapturer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.example.widget.media.IRenderView;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;

import static com.wangsu.wsrtc.signal.SignalConstants.STATUS_ROOM_NOT_EXISTING;

public class LinkMicManager implements Callback {
    private static final String TAG = "LinkMicManager";

    private static final int SIGNAL_SERVER_MSG = 0X11111;
    private static final int MEDIA_SERVER_MSG = 0X11112;
    private static final Object mHandlerLock = new Object();
    private static LinkMicManager mInstance;
    private Context mAppContext;
    private WSChatConfig mChatConfig;
    private boolean mIsAnchor;
    private SignalManager mSignalManager;
    private MediaClientManager mMediaClientManager;
    private LocalRoom mLocalRoom;
    private WSChatViewManager mChatViewManager;
    private WSDrawer mDrawer;
    private LocalRoomOwner mLocalRoomOwner;
    private HandlerThread mHandlerThread;
    private Handler mChatHandler;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private LinkMicListener mLinkMicListener;
    private ChatStateListener mChatStateListener;
    private SignalManager.AnchorChatStateListener mAnchorChatStateListener;
    private PeerConnectionFactory.PlayingFileCallback mPlayingFileCallback;

    private IjkVideoView mIjkVideoView;
    private String mOtherRoomId;
    private String mAnchorPushUrl;
    private String mOtherAnchorId;
    private String mPeerName;
    private Map<String, String> mPeerNames;
    private VideoCapturer mCustomVideoCapturer = null;

    private LinkMicManager() {
    }

    public static LinkMicManager getInstance() {
        if (mInstance == null) {
            synchronized (LinkMicManager.class) {
                if (mInstance == null) {
                    mInstance = new LinkMicManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, boolean isAuchor, WSChatConfig chatConfig) {
        synchronized (mHandlerLock) {
            if (context == null) {
                throw new IllegalArgumentException("context must be not null.");
            } else {
                if (mAppContext == context.getApplicationContext()) {
                    throw new RuntimeException("PushPCClient had initialized.");
                }
            }

            if (chatConfig == null) {
                throw new IllegalArgumentException("WSChatConfig must be not null");
            }
            if (chatConfig.captureView == null) {
                throw new IllegalArgumentException("WSChatConfig.captureView must be not null");
            }
//            if (TextUtils.isEmpty(chatConfig.slsUrl)) {
//                throw new IllegalArgumentException("WSChatConfig.slsUrl must be not empty");
//            }
            if (TextUtils.isEmpty(chatConfig.userId) || TextUtils.isEmpty(chatConfig.roomID)) {
                throw new IllegalArgumentException("user and roomId must be not empty");
            }

            ALog.init(context);

            mAppContext = context.getApplicationContext();
            mIsAnchor = isAuchor;
            mChatConfig = chatConfig;
            mPeerName = mChatConfig.userHostUrl + "/" + mChatConfig.appId + "_" + mChatConfig.roomID + "/" + mChatConfig.userId;
            mPeerNames = new HashMap<>();
            if (mChatConfig.isCustomVideoSource) {
                mCustomVideoCapturer = new FaceuVideoCapture(context.getApplicationContext(), mChatConfig.captureView);
                ((CustomVideoCapture) mCustomVideoCapturer).setCameraId(mChatConfig.cameraId);
            } else {
                mCustomVideoCapturer = null;
            }
            mHandlerThread = new HandlerThread("WSChatManager_thread");
            mHandlerThread.start();
            mChatHandler = new Handler(mHandlerThread.getLooper(), this);

            runOnChatManagerThread(new Runnable() {
                @Override
                public void run() {
                    initInternal();
                }
            });
        }
        ALog.d(TAG, "init...done");
    }

    public void startPush() {
        startPushInternal();
    }

    public void stopPush() {
        checkIsAnchor();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                stopPushInternal();
            }
        });
    }

    public void setPushRtmpUrl(String url) {
        mSignalManager.setPushRtmpUrl(url);
    }

    public void resume() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                ALog.i(TAG, "resume ...");
                mLocalRoom.resume();
            }
        });
    }

    public void pause() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                ALog.i(TAG, "pause ...");
                mLocalRoom.pause();
            }
        });
    }

    public void startLinkMic() {
        checkIsAudience();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                startLinkMicInternal();
            }
        });
    }


    public void startAnchorLinkMic(Bundle bundle) {
        checkIsAnchor();
        mOtherAnchorId = bundle.getString(SignalConstants.KEY_ANCHOR_ID);
        mOtherRoomId = bundle.getString(SignalConstants.KEY_ROOM_ID);
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mSignalManager.kickAll(SignalConstants.FLAG_LINK_MIC_ANCHOR_NOT_ROOMOWNER);
            }
        });
    }

    public void cancelLinkMic() {
        checkIsAudience();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mSignalManager.cancelLinkMic();
            }
        });
    }

    public void cancelAnchorLinkMic() {
        checkIsAnchor();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mSignalManager.cancelLinkMic();
            }
        });
    }

    public void stopLinkMic() {
        checkIsAudience();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                stopLinkMicInternal();
            }
        });
    }

    public void stopAnchorLinkMic() {
        checkIsAnchor();
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                stopAnchorLinkMicInternal();
            }
        });
    }

    public void kickAudience(final String audienceId) {
        checkIsAnchor();
        if (audienceId == null)
            throw new IllegalArgumentException("No AudienceId info.");
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                kickAudienceInternal(audienceId);
            }
        });

    }

    public void replyChatRequest(Bundle bundle) {
        checkIsAnchor();
        final String audienceId = bundle.getString("AudienceId");
        final boolean isAccept = bundle.getBoolean("IsAccept");
        final String pushUrl = bundle.getString("AnchorPushUrl");
        if (audienceId == null)
            throw new IllegalArgumentException("No audienceId info.");
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                replyChatRequestInternal(audienceId, isAccept, pushUrl);
            }
        });
    }

    public boolean refreshChatingList() {
        return mSignalManager.refreshChatingList();
    }

    public boolean refreshWaitingList() {
        return mSignalManager.refreshWaitingList();
    }

    public void startBgm(final String fileName, final float volumeScaling) {
        mPlayingFileCallback = new PeerConnectionFactory.PlayingFileCallback() {
            @Override
            public void onPlayingFileNotification(String info) {
                ALog.i(TAG, "onPlayingFileNotification " + info);
                if (mLinkMicListener != null) {
                    mLinkMicListener.bgmStop();
                }
            }
        };
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.startBgm(fileName, volumeScaling, mPlayingFileCallback);
            }
        });
    }

    public void stopBgm() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.stopBgm();
            }
        });
    }

    public void switchCamera() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.switchCamera();
            }
        });
    }

    public void switchCameraFlashMode() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.switchCameraFlashMode();
            }
        });
    }

    public void switchCameraFocusMode() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.switchCameraFocusMode();
            }
        });
    }

    public void muteMic(final boolean isMute) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.muteMic(isMute);
            }
        });
    }

    public void muteVideo(final boolean isMute) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mLocalRoomOwner.muteVideo(isMute);
            }
        });
    }

    public void release() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                releaseInternal();
            }
        });
    }

    public void registerLinkMicListener(LinkMicListener listener) {
        mLinkMicListener = listener;
    }

    public void unregisterChatListener() {
        mLinkMicListener = null;
    }

    public void registerChatStateChangeListener(ChatStateListener listener) {
        mChatStateListener = listener;
    }

    public void registerAnchorChatStateChangeListener(SignalManager.AnchorChatStateListener listener) {
        mAnchorChatStateListener = listener;
    }

    public WSChatViewManager getChatViewManager() {
        return mChatViewManager;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ALog.i(TAG, "handleMessage statusMsg  what:" + msg.what + " , mHandlerThread :" + mHandlerThread.hashCode());
        if (msg.arg1 == SIGNAL_SERVER_MSG) {
            return handleSignalMessage(msg);
        } else if (msg.arg1 == MEDIA_SERVER_MSG) {
            return handleMediaMessage(msg);
        }
        return true;
    }

    private void initInternal() {
        synchronized (mHandlerLock) {
            ALog.d(TAG, "initInternal." + AppRTCUtils.getThreadInfo());
            initSignalManager();

            initMediaManager();

            initChatViewManager();

            initRoomManager();

            //初始化本地房间拥有者
            //初始化WSSurfaceView,设置显示在屏幕上的缩放类型
            mChatConfig.captureView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            PushPCClient client = mMediaClientManager.createPushClient(mCustomVideoCapturer, mChatConfig.captureView);
            String userId = mIsAnchor ? mChatConfig.anchorId : mChatConfig.userId;
            mLocalRoomOwner = new LocalRoomOwner(mIsAnchor, userId, client);
            //加入本地房间
            mLocalRoom.addRoomOwner(mLocalRoomOwner);
        }
        startPlayInternal();
    }

    private void initRoomManager() {
        mLocalRoom = new LocalRoom(mChatConfig.roomID);
    }

    private void initChatViewManager() {
        String userId = mIsAnchor ? mChatConfig.anchorId : mChatConfig.userId;
        mChatViewManager = new WSDefaultChatViewManager(userId, mChatConfig.captureView, mIsAnchor);
    }

    private void initMediaManager() {
        boolean videoCodecHwAcceleration = mChatConfig.preferEncodeMode == WSChatConstants.ENCODER_MODE_HARD;

        mDrawer = new WSDrawer(mAppContext, videoCodecHwAcceleration, mChatConfig.videoResolution.getWidth(), mChatConfig.videoResolution.getHeight());
        mMediaClientManager = MediaClientManager.getInstance();
        mMediaClientManager.init(mAppContext, mChatConfig, mDrawer, mChatConfig.preferEncodeMode == WSChatConstants.ENCODER_MODE_HARD);

        //listener
        MediaListener listener = new MediaListener() {
            @Override
            public void onMediaEvent(int eventCode, String description) {
                Message msg = Message.obtain();
                msg.what = eventCode;
                msg.arg1 = MEDIA_SERVER_MSG;
                mChatHandler.sendMessage(msg);
            }

            @Override
            public void onMediaEventLog(String log) {
                if (mLinkMicListener != null) {
                    mLinkMicListener.statusInfo(log, null);
                }
            }

            @Override
            public void onMediaStatusLog(Bundle status) {
                if (mLinkMicListener != null) {
                    mLinkMicListener.statusInfo(null, status);
                }
            }

            @Override
            public void onSwitchCameraDone(boolean isSuccess, boolean isFrontCamera, String description) {
                if (mLinkMicListener != null) {
                    mLinkMicListener.onSwitchCameraDone(isSuccess, isFrontCamera, description);
                }
            }

            @Override
            public void onMediaConnectionChange(final String userId, final MediaConstants.MediaConnectionState newState) {
                runOnChatManagerThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newState == MediaConstants.MediaConnectionState.FAILED) {
                            //自动重连
                            if (userId.equals(mChatConfig.userId)) {
                                mLocalRoomOwner.stopPush();
                                mLocalRoomOwner.startPush();
                            } else {
                                Participator participator = mLocalRoom.getParticipator(userId);
                                if (participator != null) {
                                    participator.stopPull();
                                    participator.startPull();
                                }
                            }
                        }
                    }
                });
            }
        };
        mMediaClientManager.setListener(listener);

        if (mIsAnchor) {//主播才需要监测合流状态
            MixControlListener mixControlListener = new MixControlListener() {

                @Override
                public void onError(String action) {
                    retry();
                }

                @Override
                public void onStatus(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray peers = jsonObject.optJSONArray("peers");
                        if (peers != null && peers.length() != mPeerNames.size()) {
                            ALog.e(TAG, "peers' size incorrect. retry.");
                            retry();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                private void retry() {
                    if (mSignalManager == null) {//非空判断，防止先执行了release而导致空指针
                        return;
                    }
                    if (mPeerNames.size() > 0) {
                        if (getAnchorLinkMicFlag() > 0) {
                            mLocalRoomOwner.mixControl(getMixControlBody("create", mPeerName, 0));
                        } else {
                            mLocalRoomOwner.mixControl(getMixControlBody("create", mPeerName, mChatConfig.mergeLayout));
                        }
                    } else {
                        mLocalRoomOwner.mixControl(getMixControlBody("destroy", mPeerName, mChatConfig.mergeLayout));
                    }
                }

            };
            mMediaClientManager.setListener(mixControlListener);
        }
    }

    private void initSignalManager() {
        mSignalManager = SignalManager.getInstance();
        mSignalManager.init(mAppContext, mChatConfig);

        SignalListener signalListener = new SignalListener() {
            @Override
            public void onSignalEvent(int eventCode, Object extra) {
                if (mChatHandler == null) {
                    ALog.e(TAG, "event:" + eventCode + " " + extra);
                    return;
                }
                Message msg = Message.obtain();
                msg.what = eventCode;
                msg.arg1 = SIGNAL_SERVER_MSG;
                msg.obj = extra;
                mChatHandler.sendMessage(msg);
            }

            @Override
            public void onSignalEventLog(String log) {
                if (mLinkMicListener != null) {
                    mLinkMicListener.statusInfo(log, null);
                }
            }
        };
        mSignalManager.registerChatListener(signalListener);

        ChatStateListener chatStateListener = new ChatStateListener() {
            @Override
            public void onChatStateChange(WSChatConstants.ChatState preState, WSChatConstants.ChatState newState) {
                ALog.i(TAG, "onChatStateChange preState : " + preState.toString() + " , newState : " + newState.toString());
                if (mChatStateListener != null) {
                    mChatStateListener.onChatStateChange(preState, newState);
                }
                //mChatState = newState;
            }
        };
        mSignalManager.registerChatStateChangeListener(chatStateListener);

        SignalManager.AnchorChatStateListener anchorChatStateListener = new SignalManager.AnchorChatStateListener() {
            @Override
            public void onAnchorChatStateChange(int preState, int newState) {
                ALog.i(TAG, "onChatStateChange preState : " + preState + " , newState : " + newState);
                if (mAnchorChatStateListener != null) {
                    mAnchorChatStateListener.onAnchorChatStateChange(preState, newState);
                }
            }

        };
        mSignalManager.registerAnchorChatStateChangeListener(anchorChatStateListener);
    }

    //1、向SLS服务器发送开始推流的信令
    //2、创建成功，主播向媒体服务器发送媒体数据
    private void startPushInternal() {
        ALog.i(TAG, "startPushInternal ... ");
        mSignalManager.startStream();
    }

    //1、向SLS服务器停止发送推流的信令
    //2、踢出该房间的所有观众
    //3、主播停止媒体数据传输
    //4、恢复布局
    private void stopPushInternal() {
        ALog.i(TAG, "stopPushInternal ... ");
        mSignalManager.stopStream();
        mLocalRoom.removeAllParticipators();
        mLocalRoomOwner.stopPush();
        mChatViewManager.resetRtcViewLayout();
    }

    protected void startPlayInternal() {
        ALog.i(TAG, "startPlayInternal begin ... " + mIjkVideoView);
        if (mChatConfig.pullRtmpUrl == null) {
            ALog.i(TAG, "Error pull url");
            return;
        }

        if (mIjkVideoView != null) {
            //此时已经在播放了
            return;
        }

        if (mChatConfig.captureView != null) {

            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    mChatConfig.captureView.setVisibility(View.GONE);
                }
            });
        }

        mIjkVideoView = mChatViewManager.loadVideoView();
        mIjkVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        mIjkVideoView.setVideoURI(Uri.parse(mChatConfig.pullRtmpUrl));
        mIjkVideoView.start();
        ALog.i(TAG, "startPlayInternal end ... " + mIjkVideoView);
    }

    protected void stopPlayInternal() {
        ALog.i(TAG, "stopPlayInternal begin ... " + mIjkVideoView);
        if (mIjkVideoView != null) {
            mIjkVideoView.stopPlayback();
            mIjkVideoView.release(true);
            mIjkVideoView.stopBackgroundPlay();

            mChatViewManager.unloadVideoView(mIjkVideoView);
            mIjkVideoView = null;
            if (mChatConfig.captureView != null) {
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mChatConfig.captureView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private void startLinkMicInternal() {
        mSignalManager.startLinkMic(null);
    }

    private void startAnchorLinkMicInternal(String anchorId, String roomId) {
        mSignalManager.startAnchorLinkMic(anchorId, roomId, null);
    }

    //1、向SLS服务器发送离开连麦的信令
    //2、踢出该房间的所有人
    //3、观众停止连麦
    private void stopLinkMicInternal() {
        mSignalManager.requestStopChat();
        mLocalRoom.removeAllParticipators();
        mLocalRoomOwner.stopPush();

        startPlayInternal();
    }

    //1、向SLS服务器发送离开连麦的信令
    //2、刷新ui
    //3、停止主播连麦
    private void stopAnchorLinkMicInternal() {
        mSignalManager.requestStopAnchorChat();
        mLocalRoom.removeAllParticipators();
    }

    //主播回复观众的请求
    private void replyChatRequestInternal(String userId, boolean isAccept, String pushUrl) {
        if (TextUtils.isEmpty(pushUrl) || !isAccept) {
            mSignalManager.replyStartChat(userId, isAccept, null, pushUrl);
        } else {
            mOtherAnchorId = userId;
            mAnchorPushUrl = pushUrl;
            //同意主播连麦时，先清空所有观众，再发送同意消息
            mSignalManager.kickAll(SignalConstants.FLAG_LINK_MIC_ANCHOR_ROOMOWNER);
        }
    }

    //踢出指定观众
    private void kickAudienceInternal(String audienceId) {
        mSignalManager.kickAudience(audienceId);
    }

    private void releaseInternal() {
        ALog.d(TAG, "releaseInternal." + AppRTCUtils.getThreadInfo());
        synchronized (mHandlerLock) {
            stopPlayInternal();

            releaseSignalManager();
            releaseChatViewManager();
            releaseLocalRoom();
            releaseMediaClientManager();

            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }

            mAppContext = null;
            ALog.d(TAG, "releaseInternal...done  ");
        }
    }

    private void releaseLocalRoom() {
        if (mLocalRoomOwner != null) {
            mLocalRoomOwner.release();
            mLocalRoomOwner = null;
        }
        if (mLocalRoom != null) {
            mLocalRoom.release();
            mLocalRoom = null;
        }
    }

    private void releaseChatViewManager() {
        // 恢复布局
        if (mChatViewManager != null) {
            //mChatViewManager.restoreRtcView(mChatConfig.anchorId);
            //mChatViewManager.resetRtcViewLayout();
            mChatViewManager = null;
        }
    }

    private void releaseMediaClientManager() {
        if (mMediaClientManager != null) {
            mMediaClientManager.release();
            mMediaClientManager = null;
        }
    }

    private void releaseSignalManager() {
        if (mSignalManager != null) {
            mSignalManager.unregisterChatListener();
            mSignalManager.unregisterChatStateChangeListener();
            mSignalManager.unregisterAnchorChatStateChangeListener();
            mSignalManager.release();
            mSignalManager = null;
        }
    }

    private void checkIsAnchor() {
        if (!mIsAnchor) {
            throw new IllegalStateException("Only Anchor can call this Method.");
        }
    }

    private void checkIsAudience() {
        if (mIsAnchor) {
            throw new IllegalStateException("Only LocalRoomOwner can call this Method.");
        }
    }

    private void runOnChatManagerThread(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable can't be null.");
        }
        synchronized (mHandlerLock) {
            if (mChatHandler == null) {
                throw new IllegalArgumentException("mChatHandler can't be null.");
            }
            mChatHandler.post(runnable);
        }
    }

    public void showWaterMarkTime(final float x, final float y, final float w, final int color, final float alpha) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.showWaterMarkTime(x, y, w, color, alpha);
                }
            }
        });
    }

    public void hideWaterMarkTime() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.hideWaterMarkTime();
                }
            }
        });
    }

    public void showWaterMarkLogo(final String path, final float x, final float y, final float w, final float h, final float alpha) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.showWaterMarkLogo(path, x, y, w, h, alpha);
                }
            }
        });
    }

    public void hideWaterMarkLogo() {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                mDrawer.hideWaterMarkLogo();
            }
        });
    }

    public boolean switchFilter(final WSChatConstants.FilterType type) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.switchFilter(type);
                }
            }
        });
        return true;
    }

    public boolean setStyleFilterModel(final String modelPath, final int level) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.setStyleFilterModel(modelPath, level);
                }
            }
        });
        return true;
    }

    public void setFilter(final WSVideoFilter filter) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.setFilter(filter);
                }
            }
        });
    }

    public void setFilter(final List<WSVideoFilter> filters) {
        runOnChatManagerThread(new Runnable() {
            @Override
            public void run() {
                if (mDrawer != null) {
                    mDrawer.setFilter(filters);
                }
            }
        });
    }

    /**
     * 获取主播连麦标记
     *
     * @return
     */
    public int getAnchorLinkMicFlag() {
        return mSignalManager.getAnchorLinkMicFlag();
    }

    public VideoCapturer getCustomVideoCapturer() {
        return mCustomVideoCapturer;
    }


    private boolean handleMediaMessage(Message msg) {
        switch (msg.what) {
            case MediaConstants.EVENT_SWITCH_FLASHMODE_SUCCESS:
                if (mLinkMicListener != null) {
                    mLinkMicListener.onSwitchFlashMode(true);
                }
                break;
            case MediaConstants.EVENT_SWITCH_FLASHMODE_FAILED:
                if (mLinkMicListener != null) {
                    mLinkMicListener.onSwitchFlashMode(false);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private boolean handleSignalMessage(Message msg) {
        Map<String, Object> extraMap = null;
        List<Map<String, Object>> extraList = null;
        ALog.i(TAG, "handleSignalMessage " + msg.what);
        switch (msg.what) {
            case SignalConstants.EVENT_GET_DISPATCH_ADDR:
                //获取到媒体服务器地址
                extraMap = (Map<String, Object>) msg.obj;
                String[] pushMediaUrlArray = (String[]) extraMap.get(SignalConstants.KEY_DISPATCH_ADDR_List);
                ALog.e(TAG, Arrays.toString(pushMediaUrlArray));
                if (mLocalRoomOwner != null) {
                    mLocalRoomOwner.setPushUrl(pushMediaUrlArray);
                }
                break;
            case SignalConstants.EVENT_ROOM_CREATED:
                //房间创建成功开始推流
                if (mLocalRoomOwner != null) {
                    mLocalRoomOwner.startPush();
                    recoverNormalLayout();
                }
                if (mLinkMicListener != null) {
                    mLinkMicListener.onStartPushState(true, "房间创建成功，开始推流.");
                }
                break;
            case SignalConstants.EVENT_REQUEST_INCOME:
                extraList = (List<Map<String, Object>>) msg.obj;
                if (mSignalManager != null && mSignalManager.getAnchorLinkMicFlag() > 0) {
                    if (extraList == null) {
                        break;
                    }
                    //主播连麦中，拒绝所有连麦者，除了连麦主播
                    for (Map<String, Object> map : extraList) {
                        if (map.get(SignalConstants.KEY_USER_ID).equals(mOtherAnchorId)) {
                            if (mLinkMicListener != null) {
                                List<Map<String, Object>> userList = new LinkedList<>();
                                userList.add(map);
                                mLinkMicListener.receiveRequestIncome(userList);
                            }
                            continue;
                        }
                        if (mLinkMicListener != null) {
                            mLinkMicListener.statusInfo("[Signla] 主播连麦中，拒绝连麦者: " + map.get(SignalConstants.KEY_USER_ID), null);
                        }
                        mSignalManager.replyStartChat((String) map.get(SignalConstants.KEY_USER_ID), false, null, (String) map.get(SignalConstants.KEY_PUSH_URL));
                    }
                } else if (mLinkMicListener != null) {
                    mLinkMicListener.receiveRequestIncome(extraList);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_REQUEST_LINKMIC_SUCCESS:
                if (mLinkMicListener != null) {
                    mLinkMicListener.onLinkMicRequestResult(true, null);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_CANCEL_LINKMIC_SUCCESS:
                if (mLinkMicListener != null) {
                    mLinkMicListener.onLinkMicRequestResult(true, null);
                }
                break;
            case SignalConstants.EVENT_REQUEST_CANCLED:
                extraMap = (Map<String, Object>) msg.obj;
                if (mLinkMicListener != null) {
                    mLinkMicListener.memberCancelRequest((String) extraMap.get(SignalConstants.KEY_USER_ID));
                }
                break;
            case SignalConstants.EVENT_REQUEST_REFUSED:
                extraMap = (Map<String, Object>) msg.obj;
                String requestRefusedRoomId = (String) extraMap.get(SignalConstants.KEY_ROOM_ID);
                if (mLinkMicListener != null) {
                    mLinkMicListener.onLinkMicRequestResult(false, "");
                }
                break;
            case SignalConstants.EVENT_REQUEST_AGREED:
                //观众请求被同意
                //观众获取到推流地址
                extraMap = (Map<String, Object>) msg.obj;
                String[] requestAgreedPushUrlArray = (String[]) extraMap.get(SignalConstants.KEY_PUSH_URL);
                ALog.e(TAG, "push:" + Arrays.toString(requestAgreedPushUrlArray));
                mLocalRoomOwner.setPushUrl(requestAgreedPushUrlArray);
                break;
            case SignalConstants.EVENT_MEMBER_ADDED:
                extraList = (List<Map<String, Object>>) msg.obj;
                List<Participator> memberAddList = Participator.parseMemberAddedFromList(extraList);
                if (memberAddList == null || memberAddList.size() == 0)
                    return false;

                //如果本地房间的拥有者是观众，则开始启动推流
                if (!mIsAnchor) {
                    mLocalRoomOwner.startPush();
                }

                for (Participator participant : memberAddList) {
                    ALog.d(TAG, "Add Member:" + participant.mUserId + ", " + participant.mPushUrl);
                    //为每一为观众分配一个View和一个PullPCClient
                    if (!mIsAnchor && mChatConfig.anchorId != null && mChatConfig.anchorId.equals(participant.mUserId)) {
                        stopPlayInternal();
                    }
                    //只有主播才能控制合流布局
                    if (mIsAnchor) {
                        mPeerNames.put(participant.mUserId, getPeerNameFromUrl(participant.mPushUrl));
                        if (participant.mIsAnchor) {//加入的是主播
                            mLocalRoomOwner.mixControl(getMixControlBody("create", mPeerName, 0));
                        } else {
                            mLocalRoomOwner.mixControl(getMixControlBody("join", getPeerNameFromUrl(participant.mPushUrl), mChatConfig.mergeLayout));
                        }
                    }
                    WSSurfaceView view = mChatViewManager.getRemoteView(participant.mUserId);
                    PullPCClient client = mMediaClientManager.createPullClient(view);
                    participant.setPullUrl(participant.mPullUrls);
                    participant.setPullPCClient(client);
                    mLocalRoom.addParticipator(participant);
                    ALog.e(TAG, "pull:" + Arrays.toString(participant.mPullUrls));
                }
                if (mLinkMicListener != null) {
                    mLinkMicListener.memberJoinRoom(extraList);
                }
                break;
            case SignalConstants.EVENT_MEMBER_DELETED:
                List<Map<String, Object>> memberDeleteList = (List<Map<String, Object>>) msg.obj;
                if (memberDeleteList == null || memberDeleteList.size() == 0)
                    return false;
                ALog.i(TAG, "handle MEMBER_DELETED, memberDeleteList size is : " + memberDeleteList.size());

                for (Map<String, Object> participant : memberDeleteList) {
                    String userId = (String) participant.get(SignalConstants.KEY_USER_ID);
                    String pushUrl = (String) participant.get(SignalConstants.KEY_PUSH_URL);
                    String isAnchor = (String) participant.get(SignalConstants.KEY_IS_ANCHOR);
                    String anchorId = mChatConfig.anchorId;
                    ALog.d(TAG, "Delete Member:" + userId + ", " + pushUrl);

                    //只有主播才能控制合流布局
                    if (mIsAnchor) {
                        mPeerNames.remove(userId);
                        if ("0".equals(isAnchor) && mPeerNames.size() == 1) {//退出主播连麦模式
                            mLocalRoomOwner.mixControl(getMixControlBody("create", mPeerName, mChatConfig.mergeLayout));
                        } else {
                            mLocalRoomOwner.mixControl(getMixControlBody("quit", getPeerNameFromUrl(pushUrl), mChatConfig.mergeLayout));
                        }
                    }
                    mLocalRoom.removeParticipator(userId);
                    mChatViewManager.restoreRtcView(userId);

                    // 主播退出房间时
                    // 1、所有观众也退出
                    // 2、停止本地的推流
                    if (anchorId.equals(userId) && mSignalManager.getAnchorLinkMicFlag() == SignalConstants.FLAG_LINK_MIC_NORMAL) {
                        ALog.i(TAG, "handle MEMBER_DELETED, the anchor has left");
                        mLocalRoom.removeAllParticipators();
                        mLocalRoomOwner.stopPush();
                    }
                    if (mLinkMicListener != null) {
                        //mLinkMicListener.statusInfo("[Signal] " + userId + "离开房间.", null);
                    }
                }

                if (mLinkMicListener != null) {
                    mLinkMicListener.memberExitRoom(memberDeleteList);
                }
                break;
            case SignalConstants.EVENT_JOIN_LIMITED:
                extraMap = (Map<String, Object>) msg.obj;
                String joinLimitedId = (String) extraMap.get(SignalConstants.KEY_USER_ID);
                if (mLinkMicListener != null) {
                    mLinkMicListener.memberJoinFailed4RoomLimit(joinLimitedId);
                }
                break;
            case SignalConstants.EVENT_ROOM_DESTROYED:
                extraMap = (Map<String, Object>) msg.obj;
                String destroyedRoomId = (String) extraMap.get(SignalConstants.KEY_ROOM_ID);
                if (!mChatConfig.roomID.equals(destroyedRoomId) && mIsAnchor) {
                    //小主播连麦状态大主播房间被销毁，
                    //主播连麦时，重置合流
                    recoverNormalLayout();
                    mLocalRoom.removeAllParticipators();
                    mChatViewManager.resetRtcViewLayout();
                    mLinkMicListener.statusInfo("[Signla] 连麦主播房间销毁: ", null);
                    if (mLinkMicListener != null) {
                        mLinkMicListener.clearQueue("连麦主播退出");
                    }
                } else {
                    //普通房间被销毁
                    //销毁合流
                    //清空房间中的所有人
                    //观众和主播停止推流
                    if (mIsAnchor) {
                        mPeerNames.remove(mChatConfig.userId);
                        mLocalRoomOwner.mixControl(getMixControlBody("destroy", mPeerName, mChatConfig.mergeLayout));
                    }
                    mLocalRoom.removeAllParticipators();
                    mLocalRoomOwner.stopPush();
                    mChatViewManager.resetRtcViewLayout();
                    if (mLinkMicListener != null) {
                        mLinkMicListener.roomDestoryed(destroyedRoomId);
                    }
                }
                break;
            case SignalConstants.EVENT_SERVER_DISCONNECTED:
                //和服务器断开连接
                //清空房间中的所有人
                //主播停止推流
                //恢复界面
                extraMap = (Map<String, Object>) msg.obj;
                String disconnectedRoomId = (String) extraMap.get(SignalConstants.KEY_ROOM_ID);
                mLocalRoom.removeAllParticipators();
                mLocalRoomOwner.stopPush();
                mChatViewManager.resetRtcViewLayout();
                if (mLinkMicListener != null) {
                    mLinkMicListener.disconnected(disconnectedRoomId);
                    ALog.e(TAG, "和服务器断开连接.");
                }
                break;
            case SignalConstants.EVENT_MEMBER_KICKED:
            case SignalConstants.EVENT_MEMBER_LOST:
                //观众被踢出房间，观众停止推流
                //开启RTMP拉流.
                extraMap = (Map<String, Object>) msg.obj;
                String memberKickedUserId = (String) extraMap.get(SignalConstants.KEY_USER_ID);

                mLocalRoomOwner.stopPush();
                mLocalRoom.removeAllParticipators();
                mChatViewManager.resetRtcViewLayout();

                //开启视频播放
                startPlayInternal();

                if (mLinkMicListener != null) {
                    mLinkMicListener.kickedout(memberKickedUserId);
                }
                break;
            case SignalConstants.EVENT_ANCHOR_LINK_MEMBER_KICKED:
            case SignalConstants.EVENT_ANCHOR_LINK_MEMBER_LOST:
                //主播被踢，停止连麦，重置合流布局
                recoverNormalLayout();
                mLocalRoom.removeAllParticipators();
                mChatViewManager.resetRtcViewLayout();
                if (mLinkMicListener != null) {
                    mLinkMicListener.clearQueue("停止主播连麦");
                }
                break;
            case SignalConstants.EVENT_ANCHOR_STOP_LINKMIC_SUCCESS:
                //小主播停止连麦
                recoverNormalLayout();
                mLocalRoom.removeAllParticipators();
                mChatViewManager.resetRtcViewLayout();
                if (mLinkMicListener != null) {
                    mLinkMicListener.clearQueue(null);
                }
                break;
            case SignalManager.ERROR_CONNECT_SERVER_FAILED:
                //连接服务器失败
                //主播停止推流、踢出所有观众
                //观众停止推流、踢出所有观众
                //恢复布局
                mLocalRoom.removeAllParticipators();
                mLocalRoomOwner.stopPush();
                mChatViewManager.resetRtcViewLayout();
                if (mLinkMicListener != null) {
                    mLinkMicListener.onStartPushState(false, "连接服务器失败");
                    mLinkMicListener.onLinkMicRequestResult(false, "连接服务器失败");
                }
                break;
            case SignalConstants.EVENT_REQUEST_FAILED:// 用户一些请求发送失败时回调
                extraMap = (Map<String, Object>) msg.obj;
                int errorAction = (Integer) extraMap.get(SignalConstants.KEY_ERROR_ACTION);
                String userId = (String) extraMap.get(SignalConstants.KEY_USER_ID);// 正好这三个action都有userId字段
                switch (errorAction) {
                    case SignalConstants.ACTION_ACCEPT_CHAT:
                        if (mLinkMicListener != null) {
                            mLinkMicListener.acceptChatFailed(userId, (String) extraMap.get(SignalConstants.KEY_PUSH_URL));
                        }
                        break;
                    case SignalConstants.ACTION_KICK_SOMEONE:
                        if (mLinkMicListener != null) {
                            mLinkMicListener.kickSomeoneFailed(userId);
                        }
                        break;
                    case SignalConstants.ACTION_REFUSE_CHAT:
                        // 拒绝连麦不管请求是否能送达服务器，都直接把用户从本地等待列表删除，
                        break;
                    default:
                        break;
                }
                if (mLinkMicListener != null) {
                    mLinkMicListener.statusInfo("[Signal] " + "actionCode : " + errorAction + ", statusMsg : " + msg.obj, null);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_SERVER_STATUS_ERROR: //服务器状态错误，有的状态需要本地进行对应的处理
                extraMap = (Map<String, Object>) msg.obj;
                int statusCode = (int) extraMap.get(SignalConstants.KEY_STATUS_CODE);
                String des = (String) extraMap.get(SignalConstants.KEY_DESCRIP);
                if (statusCode == SignalConstants.STATUS_CREATE_EXISTING_ROOM) { //房间已经存在,主播直接开始推流，如果有观众，开始拉流
                    if (mLocalRoomOwner != null) {
                        mLocalRoomOwner.startPush();
                    }
                    recoverNormalLayout();
                    if (mLinkMicListener != null) {
                        mLinkMicListener.statusInfo("[Signla] 房间已创建，重新连接.", null);
                        mLinkMicListener.onStartPushState(true, "房间已创建.");
                    }
                    break;
                } else if (statusCode == SignalConstants.STATUS_JOIN_ALREADY_CONNECTED) { //观众已经在连麦列表中，直接开始推拉流
                    //当该连麦观众已在连麦链表时，会先收到主播同意的消息，然后收到观众加入的全量广播；
                    if (mLinkMicListener != null) {
                        mLinkMicListener.onLinkMicRequestResult(true, "已在连麦列表中，开始推流.");
                    }
                } else if (statusCode == SignalConstants.STATUS_JOIN_ALREADY_WAITING) {
                    if (mLinkMicListener != null) {
                        mLinkMicListener.statusInfo("[Signal] 已在连麦等待列表中，等待主播同意.", null);
                        mLinkMicListener.onLinkMicRequestResult(true, "已在等待列表.");
                    }
                } else if (statusCode == STATUS_ROOM_NOT_EXISTING) {
                    if (mLinkMicListener != null) {
                        mLinkMicListener.statusInfo("[Signal] 房间不存在.", null);
                        mLinkMicListener.onLinkMicRequestResult(false, "房间不存在.");
                    }
                }
                if (mLinkMicListener != null) {
                    mLinkMicListener.statusInfo("[Signal] " + "statusCode : " + statusCode + ", statusMsg : " + des, null);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_KICK_ALL_SUCCESS:
                LinkedList<Participant> userList = (LinkedList<Participant>) msg.obj;
                for (Participant participant : userList) {
                    mPeerNames.remove(participant.userId);
                    if (mLocalRoomOwner == null) {
                        return false;
                    }
                    mLocalRoomOwner.mixControl(getMixControlBody("quit", getPeerNameFromUrl(participant.pushMediaUrl), mChatConfig.mergeLayout));
                }
                if (mLocalRoom != null) {
                    mLocalRoom.removeAllParticipators();
                }
                if (mChatViewManager != null) {
                    mChatViewManager.resetRtcViewLayout();
                }
                if (mSignalManager != null) {
                    //清空所有连麦者成功
                    if (mSignalManager.getAnchorLinkMicFlag() == SignalConstants.FLAG_LINK_MIC_ANCHOR_ROOMOWNER) {
                        mSignalManager.replyStartChat(mOtherAnchorId, true, null, mAnchorPushUrl);
                    } else if (mSignalManager.getAnchorLinkMicFlag() == SignalConstants.FLAG_LINK_MIC_ANCHOR_NOT_ROOMOWNER) {
                        startAnchorLinkMicInternal(mOtherAnchorId, mOtherRoomId);
                    }
                }
                if (mLinkMicListener != null) {
                    mLinkMicListener.clearQueue(null);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_KICK_ALL_FAIL:
                int preFlag = (int) msg.obj;
                if (mLinkMicListener != null) {
                    mLinkMicListener.kickAllFailed(preFlag == SignalConstants.FLAG_LINK_MIC_ANCHOR_ROOMOWNER ? mOtherAnchorId : null, mAnchorPushUrl);
                }
                break;
            case SignalConstants.EVENT_SIGNAL_KICK_ANCHOR_SUCCESS:
                if (mLinkMicListener != null) {
                    mLinkMicListener.clearQueue("t掉连麦主播，退出连麦");
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void recoverNormalLayout() {
        mPeerNames.clear();
        mPeerNames.put(mChatConfig.userId, mPeerName);
        mLocalRoomOwner.mixControl(getMixControlBody("create", mPeerName, mChatConfig.mergeLayout));
    }

    private String getMixControlBody(String action, String peer, int layout) {
        try {
            JSONStringer mixJson = new JSONStringer();
            mixJson.object();
            mixJson.key("action").value(action);
            mixJson.key("room").value(mChatConfig.appId + "_" + mChatConfig.roomID);

            if ("create".equals(action)) {
                mixJson.key("peers");
                mixJson.array();
                if (mPeerNames.size() > 0) {
                    HashMap<String, String> tempPeers = new HashMap<>();
                    tempPeers.putAll(mPeerNames);
                    for (Map.Entry<String, String> entry : tempPeers.entrySet()) {
                        mixJson.object();
                        mixJson.key("name").value(entry.getValue());
                        // 设置主播画面为最底层，观众依次叠加
                        mixJson.key("layout_index").value(mChatConfig.userId.equals(entry.getKey()) ? 0 : -1);
                        mixJson.endObject();
                    }
                }
                // peers
                mixJson.endArray();
            }

            if ("join".equals(action) || "quit".equals(action)) {
                mixJson.key("peers");
                mixJson.array();
                mixJson.object();
                mixJson.key("name").value(peer);
                // 设置主播画面为最底层，观众依次叠加
                mixJson.key("layout_index").value(-1);
                mixJson.endObject();
                // peers
                mixJson.endArray();
            }

            if ("create".equals(action) || "modify".equals(action)) {
                mixJson.key("mix_config");
                mixJson.object();
                // 合流地址，不含scheme
                mixJson.key("room_url").value(mChatConfig.pushRtmpUrl.replace("rtmp://", ""));
                mixJson.key("max_bitrate").value(mChatConfig.videoBitrate / 1024);
                mixJson.key("frame_rate").value(mChatConfig.fps);

                mixJson.key("resolution");
                mixJson.object();

                int rotation = CameraUtils.getRotation(mAppContext, mChatConfig.cameraId);
                int width = mChatConfig.videoResolution.getWidth();
                int height = mChatConfig.videoResolution.getHeight();
                ALog.i(TAG, "CameraUtils getRotation " + rotation);
                if (rotation == 90 || rotation == 270) {
                    width = mChatConfig.videoResolution.getHeight();
                    height = mChatConfig.videoResolution.getWidth();
                }
                if (mSignalManager.getAnchorLinkMicFlag() > 0) {
                    // 双主播连麦，合流宽度翻倍
                    width = width * 2;
                }
                mixJson.key("width").value(width);
                mixJson.key("height").value(height);
                // resolution
                mixJson.endObject();

                mixJson.key("fill").value(2);
                mixJson.key("layout").value(layout);
                if (layout == 0) {
                    mixJson.key("layout_content");
                    mixJson.array();
                    mixJson.object();
                    mixJson.key("x").value(0);
                    mixJson.key("y").value(0);
                    mixJson.key("width").value(0.5);
                    mixJson.key("height").value(1);
                    mixJson.endObject();
                    mixJson.object();
                    mixJson.key("x").value(0.5);
                    mixJson.key("y").value(0);
                    mixJson.key("width").value(0.5);
                    mixJson.key("height").value(1);
                    mixJson.endObject();
                    // layout_content
                    mixJson.endArray();
                }

                // mix_config
                mixJson.endObject();
            }
            mixJson.endObject();

            return mixJson.toString();
        } catch (JSONException e) {
            ALog.e(TAG, "JSONException ", e);
        }
        return "";
    }

    private String getPeerNameFromUrl(String url) {
        Uri uri = Uri.parse(url);
        String userHost = mChatConfig.userHostUrl;
        for (String name : uri.getQueryParameterNames()) {
            if (name.equals("host")) {
                userHost = uri.getQueryParameter(name);
                break;
            }
        }
        return (userHost + uri.getEncodedPath());
    }

    public interface LinkMicListener {
        void onStartPushState(boolean isSuccess, String des);

        void onLinkMicRequestResult(boolean isAgreed, String des);

        void kickedout(String userId);

        void memberCancelRequest(String userId);

        void memberExitRoom(Object userList);

        void memberJoinFailed4RoomLimit(Object userId);

        void memberJoinRoom(Object userList);

        void receiveRequestIncome(Object userList);

        void roomDestoryed(String roomId);

        void disconnected(String roomId);

        void bgmStop();

        //isSuccess:切换是否成功
        //isFrontCamera:是否是前置摄像头，该值在isSuccess==false的情况下无效
        void onSwitchCameraDone(boolean isSuccess, boolean isFrontCamera, String description);

        void onSwitchFlashMode(boolean isSuccess);

        void statusInfo(String info, Bundle bundle);

        void clearQueue(String message);

        void kickSomeoneFailed(String userId);

        void acceptChatFailed(String userId, String pushUrl);

        void kickAllFailed(String userId, String pushUrl);
    }
}
