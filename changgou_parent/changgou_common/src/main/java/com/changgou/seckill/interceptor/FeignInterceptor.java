package com.changgou.seckill.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Ykj
 * @ClassName FeignInterceptor
 * @Discription
 * @date 2021/12/13 9:22
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemlate) {
        //传递令牌
        RequestAttributes requestAttributes=RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request!=null) {
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()){
                    String headerName=headerNames.nextElement();
                    if ("authorization".equals(headerName)){
                        String headerValue = request.getHeader(headerName);  //Bearer jwt token

                        //传递令牌
                        requestTemlate.header(headerName,headerValue);
                    }
                }
            }
        }
    }
}
