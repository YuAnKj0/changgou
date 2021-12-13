package com.changgou.web.gateway.filter;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 18:02 2021/12/11
 * @Modified By:
 */
public class UrlFilter {

    public static String filterPath="/api/worder/**,/api/wseckillorder,/api/seckill,/api/wxpay,/api/wxpay/**,/api/worder/**,/api/user/**,/api/address/**,/api/wcart/**,/api/cart/**,/api/categoryReport/**,/api/orderConfig/**,/api/order/**,/api/orderItem/**,/api/orderLog/**,/api/preferential/**,/api/returnCause/**,/api/returnOrder/**,/api/returnOrderItem/**";

    public static boolean hasAuthorized(String url){
        String[] split = filterPath.replace("**", "").split(",");
        for (String value : split) {
            if (url.startsWith(value)) {
                return true; //代表当前访问地址需要传递令牌
            }

        }
        return false; //不需传递令牌

    }

}
