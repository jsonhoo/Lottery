package com.wyzk.lottery.network.api;

import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.model.BankBean;
import com.wyzk.lottery.model.ResultReturn;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IntegralApi {

    @FormUrlEncoded
    @POST(UrlContainer.GET_BANK)
    Observable<ResultReturn<BankBean>> getBank(@Field("token") String token);

    @FormUrlEncoded
    @POST(UrlContainer.ADD)
    Observable<ResultReturn<String>> add(@Field("token") String token, @Field("chargeValue") String chargeValue,
                                         @Field("userManagerPayAccountId") String userManagerPayAccountId, @Field("chargeType") String chargeType,
                                         @Field("remarkCode") String remarkCode);
}
