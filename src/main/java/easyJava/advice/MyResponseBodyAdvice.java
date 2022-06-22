package easyJava.advice;

import com.alibaba.fastjson.JSON;
import easyJava.utils.AESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {
    private static final Logger logger = LoggerFactory.getLogger(MyResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //形如：/love/user/test2
        String requestPath = request.getURI().getPath();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession httpSession = httpServletRequest.getSession(true);
        var headers = request.getHeaders();
        var encrypt = headers.get("encrypt");
        if (encrypt != null) {
            logger.info("requestPath:" + requestPath + "encrypt:" + encrypt);
            String bodyStr = JSON.toJSONString(body);
            body = AESUtils.encryptIntoHexString(bodyStr, encrypt.get(0));
        } else {
            logger.info("requestPath:" + requestPath + "encrypt:" + encrypt);
        }
        return body;
    }
}