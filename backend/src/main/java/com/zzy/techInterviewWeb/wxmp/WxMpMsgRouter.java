package com.zzy.techInterviewWeb.wxmp;

import com.zzy.techInterviewWeb.wxmp.handler.EventHandler;
import com.zzy.techInterviewWeb.wxmp.handler.MessageHandler;
import com.zzy.techInterviewWeb.wxmp.handler.SubscribeHandler;
 
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号路由
 *
 * 
 * 
 */
@Configuration
public class WxMpMsgRouter {

    @Autowired(required = false)
    private WxMpService wxMpService;

    @Autowired(required = false)
    private EventHandler eventHandler;

    @Autowired(required = false)
    private MessageHandler messageHandler;

    @Autowired(required = false)
    private SubscribeHandler subscribeHandler;

    @Bean
    public WxMpMessageRouter getWxMsgRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 消息
        router.rule()
                .async(false)
                .msgType(XmlMsgType.TEXT)
                .handler(messageHandler)
                .end();
        // 关注
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE)
                .handler(subscribeHandler)
                .end();
        // 点击按钮
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.CLICK)
                .eventKey(WxMpConstant.CLICK_MENU_KEY)
                .handler(eventHandler)
                .end();
        return router;
    }
}
