package com.wyzk.lottery.network;

import com.fsix.mqtt.util.Logc;
import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.network.api.IntegralApi;
import com.wyzk.lottery.network.api.LiveApi;
import com.wyzk.lottery.network.api.UserApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    public final static String TAG = "NETWORK_TAG:";
    private static Network network;
    private UserApi userApi;
    private LiveApi liveApi;
    private IntegralApi integralApi;
    private OkHttpClient okHttpClient;
    private Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
    private Retrofit mRetrofit;

    public Network() {
        if (okHttpClient == null) {
            synchronized (Network.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new LoggingInterceptor()).build();
                }
            }
        }

        if (mRetrofit == null) {
            mRetrofit = createRetrofit();
        }
    }

    public static Network getNetworkInstance() {
        if (network == null) {
            synchronized (Network.class) {
                if (network == null) {
                    network = new Network();
                }
            }
        }
        return network;
    }

    private void printRequest(Request request) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.url());
        if ("POST".equals(request.method())) {
            stringBuilder.append("?");
            RequestBody body = request.body();
            if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                int length = formBody.size();
                for (int i = 0; i < length; i++) {
                    try {
                        stringBuilder.append(formBody.encodedName(i) + "=" + URLDecoder.decode(formBody.encodedValue(i), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (i != length - 1) {

                        stringBuilder.append("&");
                    }
                }
            } else {
            }
        }
        Logc.i(TAG + stringBuilder.toString());
    }

    private void printResponse(Response response) throws IOException {
        ResponseBody body = response.body();
        BufferedSource bufferedSource = body.source();
        bufferedSource.request(Long.MAX_VALUE);
        Buffer buffer = bufferedSource.buffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"));
        }
        String bodyString = buffer.clone().readString(charset);
        Logc.i(TAG + bodyString);
    }

    /**
     * 创建相应的服务接口
     */
    public <T> T create(Class<T> service) {
        checkNotNull(service, "service is null");
        return mRetrofit.create(service);
    }

    private Retrofit createRetrofit() {
        //初始化OkHttp
        // 返回 Retrofit 对象
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(UrlContainer.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    private <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public UserApi getUserApi() {
        if (userApi == null) {
            return Network.getNetworkInstance().create(UserApi.class);
        }
        return userApi;
    }

    public LiveApi getLiveApi() {
        if (liveApi == null) {
            return Network.getNetworkInstance().create(LiveApi.class);
        }
        return liveApi;
    }

    public IntegralApi getIntegralApi() {
        if (integralApi == null) {
            return Network.getNetworkInstance().create(IntegralApi.class);
        }
        return integralApi;
    }

    class LoggingInterceptor implements Interceptor {
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            printRequest(request);
            Response response = chain.proceed(request);
            printResponse(response);
            return response;
        }
    }
}
