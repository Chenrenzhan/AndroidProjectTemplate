package com.drumge.template.view.basic;

/**
 * Created by qiushunming on 16/1/11.
 */
public final class ELModudleConfig {

    public static final int TOP_VIEW_GROUP = 0;
    public static final int BOTTOM_VIEW_GROUP = 1;
//    public static final int TOP_WINDOW_VIEW_GROUP = 2;

    public static final String BASE_URI = "com.yy.live.module.";

    //module 类名
    public static final String MODULE_NAME_VOTE = "vote.VoteModule";
    public static final String MODULE_NAME_NOBLE_SEAT = "nobleSeat.NobleSeatModule";
    public static final String MODULE_NAME_GIFT = "giftmodule.GiftModule";
    public static final String MODULE_NAME_STREAMER = "giftstreamer.StreamerModule";

    public static final String MODULE_NAME_ATMOSPHERE = "atmosphere.AtmosphereModule";
//    public static final String MODULE_NAME_HONOURBARRAGE = "honourbarrage.HonourBarrageModule";
    public static final String MODULE_NAME_PRIVILEGE = "privilege.PrivilegeModule";

    //public static final String MODULE_NAME_DiamondBroad = "DiamondBroadcastModule.ui.DiamondBroadcastViewController";
    public static final String MODULE_NAME_DiamondBroad = "DiamondBroadcastModule.ui.FullSeviceBoradCastModule";
    public static final String MODULE_NAME_GiftComble = "GiftComboButtonModule.GiftCombleController";
    public static final String MODULE_NAME_WeekCard="weekcard.WeekCardModule";

    public static final String MODULE_NAME_NobleMall  ="noblemall.NobleMall";
    public static final String MODULE_NAME_REVENUE = "revenue.RevenueModule";
    public static final String MODULE_NAME_TEMPBROADCAST="tempbroadcast.AllBroadcastModule";
    public static final String MODULE_NAME_WEBACTIVITY = "webactivity.WebActivityModule";
    public static final String MODULE_NAME_GiftDanMu  = "giftdanmu.GiftDanMu"   ;

    public static final String MODULE_NAME_TRUATURE="treasure.TreasureModule";
//    public static final String MODULE_NAME_EXTEND_BUSINESS="extendbusiness.ExtendBusinessModule";
    public static final String MODULE_NAME_ANCHORINFOCARD="anchorInfoCard.AnchorInfoCardModule";
    //public static final String MODULE_NAME_TURNTABLE="turntable.TurnTableModule";
    public static final String MODULE_NAME_TURNTABLE="turntable.NewTurnTableModule";
    public static final String MODULE_NAME_SEND_HEARD = "heart.SendHeartModule";

    public static final String MODULE_NAME_RICH_TOP = "richtop.RichTopModule";

   public static final String MODULE_NAME_CHAT="chatmodule.ChatModule";

    public static final String MODULE_NAME_NOBLE="noble.NobleModule";

    public static final String MODELE_SHARE = "sharemodule.ShareModule";

    public static final String MODELE_GiftStreamLightModule = "streamlight.GiftStreamLightModule";

    public static final String MODELE_BigGiftEffectModule = "BigGiftEffectModule.BigGiftEffectModule";
    
    public static final String MODULE_ACT_MEDAL = "actMedal.ActMedalModule";

    public static final String MODULE_WHISPER_MODULE ="whisperModule.WhisperModule";
    public static final String MODULE_EXTEND_LAYOUT ="extend.ExtendLayoutModule";

    public static final String MODULE_NAME_HOT_BALL ="heatball.HotBallModule";
    public static final String MODULE_NAME_MAGIC_HAT ="magichat.MagicHatModule";
    public static final String MODULE_LUCKY_LIST="luckylist.LuckyListModule";
    public static final String MODULE_CAVALIER ="cavalier.CavalierModule";

    public static final String MODULE_STARTASK = "startask.StarTaskModule";


    public static final String MODULE_NAME_THIRDDATAREPORT = "webactivity.ThirdDataReportModule";
    public static final String MODULE_NAME_COMMON_TIP = "commontip.CommonTipModule";

    public static ELModudleConfig.Modudles elModules;

    public static class Modudles {

        public String[] names = {  //注意这里代表了视图的层级关系,不要随便加在后面....
//                MODELE_SHARE,             //分享模块放到了liveCompnent实例化的时候创建
                MODULE_NAME_THIRDDATAREPORT,
                MODULE_NAME_ANCHORINFOCARD,
                MODULE_NAME_RICH_TOP,
//                MODULE_NAME_HONOURBARRAGE,
                MODULE_NAME_WeekCard,
                MODULE_NAME_TRUATURE,
//                MODULE_NAME_EXTEND_BUSINESS,
//                MODULE_NAME_STREAMER,
                MODELE_BigGiftEffectModule,
                MODULE_NAME_WEBACTIVITY,
                MODULE_NAME_HOT_BALL,
                MODULE_NAME_MAGIC_HAT,
                MODULE_LUCKY_LIST,
                MODULE_EXTEND_LAYOUT,
                MODULE_NAME_COMMON_TIP,
                MODELE_GiftStreamLightModule,
//                MODULE_CAVALIER,
                MODULE_NAME_GiftDanMu,
                MODULE_NAME_GIFT,
                MODULE_NAME_NOBLE,
                MODULE_NAME_NobleMall,
                MODULE_NAME_NOBLE_SEAT,
                MODULE_NAME_DiamondBroad,
                MODULE_NAME_TEMPBROADCAST,
                MODULE_NAME_CHAT,
                MODULE_STARTASK,
//                MODULE_NAME_GiftComble,
                MODULE_NAME_ATMOSPHERE,
//                MODULE_NAME_REVENUE,
                MODULE_NAME_TURNTABLE,
                MODULE_ACT_MEDAL,
                MODULE_NAME_VOTE,
                MODULE_NAME_SEND_HEARD,
                MODULE_NAME_PRIVILEGE,
                MODULE_WHISPER_MODULE
        };
    }

}
