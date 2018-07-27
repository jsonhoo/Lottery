package com.wyzk.lottery.constant;

/**
 * Created by sushuai on 2016/3/25.
 */
public interface UrlContainer {
    String BASE_URL = "http://120.77.252.48:8888";//阿里云
//    String BASE_URL = "http://103.56.118.154:8888";//香港云

    //用户模块
    String LOGIN = "/game/user/app/login";
    String REGISTER = "/game/user/register";
    String GET_USER_INFO = "/game/user/get";//获取用户信息
    String GET_CONSUME_PAGE_LIST = "/game/integral/consumePageList";//积分模块 / 积分消耗历史

    //直播模块
    String GET_ROOM_LIST = "/game/room/pageList";//获取房间列表
    String BET = "/game/user/bet";//用户下注
    String GENERATE = "/game/round/create";//生成场次
    String GETLASTROOMROUND = "/game/round/getLastRoomRound";//获取房间当前场次
    String CANCEL_ROOMROUND = "/game/round/cancel";//游戏场次作废
    String ROUND_SETTLE = "/game/round/besSettle";//大吃小场次结算
    String ROUND_LICENSING = "/game/round/licensing";//封盘
    String TREND = "/game/round/position";//场次位置输赢信息 趋势
    String GET_USER_WIN_LOSE = "/game/round/getLastUserWinLoseByRoundId";//获取用户盈亏积分
    String UPDATE_ROOM_PERCENTAGE = "/game/room/updateRoomPercentage";//房间修改
    String MYROOM = "/game/room/myRoom";//房间信息
}