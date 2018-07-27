package com.wyzk.lottery.network.api;

import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.model.AppUpdateBean;
import com.wyzk.lottery.model.IntegralRecordModel;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.TokenModel;
import com.wyzk.lottery.model.UserInfoModel;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;


public interface UserApi {
    @FormUrlEncoded
    @POST(UrlContainer.LOGIN)
    Observable<ResultReturn<TokenModel>> login(@Field("username") String email, @Field("password") String password, @Field("imei") String imei);

    @FormUrlEncoded
    @POST(UrlContainer.REGISTER)
    Observable<ResultReturn<String>> register(@Field("username") String username, @Field("password") String password, @Field("realname") String realname, @Field("sex") int sex);

    @FormUrlEncoded
    @POST(UrlContainer.GET_USER_INFO)
    Observable<ResultReturn<UserInfoModel>> getUserInfo(@Field("token") String token);

    @FormUrlEncoded
    @POST(UrlContainer.GET_CONSUME_PAGE_LIST)
    Observable<IntegralRecordModel> getConsumePageList(@Field("token") String token, @Field("page") int page, @Field("rows") int rows);

    @GET
    Observable<AppUpdateBean> update(@Url String path);
}
