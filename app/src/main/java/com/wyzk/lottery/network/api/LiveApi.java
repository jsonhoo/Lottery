package com.wyzk.lottery.network.api;

import com.wyzk.lottery.constant.UrlContainer;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.model.RoundInfoModel;
import com.wyzk.lottery.model.WinLoseModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LiveApi {
    @FormUrlEncoded
    @POST(UrlContainer.GET_ROOM_LIST)
    Observable<RoomModel> getRoomList(@Field("token") String token, @Field("pageIndex") int pageIndex, @Field("pageRows") int pageRows);

    /**
     * 用户下注
     *
     * @param token
     * @param roomId
     * @param postionId
     * @param roundId
     * @param betValue
     * @return
     */
    @FormUrlEncoded
    @POST(UrlContainer.BET)
    Observable<ResultReturn<String>> bet(@Field("token") String token, @Field("roomId") int roomId, @Field("positionId") int postionId, @Field("roundId") int roundId, @Field("betValue") int betValue);

    /**
     * 生成场次
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(UrlContainer.GENERATE)
    Observable<ResultReturn<String>> generate(@Field("token") String token);

    @FormUrlEncoded
    @POST(UrlContainer.GETLASTROOMROUND)
    Observable<ResultReturn<RoomRoundModel>> getLastRoomRound(@Field("token") String token, @Field("roomId") int roomId);

    @FormUrlEncoded
    @POST(UrlContainer.CANCEL_ROOMROUND)
    Single<ResultReturn<String>> cancelRoomRound(@Field("token") String token);

    @FormUrlEncoded
    @POST(UrlContainer.ROUND_SETTLE)
    Observable<ResultReturn<String>> settleRound2(@FieldMap(encoded = true) Map<String, String> map);

    @Headers("Content-Type: application/json")
    @POST(UrlContainer.ROUND_SETTLE)
    Observable<ResultReturn<String>> settleRound(@Query("token") String token, @Body RequestBody array);

    @FormUrlEncoded
    @POST(UrlContainer.ROUND_LICENSING)
    Observable<ResultReturn<String>> licensing(@Field("token") String token);//封盘

    @FormUrlEncoded
    @POST(UrlContainer.TREND)
    Observable<ResultReturn<List<RoundInfoModel>>> getTrend(@Field("token") String token, @Field("roomId") int roomId, @Field("gameId") int gameId);//封盘

    @FormUrlEncoded
    @POST(UrlContainer.GET_USER_WIN_LOSE)
    Observable<ResultReturn<WinLoseModel>> getWinLoseByRoundId(@Field("token") String token, @Field("roundId") int roundId);

    @FormUrlEncoded
    @POST(UrlContainer.UPDATE_ROOM_PERCENTAGE)
    Observable<ResultReturn<String>> updateRoomPercentage(@Field("token") String token, @Field("roomId") String roomId, @Field("percentage") int percentage);

    @FormUrlEncoded
    @POST(UrlContainer.MYROOM)
    Observable<ResultReturn<RoomModel.RowModel>> myRoom(@Field("token") String token);


    @FormUrlEncoded
    @POST(UrlContainer.GET_ONLINE_COUNT)
    Observable<ResultReturn<Integer>> getOnlineCount(@Field("token") String token,@Field("roomId") int roomId);
}
