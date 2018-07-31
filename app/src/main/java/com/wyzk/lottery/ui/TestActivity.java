package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fsix.mqtt.bean.MQBean;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.ACache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TestActivity extends LotteryBaseActivity {
    private String token;
    private EditText title_topic;
    private TextView logger;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        logger = (TextView) findViewById(R.id.logger);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        title_topic = (EditText) findViewById(R.id.title_topic);
        token = ACache.get(this).getAsString(IConst.TOKEN);
    }

    private void a(String text) {
        logger.append(text + "\r\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            a((String) msg.obj);
        }
    };

    public void show(String text) {
        Message msg = Message.obtain();
        msg.obj = text;
        handler.sendMessage(msg);
    }

    private void cancle() {
        Network.getNetworkInstance().getLiveApi()
                .cancelRoomRound(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResultReturn<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscription = d;
                    }

                    @Override
                    public void onSuccess(ResultReturn<String> stringResultReturn) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void startXiazhu() {
        subscription = Network.getNetworkInstance().getLiveApi()
                .generate(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> stringResultReturn) throws Exception {
                        tips(stringResultReturn.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tips(throwable.getMessage());
                    }
                });
    }

    public void onStartXiazhu(View view) {
        startXiazhu();
    }

    @Override
    public void onNotify(MQBean eventData) {
        super.onNotify(eventData);
        show(eventData.toString());
    }

    public void onRegisterMQTT(View view) {
        String topix = title_topic.getText().toString();
        if (TextUtils.isEmpty(topix))
            return;
        subscribeMqTopic(topix);
        view.setEnabled(false);
    }

    public void onCancel(View view) {
        cancle();
    }

    public void onCalcResult(View view) {
        int value1 = 0;
        int value2 = 0;
        int value3 = 0;
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;

        int position1 = Integer.valueOf(value1);
        int position2 = Integer.valueOf(value2);
        int position3 = Integer.valueOf(value3);
        int position4 = Integer.valueOf(value4);
        int position5 = Integer.valueOf(value5);
        int position6 = Integer.valueOf(value6);

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("position", 1);
            jsonObject1.put("rank", position1);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("position", 2);
            jsonObject2.put("rank", position2);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("position", 3);
            jsonObject3.put("rank", position3);

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("position", 4);
            jsonObject4.put("rank", position4);

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("position", 5);
            jsonObject5.put("rank", position5);

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("position", 6);
            jsonObject6.put("rank", position6);

            jsonArray.put(0, jsonObject1);
            jsonArray.put(1, jsonObject2);
            jsonArray.put(2, jsonObject3);
            jsonArray.put(3, jsonObject4);
            jsonArray.put(4, jsonObject5);
            jsonArray.put(5, jsonObject6);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonArray.toString();
        RequestBody rsbody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json);
        subscription = Network.getNetworkInstance().getLiveApi()
                .settleRound(token, rsbody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> stringResultReturn) throws Exception {
                        if (stringResultReturn != null && stringResultReturn.getCode() == 0) {
                            tips("上报成功");
                        } else {
                            tips("上报失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tips("上报失败");
                        tips("onSettle======>" + throwable.getMessage());
                    }
                });
    }
}
