package com.wyzk.lottery.network.api;

import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.model.ChargeHistoryModel;
import com.wyzk.lottery.model.IntegralModel;
import com.wyzk.lottery.model.ResultReturn;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-08-02<br>
 * 描述: 充值接口
 **/
public interface ChargeApi {

    @GET(UrlContainer.GET_INTEGRAL_LIST)
    Observable<ResultReturn<IntegralModel>> getList(@Query("page") int page, @Query("rows") int rows);

    @GET(UrlContainer.GET_CHARGE_HISTORY_LIST)
    Observable<ResultReturn<ChargeHistoryModel>> getChargeHistory(@Query("page") int page, @Query("rows") int rows);

    @GET(UrlContainer.EXCHANGE_ADD)
    Observable<ResultReturn> exchangeAdd(@Query("exchangeValueQUERY") int exchangeValueQUERY, @Query("userPayAccountIdQUERY") int userPayAccountIdQUERY);

    @GET(UrlContainer.CHARGE_ADD)
    Observable<ResultReturn> chargeAdd(@Query("chargeValueQUERY") int chargeValueQUERY, @Query("userManagerPayAccountIdQUERY") int userManagerPayAccountIdQUERY, @Query("chargeTypeQUERY") String chargeTypeQUERY, @Query("remarkCode") String remarkCode);


}
