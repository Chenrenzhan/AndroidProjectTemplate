package com.drumge.template.view.component;

import com.yy.mobile.util.DontProguardClass;

/**
 * Class Name:BasicChannelComponentContainer
 * Description:直播间模板化组件配置类
 * Author:zengyan
 * Date:2016/1/5
 * Modified History:
 */
@DontProguardClass
public class ComponetConfig {
    public String componentId = "";//组件id
    public IComponent component;//组件类
    public String tag = "";//用于设置组件的tag
    public String description = "";//组件描述

    @Override
    public String toString() {
        return String.format("componentId = %s, description = %s",componentId,description);
    }
}
