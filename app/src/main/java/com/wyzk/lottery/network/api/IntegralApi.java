package com.wyzk.lottery.network.api;

import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.model.AccountBean;
import com.wyzk.lottery.model.BankBean;
import com.wyzk.lottery.model.ChargeModel;
import com.wyzk.lottery.model.ExchangeModel;
import com.wyzk.lottery.model.RechargeManageModel;
import com.wyzk.lottery.model.ResultReturn;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IntegralApi {

    @GET(UrlContainer.GET_BANK)
    Observable<ResultReturn<BankBean>> getBank(@Query("token") String token, @Query("type") String type);

    @FormUrlEncoded
    @POST(UrlContainer.ADD_CHARGE)
    Observable<ResultReturn<String>> addCharge(@Field("token") String token, @Field("chargeValue") String chargeValue,
                                               @Field("userManagerPayAccountId") String userManagerPayAccountId, @Field("chargeType") String chargeType,
                                               @Field("remarkCode") String remarkCode);

    @GET(UrlContainer.ADD_ACCOUNT)
    Observable<ResultReturn<String>> addAccount(@Query("token") String token, @Query("userPayAccountBank") String bankName,
                                                @Query("userPayAccount") String cardNum, @Query("userPayAccountName") String cardName, @Query("userPayAccountType") String type);

    @GET(UrlContainer.GET_ACCOUNT_LIST)
    Observable<ResultReturn<List<AccountBean>>> getAccountList(@Query("token") String token);

    @FormUrlEncoded
    @POST(UrlContainer.WITHDRAWAL)
    Observable<ResultReturn<String>> withdrawal(@Field("token") String token, @Field("exchangeValue") String exchangeValue, @Field("userPayAccountId") String userPayAccountId);

    @GET(UrlContainer.GET_CHARGE_HISTORY_LIST)
    Observable<ResultReturn<ChargeModel>> getChargeHistory(@Query("token") String token, @Query("page") int page, @Query("rows") int rows);

    @GET(UrlContainer.GET_INTEGRAL_EXCHANGE_HISTORY)
    Observable<ResultReturn<ExchangeModel>> getExchangeHistory(@Query("token") String token, @Query("page") int page, @Query("rows") int rows);


    @GET(UrlContainer.GET_CHARGE_LIST)
    Observable<ResultReturn<RechargeManageModel>> getRechargeRecord(@Query("token") String token,
                                                      @Query("page") int page,
                                                      @Query("rows") int rows,
                                                      @Query("chargeStatus") int chargeStatus);

    @GET(UrlContainer.GET_INTEGRAL_EXCHANGE_LIST)
    Observable<ResultReturn<ExchangeModel>> getExchangeList(@Query("token") String token, @Query("page") int page, @Query("rows") int rows,@Query("username") String username);


    @FormUrlEncoded
    @POST(UrlContainer.FINISH_CHARGE)
    Observable<ResultReturn<String>> setRechargeFinish(@Field("token") String token, @Field("chargeId") String chargeId);

    @FormUrlEncoded
    @POST(UrlContainer.REJECT_CHARGE)
    Observable<ResultReturn<String>> setRechargeReject(@Field("token") String token, @Field("chargeId") String chargeId);

    @FormUrlEncoded
    @POST(UrlContainer.EXCHANGE_FINISH)
    Observable<ResultReturn<String>> setExchangeFinish(@Field("token") String token, @Field("exchangeId") String chargeId);

    @FormUrlEncoded
    @POST(UrlContainer.EXCHANGE_REJECT)
    Observable<ResultReturn<String>> setExchangeReject(@Field("token") String token, @Field("exchangeId") String chargeId);

//            @GET(UrlContainer.GET_INTEGRAL_LIST)
//            Observable<ResultReturn<IntegralModel>>getList(@Query("page")int page, @Query("rows")int rows);


//    @GET(UrlContainer.EXCHANGE_ADD)
//    Observable<ResultReturn> exchangeAdd(@Query("exchangeValueQUERY") int exchangeValueQUERY, @Query("userPayAccountIdQUERY") int userPayAccountIdQUERY);
//
//    @GET(UrlContainer.CHARGE_ADD)
//    Observable<ResultReturn> chargeAdd(@Query("chargeValueQUERY") int chargeValueQUERY, @Query("userManagerPayAccountIdQUERY") int userManagerPayAccountIdQUERY, @Query("chargeTypeQUERY") String chargeTypeQUERY, @Query("remarkCode") String remarkCode);
}
