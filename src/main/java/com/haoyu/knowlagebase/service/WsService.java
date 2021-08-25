package com.haoyu.knowlagebase.service;

import com.haoyu.knowlagebase.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/*
 * @author haoyu
 * @date 2021/8/25 17:57
 */
/*
做这样一个类为了让异步化生效 因为@Async必须在另外一个类中才能生效
 */
@Service
public class WsService {
    @Resource
    public WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message, String logId) {
        MDC.put("LOG_ID", logId);
        webSocketServer.sendInfo(message);
    }
}
