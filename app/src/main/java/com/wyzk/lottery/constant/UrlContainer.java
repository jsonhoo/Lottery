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

    String GET_ONLINE_COUNT = "/game/user/getRoomStateByRoomId";//获取房间人数

    //积分模块
    String GET_BANK = "/game/integral/charge/bank";//获取管理员充值卡号
    String ADD_CHARGE = "/game/integral/charge/add";//申请充值
    String ADD_ACCOUNT = "/game/integral/exchange/account/add";//新增提现账号
    String GET_ACCOUNT_LIST = "/game/integral/exchange/account/list";//获取提现账号列表
    String WITHDRAWAL = "/game/integral/exchange/add";//提现申请
    String GET_CHARGE_HISTORY_LIST = "/game/integral/charge/history";//我的充值记录
    String GET_INTEGRAL_LIST = "/game/integral/pageList";//积分列表

    String GET_INTEGRAL_EXCHANGE_HISTORY = "/game/integral/exchange/history";//提现记录

    String GET_INTEGRAL_EXCHANGE_LIST="/manager/game/integral/exchange/list";//提现待审批列表

    String GET_CHARGE_LIST = "/manager/game/integral/charge/list"; //充值待审批列表

    String FINISH_CHARGE = "/manager/game/integral/charge/reviewFinish";//充值审核通过
    String REJECT_CHARGE = "/manager/game/integral/charge/reviewReject";//充值审核驳回
    String EXCHANGE_FINISH = "/manager/game/integral/exchange/reviewFinish"; //提现审核通过
    String EXCHANGE_REJECT = "/manager/game/integral/exchange/reviewReject";//提现审核驳回

    String UP_POKE = "/game/round/baccarat/uploadCard"; //牌上传
}
