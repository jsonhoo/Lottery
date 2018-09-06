package com.het.csrmesh.api;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;
import com.csr.internal.mesh.client.api.AuthListener;
import com.csr.internal.mesh.client.api.MeshSecurityApi;
import com.csr.internal.mesh.client.api.common.Config;
import com.het.csrmesh.App;
import com.het.csrmesh.Exceptions.BleNotAvailableException;
import com.het.csrmesh.events.MeshRequestEvent;
import com.het.csrmesh.events.MeshResponseEvent;
import com.het.csrmesh.events.MeshSystemEvent;
import com.het.csrmesh.model.Gateway;
import com.het.csrmesh.model.listeners.LogLevel;
import com.het.csrmesh.utils.LocalLog;
import com.het.csrmesh.utils.UUIDUtils;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class that contains the API calls referred to the library settings.
 */
public class MeshLibraryManager extends Thread {
    protected static final String TAG = MeshLibraryManager.class.getSimpleName();

    private Handler mMeshHandler;
    private MeshService mService;

    public static LogLevel logLevel = LogLevel.NONE;
    private static MeshLibraryManager mSingleInstance;

    public static final String EXTRA_REQUEST_ID = "INTERNALREQUESTID";


    private static AtomicInteger mRequestId = new AtomicInteger(0);
    private HashMap<Integer, Integer> mRequestIds = new HashMap<Integer, Integer>();
    private boolean mShutdown = false;
    public boolean isBearReady = false;
    public static boolean isAutoConnect = true;
    private boolean isChannelReady = false;
    private boolean mIsInternetAvailable;
    private boolean mContinuousScanningWear = false;
    private boolean mContinuousScanningMobile = false;
    private boolean mCurrentContinuousScanning = false;
    private WeakReference<Context> mContext;

    private String mSelectedGatewayUUID = "";

    public enum MeshChannel {
        BLUETOOTH,
        INVALID, REST
    }

    private MeshChannel mCurrentChannel = MeshChannel.INVALID;
    private RestChannel.RestMode mCurrentRestMode;

    public static MeshLibraryManager getInstance() {
        return mSingleInstance;
    }

    public boolean isServiceAvailable() {
        return (mService != null);
    }

    private MeshLibraryManager(Context context, MeshChannel channel, LogLevel lgLevel) {
        logLevel = lgLevel;
        App.bus.register(this);
        mContext = new WeakReference<Context>(context);
        mCurrentChannel = channel;
        Intent bindIntent = new Intent(mContext.get(), MeshService.class);
        context.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    public static void initInstance(Context context, MeshChannel channel, LogLevel logLevel) {
        if (mSingleInstance == null) {
            mSingleInstance = new MeshLibraryManager(context, channel, logLevel);
            mSingleInstance.start();
        }
    }

    public static void initInstance(Context context, MeshChannel channel, LogLevel logLevel, boolean autoConnect) {
        if (mSingleInstance == null) {
            isAutoConnect = autoConnect;
            mSingleInstance = new MeshLibraryManager(context, channel, logLevel);
            mSingleInstance.start();
        }
    }


    public void shutdown() {
        if (mService == null) {
            return;
        }
        mShutdown = true;
        if (mCurrentChannel == MeshChannel.BLUETOOTH) {
            mService.shutDown();
        } else {
            App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.SERVICE_SHUTDOWN));
        }
    }

    public void onDestroy() {
        App.bus.unregister(this);
        mContext.get().unbindService(mServiceConnection);
        mSingleInstance = null;
        mService = null;
    }

    public MeshLibraryManager setAutoConnectEnable(boolean enable) {
        isAutoConnect = enable;
        return mSingleInstance;
    }

    /**
     * Return the list of bluetooth addresses of bridges connected.
     *
     * @return List of bluetooth addresses of bridges connected.
     */
    public ArrayList<String> getConnectedBridges() {
        return mService.getConnectedBridges();
    }


    /*package*/ MeshService getMeshService() {
        return mService;
    }


    /**
     * Send a message to a device to see if it needs to update it's security parameters.
     * If the device requests an update then this will be handled internally by the library, and
     * when it is complete the app will receive MESSAGE_DEVICE_KEY_IV_CHANGED.
     *
     * @param deviceId          The id of the device.
     * @param resetKey          The key returned when the device was associated.
     * @param networkPassPhrase The network pass phrase to respond with. If this doesn't match then the device will request an update.
     * @param networkIV         The network IV to respond with. If this doesn't match then the device will request an update.
     * @return Unique id to identify the request. Included in the response or timeout message as EXTRA_MESH_REQUEST_ID.
     */
    public int updateNetworkSecurity(int deviceId, byte[] resetKey, String networkPassPhrase, byte[] networkIV) {
        return mService.updateNetworkSecurity(deviceId, resetKey, networkPassPhrase, networkIV);
    }

    /**
     * Enable Bluetooth channel. Calls to model APIs made after this will be sent via Bluetooth.
     * Event CHANNEL_CHANGE is sent to indicate channel has changed.
     * There will be a delay before a bridge is connected. When that completes the event
     * BRIDGE_CONNECTED will be sent.
     */
    public void setBluetoothChannelEnabled() {
        if (mCurrentChannel != MeshChannel.BLUETOOTH) {
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        mCurrentChannel = MeshChannel.BLUETOOTH;
        isChannelReady = false;
//        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_NOT_READY));
        if (Build.VERSION.SDK_INT >= 21) {
            mService.setBluetoothBearerEnabled(ScanSettings.SCAN_MODE_LOW_LATENCY);
        } else {
            mService.setBluetoothBearerEnabled();
        }
    }

    public boolean isChannelReady() {
        return isChannelReady;
    }

    /**
     * Enable REST channel. Calls to model APIs made after this will be sent via REST (cloud or gateway).
     * Event CHANNEL_CHANGE is sent to indicate channel has changed.
     */
    public void setRestChannelEnabled(RestChannel.RestMode restMode, String tenantId, String siteId) {
        mCurrentRestMode = restMode;

        if (restMode == RestChannel.RestMode.GATEWAY) {
            mService.setRestChannel(Config.Channel.CHANNEL_GATEWAY);
        } else {
            mService.setRestChannel(Config.Channel.CHANNEL_CLOUD);
        }

        setRest(tenantId, siteId);
    }

    AuthListener mAuthListener = new AuthListener() {
        @Override
        public void notifyAuthenticationState(MeshSecurityApi.AuthenticationState state) {
            Log.d(TAG, "Authentication State update: " + state.toString());

            switch (state) {
                case NOT_AUTHENTICATED: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.REST_NOT_AUTHENTICATED));
                    Log.e(TAG, "NOT_AUTHENTICATED - Can not authenticate if a Gateway is not selected.");
                    break;
                }
                case AUTHENTICATION_INPROGRESS: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.REST_AUTHENTICATION_INPROGRESS));
                    break;
                }
                case AUTHENTICATION_FAILED: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.REST_AUTHENTICATION_FAILED));
                    Log.e(TAG, "Authentication FAILED");
                    break;
                }
                case AUTHENTICATED: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.REST_AUTHENTICATED));
                    break;
                }
                case AUTHENTICATION_EXPIRED: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.REST_AUTHENTICATION_EXPIRED));
                    Log.e(TAG, "AUTHENTICATION_EXPIRED - Can not authenticate if a Gateway is not selected.");
                    break;
                }
            }
        }
    };


    /**
     * Authenticate the RestClient API
     */
    public void authenticateRest(Gateway gateway) {
        if (!isRestAuthenticated()) {
            String controllerUUID = UUIDUtils.getControllerUUID(mService).toString();
            RestChannel.setRestParameters(MeshService.ServerComponent.AUTH, gateway.getHost(), gateway.getPort(), RestChannel.BASE_PATH_CGI + RestChannel.BASE_PATH_AUTH, RestChannel.URI_SCHEMA_HTTP);

            mService.authenticateRest(controllerUUID, gateway.getDhmKey(), mAuthListener);
        }
    }

    /**
     * Returns the authentication state
     *
     * @return AuthenticationState
     */
    public boolean isRestAuthenticated() {
        return mService.isRestAuthenticated();
    }


    /**
     * Enable REST channel without setting tenant nor site. Existing value set in library will be used if available.
     * Also tenant and site are not required for ConfigCloudApi, ConfigGatewayApi or Association.
     */
    public void setRestChannelEnabled() {
        setRest(null, null);
    }


    public String getMeshId() {
        return mService.getMeshId();
    }

    public MeshChannel getChannel() {
        return mCurrentChannel;
    }

    public RestChannel.RestMode getRestMode() {
        return mCurrentRestMode;
    }

    /**
     * Restart bonjour in Mesh Service
     */
    public void restartBonjour() {
        mService.restartNSDManager();
    }

    public void setNetworkPassPhrase(String passPhrase) {
        mService.setNetworkPassPhrase(passPhrase);
    }

    public void setControllerAddress(int address) {
        mService.setControllerAddress(address);
    }

    public void setApplicationCode(String applicationCode) {
        mService.setApplicationCode(applicationCode);
    }

    public void setIsInternetAvailable(boolean state) {
        mIsInternetAvailable = state;
    }

    public void setNetworkKey(byte[] networkKey) {
        mService.setNetworkKey(networkKey);
    }

    /*package*/ int getNextRequestId() {
        return mRequestId.incrementAndGet();
    }

    /*package*/ void setRequestIdMapping(int libraryId, int internalId) {
        mRequestIds.put(libraryId, internalId);
    }

    /*package*/ int getRequestId(int libraryId) {
        if (mRequestIds.containsKey(libraryId)) {
            return mRequestIds.get(libraryId);
        } else {
            return 0;
        }
    }

    /**
     * Given the request databaseId that is used in the wrapper, find the mesh request databaseId that
     * was returned from the library.
     *
     * @param requestId Wrapper request databaseId.
     * @return Mesh request databaseId from library, or zero if none found.
     */
    /*package*/ int getMeshRequestId(int requestId) {
        for (int libId : mRequestIds.keySet()) {
            if (requestId == mRequestIds.get(libId)) {
                return libId;
            }
        }
        return 0;
    }

    /**
     * Set bluetooth channel as bearer and connect to a bridge
     */
    private void connectBluetooth() {
        if (mService != null && mService.getActiveBearer() == MeshService.Bearer.BLUETOOTH) {
            mService.setMeshListeningMode(true, false);
            if (isAutoConnect) {
                mService.startAutoConnect(1);
            }
            //mService.setContinuousLeScanEnabled(true);
        }
    }

    /**
     * Automatically connect to numBridges bridges.
     *
     * @param numDevices Number of devices desired to be connected to.
     */
    public void startAutoConnect(int numDevices) {
        mService.startAutoConnect(numDevices);
    }

    /**
     * Return if autoconnect to bridges is enabled or not.
     *
     * @return True if autoconnect is enabled
     */
    public boolean isAutoConnectEnabled() {
        return mService.isAutoConnectEnabled();
    }

    /**
     * Stop the autoconnection with bridges.
     */
    public void stopAutoconnect() {
        isAutoConnect = false;
        if (mService != null) {
            mService.stopAutoConnect();
        }
    }

    /**
     * Connect to a specific bluetooth device.
     *
     * @param device
     */
    public void connectDevice(BluetoothDevice device) {
        mService.connectBridge(device);
    }

    /**
     * Disconnect from all connected Bluetooth LE bridge devices.
     * Bridges will not be automatically reconnected after disconnect.
     */
    public void disconnectAllDevices() {
        if (mService != null) {
            mService.disconnectAllBridges();
        }
    }

    /**
     * Disconnect from a specific bluetooth device.
     *
     * @param btAddress Bluetooth device address to disconnect.
     */
    public void disconnectDevice(String btAddress) {
        mService.disconnectBridge(btAddress);
    }

    /**
     * Set REST bearer parameters and disconnect BT bridge
     *
     * @param tenantId
     * @param siteId
     */
    private void setRest(String tenantId, String siteId) {
        isChannelReady = false;
        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_NOT_READY)); // Change recommended
        mCurrentChannel = MeshChannel.REST;
        mService.setRestBearerEnabled(tenantId, siteId);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((MeshService.LocalBinder) rawBinder).getService();
            if (mService != null) {
                mService.setHandler(getHandler());
                mService.setLeScanCallback(mScanCallBack);
                if (mCurrentChannel == MeshChannel.BLUETOOTH) {
                    enableBluetooth();
                }
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private BluetoothAdapter.LeScanCallback mScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mService != null)
                if (mService.processMeshAdvert(device, scanRecord, rssi)) {
                    {
                        Bundle data = new Bundle();
                        data.putParcelable(MeshConstants.EXTRA_DEVICE, device);
                        data.putInt(MeshConstants.EXTRA_RSSI, rssi);
                        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.DEVICE_SCANNED, data));
                    }
                }
        }
    };

    private static class MeshApiMessageHandler extends Handler {
        private final WeakReference<MeshLibraryManager> mParent;

        MeshApiMessageHandler(MeshLibraryManager machine) {
            this.mParent = new WeakReference<MeshLibraryManager>(machine);
        }

        @Override
        public void handleMessage(Message msg) {
            // If the message contains a mesh request databaseId then translate it to our internal databaseId.
            int meshRequestId = msg.getData().getInt(MeshConstants.EXTRA_MESH_REQUEST_ID);
            Bundle data = msg.getData();
            if (meshRequestId != 0) {
                int internalId = mParent.get().getRequestId(meshRequestId);
                if (internalId != 0) {
                    data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, internalId);
                    // No longer need to keep this mapping now that we have the response.
                    if (msg.what != MeshConstants.MESSAGE_ASSOCIATING_DEVICE) {
                        mParent.get().mRequestIds.remove(meshRequestId);
                    }
                }
                // Remove from the bundle as the client isn't interested in the library databaseId,
                // only the wrapper databaseId.
                data.remove(MeshConstants.EXTRA_MESH_REQUEST_ID);
            }
            // Handle mesh API messages and notify. Use the data variable NOT msg.getData() when retrieving data.
            switch (msg.what) {
                case MeshConstants.MESSAGE_LE_BEARER_READY: {
                    // The library is ready for connection now.
                    mParent.get().connectBluetooth();
                    mParent.get().isBearReady = true;
                    break;
                }
                case MeshConstants.MESSAGE_REST_BEARER_READY: {
                    if (mParent.get().mService.isRestConfigured()) {
                        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_READY));
                        mParent.get().isChannelReady = true;
                    }
                    break;
                }
                case MeshConstants.MESSAGE_LE_CONNECTED: {
                    Log.d(TAG, "MeshConstants.MESSAGE_LE_CONNECTED " + msg.getData().getString(MeshConstants.EXTRA_DEVICE_ADDRESS));
                    if (mParent.get().getChannel() == MeshChannel.BLUETOOTH) {
                        mParent.get().isChannelReady = true;
                        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_READY, data));
                    }
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.BRIDGE_CONNECTED, data));
                    break;
                }
                case MeshConstants.MESSAGE_LE_DISCONNECTED: {
                    LocalLog.w(TAG, "Response MESSAGE_LE_DISCONNECT");
                    if (mParent.get().getChannel() == MeshChannel.BLUETOOTH) {
                        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_NOT_READY, data));
                        mParent.get().isChannelReady = false;
                    }
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.BRIDGE_DISCONNECTED, data));
                    break;
                }
                case MeshConstants.MESSAGE_LE_DISCONNECT_COMPLETE: {
                    LocalLog.w(TAG, "Response MESSAGE_LE_DISCONNECT_COMPLETE");
                    if (!mParent.get().mShutdown) {
                        // Check if network is available.
                        if (mParent.get().mIsInternetAvailable) {
                            App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.CHANNEL_READY));
                            mParent.get().isChannelReady = true;
                        } else {
                            LocalLog.w(TAG, "Response MESSAGE_LE_TEST");
                            // TODO: handle scenario of Bluetooth disconnected and network not available - notify user he needs at least bluetooth or internet to control?
                        }
                    } else {
                        mParent.get().isChannelReady = false;
                        mParent.get().mCurrentChannel = MeshChannel.INVALID;
                        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.SERVICE_SHUTDOWN));
                    }
                    break;
                }

                case MeshConstants.MESSAGE_GATEWAY_SERVICE_DISCOVERED: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.GATEWAY_DISCOVERED, data));
                    break;
                }
                case MeshConstants.MESSAGE_DEVICE_APPEARANCE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DEVICE_APPEARANCE, data));
                    break;
                }
                case MeshConstants.MESSAGE_RESET_DEVICE:
                    /* The application can handle a request to reset here.
                     * It should calculate the signature using ConfigModelApi.computeResetDeviceSignatureWithDeviceHash(long, byte[])
                     * to check the signature is valid.
                     */
                    break;
                case MeshConstants.MESSAGE_BATTERY_STATE:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.BATTERY_STATE, data));
                    break;
                case MeshConstants.MESSAGE_LIGHT_STATE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LIGHT_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_POWER_STATE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.POWER_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_ACTION_SENT: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ACTION_SENT, data));
                    break;
                }
                case MeshConstants.MESSAGE_ACTION_DELETE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ACTION_DELETED, data));
                    break;
                }
                case MeshConstants.MESSAGE_ASSOCIATING_DEVICE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ASSOCIATION_PROGRESS, data));
                    break;
                }
                case MeshConstants.MESSAGE_LOCAL_DEVICE_ASSOCIATED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LOCAL_DEVICE_ASSOCIATED, data));
                    break;
                }
                case MeshConstants.MESSAGE_LOCAL_ASSOCIATION_FAILED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LOCAL_DEVICE_FAILED, data));
                    break;
                }
                case MeshConstants.MESSAGE_ASSOCIATION_PROGRESS: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LOCAL_ASSOCIATION_PROGRESS, data));
                    break;
                }
                case MeshConstants.MESSAGE_NETWORK_SECURITY_UPDATE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.MESSAGE_NETWORK_SECURITY_UPDATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_REQUEST_BT: {
                    App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.BT_REQUEST));
                    break;
                }
                case MeshConstants.MESSAGE_DEVICE_ASSOCIATED:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DEVICE_ASSOCIATED, data));
                    break;
                case MeshConstants.MESSAGE_TIMEOUT:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TIMEOUT, data));
                    break;

                case MeshConstants.MESSAGE_ATTENTION_STATE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ATTENTION_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_ACTUATOR_VALUE_ACK:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ACTUATOR_VALUE, data));
                    break;
                case MeshConstants.MESSAGE_ACTUATOR_TYPES:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ACTUATOR_TYPES, data));
                    break;
                case MeshConstants.MESSAGE_BEARER_STATE:
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.BEARER_STATE, data));
                    break;

                case MeshConstants.MESSAGE_SENSOR_STATE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SENSOR_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_SENSOR_VALUE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SENSOR_VALUE, data));
                    break;
                }
                case MeshConstants.MESSAGE_SENSOR_TYPES: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SENSOR_TYPES, data));
                    break;
                }
                case MeshConstants.MESSAGE_PING_RESPONSE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.PING_RESPONSE, data));
                    break;
                }
                case MeshConstants.MESSAGE_GROUP_NUM_GROUPIDS: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GROUP_NUMBER_OF_MODEL_GROUPIDS, data));
                    break;
                }
                case MeshConstants.MESSAGE_GROUP_MODEL_GROUPID: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GROUP_MODEL_GROUPID, data));
                    break;
                }
                case MeshConstants.MESSAGE_FIRMWARE_VERSION: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.FIRMWARE_VERSION_INFO, data));
                    break;
                }
                case MeshConstants.MESSAGE_DATA_SENT: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATA_SENT, data));
                    break;
                }
                case MeshConstants.MESSAGE_RECEIVE_BLOCK_DATA: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATA_RECEIVE_BLOCK, data));
                    break;
                }
                case MeshConstants.MESSAGE_RECEIVE_STREAM_DATA: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM, data));
                    break;
                }
                case MeshConstants.MESSAGE_RECEIVE_STREAM_DATA_END: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM_END, data));
                    break;
                }
                case MeshConstants.MESSAGE_DEVICE_ID: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.CONFIG_DEVICE_IDENTIFIER, data));
                    break;
                }
                case MeshConstants.MESSAGE_CONFIG_DEVICE_INFO: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.CONFIG_INFO, data));
                    break;
                }
                case MeshConstants.MESSAGE_DEVICE_DISCOVERED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DEVICE_UUID, data));
                    break;
                }
                case MeshConstants.MESSAGE_PARAMETERS: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.CONFIG_PARAMETERS, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_PROFILE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_PROFILE, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_REMOVE_NETWORK: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_REMOVE_NETWORK, data));
                    break;
                }
                case MeshConstants.MESSAGE_TENANT_RESULTS: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TENANT_RESULTS, data));
                    break;
                }
                case MeshConstants.MESSAGE_TENANT_CREATED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TENANT_CREATED, data));
                    break;
                }
                case MeshConstants.MESSAGE_TENANT_INFO: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TENANT_INFO, data));
                    break;
                }
                case MeshConstants.MESSAGE_TENANT_DELETED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TENANT_DELETED, data));
                    break;
                }
                case MeshConstants.MESSAGE_TENANT_UPDATED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TENANT_UPDATED, data));
                    break;
                }
                case MeshConstants.MESSAGE_SITE_RESULTS: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SITE_RESULTS, data));
                    break;
                }
                case MeshConstants.MESSAGE_SITE_CREATED: {
                    if (mParent.get().getChannel() == MeshChannel.REST) {
                        App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SITE_CREATED, data));
                    }
                    break;
                }
                case MeshConstants.MESSAGE_SITE_INFO: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SITE_INFO, data));
                    break;
                }
                case MeshConstants.MESSAGE_SITE_DELETED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SITE_DELETED, data));
                    break;
                }
                case MeshConstants.MESSAGE_SITE_UPDATED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.SITE_UPDATED, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_FILE_INFO: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_FILE_INFO, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_FILE: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_FILE, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_FILE_CREATED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_FILE_CREATED, data));
                    break;
                }
                case MeshConstants.MESSAGE_GATEWAY_FILE_DELETED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.GATEWAY_FILE_DELETED, data));
                    break;
                }
                case MeshConstants.MESSAGE_FIRMWARE_UPDATE_ACKNOWLEDGED: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.FIRMWARE_UPDATE_ACKNOWLEDGED, data));
                    break;
                }
                case MeshConstants.MESSAGE_TRANSACTION_NOT_CANCELLED: {
                    break;
                }
                case MeshConstants.MESSAGE_REST_ERROR: {
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.ERROR, data));
                    break;
                }
                case MeshConstants.MESSAGE_TIME_STATE: {
                    int timeInterval = data.getInt(MeshConstants.EXTRA_TIME_INTERVAL);
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TIME_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_LOT_ANNOUNCE: {
                    LocalLog.w(TAG, "Lot announce response");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LOT_INTEREST, data));
                    break;
                }
                case MeshConstants.MESSAGE_LOT_INTEREST: {
                    LocalLog.w(TAG, "Lot interest response");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.LOT_INTEREST, data));
                    break;
                }
                case MeshConstants.MESSAGE_DIAGNOSTIC_STATE: {
                    LocalLog.w(TAG, "response for DIAGNOSTIC_STATE ");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DIAGNOSTIC_STATE, data));
                    break;
                }
                case MeshConstants.MESSAGE_DIAGNOSTIC_STATS: {
                    LocalLog.w(TAG, "response for DIAGNOSTIC_STATES");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DIAGNOSTIC_STATS, data));
                    break;
                }
                case MeshConstants.MESSAGE_TRACKER_REPORT: {
                    LocalLog.w(TAG, "response for TRACKER_REPORT");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TRACKER_REPORT, data));
                    break;
                }
                case MeshConstants.MESSAGE_WATCHDOG_INTERVAL: {
                    LocalLog.w(TAG, "response for WATCHDOG_INTERVAL");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.WATCHDOG_INTERVAL, data));
                    break;
                }
                case MeshConstants.MESSAGE_WATCHDOG_MESSAGE: {
                    LocalLog.w(TAG, "response for WATCHDOG_MESSAGE");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.WATCHDOG_MESSAGE, data));
                    break;
                }
                case MeshConstants.MESSAGE_TRACKER_FOUND: {
                    LocalLog.w(TAG, "response for TRACKER_FOUND");
                    App.bus.post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.TRACKER_FOUND, data));
                    break;
                }

                default:
                    break;
            }
        }
    }

    private synchronized Handler getHandler() {
        while (mMeshHandler == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                //Ignore and try again.
            }
        }
        return mMeshHandler;
    }


    @Override
    public void run() {
        // Just process the message loop.
        Looper.prepare();
        synchronized (this) {
            mMeshHandler = new MeshApiMessageHandler(this);
            notifyAll();
        }
        Looper.loop();
    }


    @Subscribe
    public void onEvent(MeshRequestEvent event) {
        switch (event.what) {

            // Killing transactions
            case KILL_TRANSACTION:

                int id = getMeshRequestId(event.data.getInt(MeshConstants.EXTRA_DATA));
                mService.cancelTransaction(id);
                break;
            // Association:
            case DISCOVER_DEVICES:
            case ATTENTION_PRE_ASSOCIATION:
            case ASSOCIATE_DEVICE:
            case ASSOCIATE_GATEWAY:
            case START_ADVERTISING:
            case STOP_ADVERTISING:
                Association.handleRequest(event);
                break;

            case MASP_RESET:
                Association.handleRequest(event);
                break;

            // Bonjour
            case START_BROWSING_GATEWAYS:
                mService.startBrowsing();
                break;
            case STOP_BROWSING_GATEWAYS:
                mService.stopBrowsing();
                break;

            // MCP events. These are all handled by the model classes.
            case POWER_GET_STATE:
            case POWER_SET_STATE:
            case POWER_TOGGLE_STATE:
                PowerModel.handleRequest(event);
                break;

            case LIGHT_GET_STATE:
            case LIGHT_SET_LEVEL:
            case LIGHT_SET_COLOR_TEMPERATURE:
            case LIGHT_SET_POWER_LEVEL:
            case LIGHT_SET_RGB:
            case LIGHT_SET_WHITE:
                LightModel.handleRequest(event);
                break;

            case ATTENTION_SET_STATE:
                AttentionModel.handleRequest(event);
                break;

            case ACTION_SET_ACTION:
            case ACTION_DELETE_ACTION:
                ActionModel.handleRequest(event);
                break;

            case ACTUATOR_GET_TYPES:
            case ACTUATOR_SET_VALUE:
                ActuatorModel.handleRequest(event);
                break;

            case BEARER_SET_STATE:
            case BEARER_GET_STATE:
                BearerModel.handleRequest(event);
                break;

            case BATTERY_GET_STATE:
                BatteryModel.handleRequest(event);
                break;

            case SENSOR_GET_VALUE:
            case SENSOR_SET_VALUE:
            case SENSOR_GET_TYPES:
            case SENSOR_SET_STATE:
            case SENSOR_GET_STATE:
                SensorModel.handleRequest(event);
                break;

            case TIME_SET_STATE:
            case TIME_GET_STATE:
            case TIME_BROADCAST:
                BatteryModel.handleRequest(event);
                break;

            case PING_REQUEST:
                PingModel.handleRequest(event);
                break;

            case GROUP_GET_NUMBER_OF_MODEL_GROUP_IDS:
            case GROUP_SET_MODEL_GROUP_ID:
            case GROUP_GET_MODEL_GROUP_ID:
                GroupModel.handleRequest(event);
                break;

            case FIRMWARE_UPDATE_REQUIRED:
            case FIRMWARE_GET_VERSION:
                FirmwareModel.handleRequest(event);
                break;

            case DATA_SEND_DATA:
                DataModel.handleRequest(event);
                break;

            case CONFIG_DISCOVER_DEVICE:
            case CONFIG_GET_INFO:
            case CONFIG_RESET_DEVICE:
            case CONFIG_SET_DEVICE_IDENTIFIER:
            case CONFIG_GET_PARAMETERS:
            case CONFIG_SET_PARAMETERS:
                ConfigModel.handleRequest(event);
                break;

            // Gateway REST events.
            case GATEWAY_GET_PROFILE:
            case GATEWAY_REMOVE_NETWORK:
                ConfigGateway.handleRequest(event);
                break;

            // Cloud REST events.
            case CLOUD_GET_TENANTS:
            case CLOUD_CREATE_TENANT:
            case CLOUD_GET_TENANT_INFO:
            case CLOUD_DELETE_TENANT:
            case CLOUD_UPDATE_TENANT:
            case CLOUD_GET_SITES:
            case CLOUD_CREATE_SITE:
            case CLOUD_GET_SITE_INFO:
            case CLOUD_DELETE_SITE:
            case CLOUD_UPDATE_SITE:
                ConfigCloud.handleRequest(event);
                break;

            // Configuration
            case SET_CONTINUOUS_SCANNING:
                if (getChannel() == MeshChannel.BLUETOOTH) {
                    boolean enable = event.data.getBoolean(MeshConstants.EXTRA_DATA);
                    boolean wear = event.data.getBoolean(MeshConstants.EXTRA_WEAR);
                    if (wear) {
                        mContinuousScanningWear = enable;
                    } else {
                        mContinuousScanningMobile = enable;
                    }

                    if ((mContinuousScanningWear == true || mContinuousScanningMobile == true) && !mCurrentContinuousScanning) {
                        // enabling continuous scan.
                        mCurrentContinuousScanning = true;
                        BluetoothChannel.setContinuousScanEnabled(mCurrentContinuousScanning);
                    } else if (mContinuousScanningWear == false && mContinuousScanningMobile == false && mCurrentContinuousScanning) {
                        // disabling continuous scan.
                        mCurrentContinuousScanning = false;
                        BluetoothChannel.setContinuousScanEnabled(mCurrentContinuousScanning);
                    }
                }
                break;

            case SET_CONTROLLER_ADDRESS:
                int address = event.data.getInt(MeshConstants.EXTRA_DATA);
                mService.setControllerAddress(address);
                break;

            case LOT_ANNOUNCE:
            case LOT_INTEREST:
                LargeObjectTransferModel.handleRequest(event);
                break;
            case DIAGNOSTIC_GET_STATS:

                break;
        }
    }

    /**
     * Determine if bluetooth bridge is connected
     *
     * @return true if bridge is connected, false if not.
     */
    public static boolean isBluetoothBridgeReady() {
        if (MeshLibraryManager.getInstance() == null) {
            return false;
        }
        MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
        if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH && (MeshLibraryManager.getInstance().isChannelReady())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set selected gateway UUID to be used in local gateway control
     *
     * @param uuid selected gateway uuid
     */
    public void setSelectedGatewayUUID(final String uuid) {
        Log.d(TAG, "*** GW SELECTED: " + uuid);
        mSelectedGatewayUUID = uuid;
    }

    public String getSelectedGatewayUUID() {
        return mSelectedGatewayUUID;
    }


    //---- BT and BLE
    @TargetApi(18)
    public static boolean checkAvailability(Context mContext) throws BleNotAvailableException {
        if (Build.VERSION.SDK_INT < 18) {
            throw new BleNotAvailableException("Bluetooth LE not supported by this device");
        } else if (!mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            throw new BleNotAvailableException("Bluetooth LE not supported by this device");
        } else {
            return ((BluetoothManager) mContext.getSystemService(mContext.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
        }
    }
}
