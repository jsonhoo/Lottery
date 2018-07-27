package com.wyzk.lottery.video.utils;


import com.wangsu.wsrtc.sdk.WSChatConfig;
import com.wangsu.wsrtc.sdk.WSSurfaceView;

public class MyWSChatConfig {

    private static WSChatConfig getDefaultWSChatConfig() {
        WSChatConfig chatConfig = new WSChatConfig();
        chatConfig.setCameraId(chatConfig.cameraId);
        chatConfig.setPreferCodecMode(chatConfig.preferEncodeMode);
        chatConfig.setFps(chatConfig.fps);
        chatConfig.setVideoResolution(chatConfig.videoResolution);
        chatConfig.setVideoBitrate(500 * 1024);
        chatConfig.setHasVideo(true);
        chatConfig.setCustomVideoSource(false);
        chatConfig.setMergeLayout(11);
        chatConfig.setAppId("yehua");
        chatConfig.setAuthKey("C0ED2EB419E14EE38BE38D3D1AFC9E4A");
        chatConfig.setUserHostUrl("wsrtc.yehua.com");
        chatConfig.setAnchorId("888000");
        return chatConfig;
    }

    public static WSChatConfig getWSChatConfig(WSSurfaceView wsSurfaceView, String pushUrl, String pullUrl,
                                               String userId, String roomId, boolean isAnchor) {
        WSChatConfig config = getDefaultWSChatConfig();
        config.setCaptureView(wsSurfaceView);
        config.setPushRtmpUrl(pushUrl);
        config.setPullRtmpUrl(pullUrl);
        config.setUserId(userId);
        config.setRoomID(roomId);
        config.setIsAnchor(isAnchor);
        return config;
    }

}
