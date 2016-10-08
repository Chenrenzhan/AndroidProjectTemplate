package com.drumge.template.view.component;

import com.yy.mobile.ui.audience.OnlineAudienceComponent;
import com.yy.mobile.ui.basicfunction.SlideFunctionsComponent;
import com.yy.mobile.ui.basicgunview.BasicDanmuComponent;
import com.yy.mobile.ui.chatemotion.ChatEmotionComponent;
import com.yy.mobile.ui.likelamp.LikeLampComponent;
import com.yy.mobile.ui.profile.InteractiveExpandComponent;
import com.yy.mobile.ui.programinfo.OfficialProgramInfoComponent;
import com.yy.mobile.ui.programinfo.ProgramInfoFragment;
import com.yy.mobile.util.DontProguardClass;

/**
 * Class Name:BasicChannelComponentContainer
 * Description:用于定义一些组件ID 的常量类 （注意不要随意修改ID）
 * Author:zengyan
 * Date:2015/12/24
 * Modified History:
 */
@DontProguardClass
public class ComponentConst {
    //======================poupupComponent ID 无需定义布局的 用于代替布局的ID start ====================================//
    public static final int MIC_ORDER_COMPONENT_ID = 0;//控麦模式

    public static final int PRIVATE_CHAT_COMPONENT_ID = 1;//私聊

//    public static final int CHAT_EMOTION_COMPONENT_ID = 2;//发言

    public static final int CHAT_MANAGER_COMPONENT_ID = 3;//弹幕管理

    public static final int LIVE_NOTICE_COMPONENT_ID = 4;//直播通知

    public static final int MIC_COMPONENT_ID = 5;//麦序模式

    public static final int ONLINE_COMPONENT_ID = 6;//在线列表

    //======================poupupComponent ID 无需定义布局的 用于代替布局的ID end ====================================//
    // component 的ID
    public static final String BASIC_LIVE_PROGRAM_COMPONENT_ID = "basic_live_program_component"; //主播节目信息区
    public static final String BASIC_LIVE_FUNCTION_COMPONENT_ID = "basic_live_function_component";//基础功能区
    public static final String BASIC_LIVE_ONLINE_AUDIENCE_COMPONENT_ID = "basic_live_online_audience_component";//在线列表区
    public static final String BASIC_LIVE_CHAT_COMPONENT_ID = "basic_live_chat_component";//弹幕区
    public static final String BASIC_LIVE_VIDEO_COMPONENT_ID = "basic_live_video_component";//视频区域
    public static final String BASIC_LIVE_CONTENT_RECOMMEND_COMPONENT_ID = "basic_live_content_recommend_componet";//内容推荐区域
    public static final String BASIC_LIVE_SEFT_DEFIND_COMPONENT_ID = "basic_live_seft_defind_component";//自定义功能区
    public static final String BASIC_LIVE_MONEY_COMPONENT_ID = "basic_live_money_component"; //营收区
    public static final String BASIC_LIVE_FREE_MIC_COMPONENT_ID = "basic_live_free_mic_component";//自由模式麦序区域
    public static final String BASIC_LIVE_HOST_MIC_COMPONENT_ID = "basic_live_host_mic_component";//主席模式麦序区域
    public static final String BASIC_LIVE_PRIVATE_CHAT_COMPONENT_ID = "basic_live_private_chat_component";//私聊区域
    public static final String BASIC_LIVE_ORDER_MIC_COMPONENT_ID = "basic_live_order_mic_component";//麦序模式麦序区域
    public static final String BASIC_LIVE_MOBILELIVE_COMPONENT_ID = "basic_live_mobilelive_component"; //现场区
    public static final String BASIC_LIVE_CHAT_EMOTION_COMPONENT_ID = "basic_live_chat_emotion_component";//发言区
    public static final String BASIC_LIVE_SECOND_VIDEO_INFO_COMPONENT_ID = "basic_live_second_video_info_component";//双视屏，右边视屏信息区
    public static final String BASIC_LIVE_TOUCH_COMPONENT_ID = "basic_live_touch_component";//触摸组件区域
    public static final String BASIC_LIVE_MOBILELIVE_CHAT_MANAGER_COMPONENT_ID = "basic_live_mobilelive_chat_manager_component"; //现场弹幕管理
    public static final String BASIC_LIVE_LIKE_COMPONENT_ID = "basic_live_like_component"; //点赞区
    public static final String BASIC_LIVE_LIVE_NOTICE_COMPONENT_ID = "basic_live_notice_component";//直播通知列表区域
    public static final String BASIC_LIVE_ANIMATION_CONPONENT_ID = "basic_live_animation_component";//直播通知的气泡
    public static final String BASIC_BASIC_FUNCTION_COMPONENT_ID = "basic_basic_function_component";//基础功能按钮

    public static final String BASIC_MIC_COMPONENT_ID = "basic_mic_component";//麦序模式
    public static final String BASIC_ONLINE_COMPONENT_ID = "basic_online_component";//在线列表

    //========================组件分级列表===============================================================================//
    public static final int COMPONENT_LEVEL_ONE = 1;
    public static final int COMPONENT_LEVEL_TWO = 2;
    public static final int COMPONENT_LEVEL_THREE = 3;
//    //Level 1
//    public static final String PROGRAM_INFO_FRAGMENT = "ProgramInfoFragment"; //节目信息区
//    public static final String ONLINE_AUDIENCE_COMPONENT = "OnlineAudienceComponent";//在线列表
//    public static final String INTERACTIVE_EXPAND_COMPONENT = "InteractiveExpandComponent";//扩展区
//    //Level 2
//    public static final String SLIDE_FUNCTION_COMPONENT = "SlideFunctionsComponent";//滑动菜单区域
//    public static final String CHAT_EMOTION_COMPONENT = "ChatEmotionComponent";//发言组件
//    public static final String LIVE_COMPONENT = "LiveComponent"; //业务区--秀场
//    public static final String MOBILE_LIVE_COMPONENT = "MobileLiveComponent"; //业务区--现场
//    public static final String LIKE_LAMP_COMPONENT = "LikeLampComponent"; //氛围灯
//    //Default Level 3
//    public static final String BASIC_DANMU_COMPONENT = "BasicDanmuComponent";//弹幕等等

    //Level 1
    public static final String PROGRAM_INFO_FRAGMENT = ProgramInfoFragment.class.getSimpleName(); //节目信息区
    public static final String OFFICIAL_PROGRAM_INFO_FRAGMENT = OfficialProgramInfoComponent.class.getSimpleName(); //官网节目信息区
    public static final String ONLINE_AUDIENCE_COMPONENT = OnlineAudienceComponent.class.getSimpleName();//在线列表
    public static final String INTERACTIVE_EXPAND_COMPONENT = InteractiveExpandComponent.class.getSimpleName();//扩展区
    //Level 2
    public static final String CHAT_EMOTION_COMPONENT = ChatEmotionComponent.class.getSimpleName();//发言组件
    public static final String LIVE_COMPONENT = "LiveComponent"; //业务区--秀场
    public static final String MOBILE_LIVE_COMPONENT = "MobileLiveComponent"; //业务区--现场
    public static final String LIKE_LAMP_COMPONENT = LikeLampComponent.class.getSimpleName(); //氛围灯
    public static final String SLIDE_FUNCTION_COMPONENT = SlideFunctionsComponent.class.getSimpleName();//滑动菜单区域

    //Default Level 3
    public static final String BASIC_DANMU_COMPONENT = BasicDanmuComponent.class.getSimpleName();//弹幕等等

    //组件被用到何处
    public static final int COMPONENT_BUSINESS_SIDE_LIVE = 0;//直播间
    public static final int COMPONENT_BUSINESS_SIDE_REPLAY = 1;//回放
}
