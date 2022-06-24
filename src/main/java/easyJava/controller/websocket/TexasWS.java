package easyJava.controller.websocket;

import com.alibaba.fastjson.JSON;
import easyJava.entity.BaseEntity;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 德州入口
 *
 * @author lxr
 */
@ServerEndpoint("/ws/texas")
@Component
public class TexasWS {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TexasWS.class);
    // 缓冲区最大大小
    static final int maxSize = 256;// 1 * 1024;// 1K

    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<Session> sessionList = new CopyOnWriteArrayList<Session>();

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        logger.info("onMessage:" + message);
        // onMessageDoReflect(message, session);
        onMessageDo(message, session);
    }

    public void onMessageDo(String message, Session session) {
        sessionMap.put(JSON.parseObject(message, BaseEntity.class).getId(), session);
    }


    @OnOpen
    public void onOpen(Session session) {
        logger.info("onOpen");
        // 可以缓冲的传入二进制消息的最大长度
        session.setMaxBinaryMessageBufferSize(maxSize);
        // 可以缓冲的传入文本消息的最大长度
        session.setMaxTextMessageBufferSize(maxSize);
        sessionList.add(session);

    }

    @OnClose
    public void onClose(Session session) {
        onConnectLost(session);
        logger.info(" connection closed ");
    }

    @OnError
    public void onError(Session session, Throwable e) {
        onConnectLost(session);
        logger.info(" connection error: " + e.getMessage());
    }

    public void onConnectLost(Session session) {
    }

    public static void sendToAllText(String text) {
        logger.info("sendToAllText size:" + sessionList.size() + ",txt:" + text);
        sessionList.forEach(session -> {
            if (session == null) {
                return;
            }
            synchronized (session) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(text);
                    } catch (IOException e) {
                        logger.error("sendToAllText error:", e);
                    }
                }
            }
        });
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i) == null || !sessionList.get(i).isOpen()) {
                logger.error("session remove :" + i);
                sessionList.remove(i);
                break;
            }
        }
    }
    public static void sendToAllObject(Object obj) {
        logger.info("sendToAllText size:" + sessionList.size() + ",txt:" + obj);
        sessionList.forEach(session -> {
            if (session == null) {
                return;
            }
            synchronized (session) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendObject(obj);
                    } catch (IOException | EncodeException e) {
                        logger.error("sendToAllText error:", e);
                    }
                }
            }
        });
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i) == null || !sessionList.get(i).isOpen()) {
                logger.error("session remove :" + i);
                sessionList.remove(i);
                break;
            }
        }
    }

    /**
     * 发送文本消息
     *
     * @param session
     * @param text
     */
    public static void sendText(Session session, String text) {
        if (session == null) {
            return;
        }
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(text);
                    // logger.info(text);
                } catch (IOException e) {
                    logger.error("sendText error:", e);
                }
            }
        }
    }

}
