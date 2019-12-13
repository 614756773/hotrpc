package cn.hotpot.rpc.client.config;

import cn.hotpot.rpc.common.netty.client.Client;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author qinzhu
 * @since 2019/12/11
 */
@Slf4j
public class ProxyFactory {

    private static final String HOST = "127.0.0.1";

    private static final Integer PORT = 8864;

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new CustomizeInvocationHandler());
    }

    private static class CustomizeInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Request request = new Request()
                    .setId(UUID.randomUUID().toString())
                    .setClassName(method.getDeclaringClass().getName())
                    .setMethodName(method.getName())
                    .setParams(args)
                    .setParamTypes(method.getParameterTypes());
            Response response = new Client(HOST, PORT).send(request);
            Throwable exp = response.getThrowable();
            if (exp != null) {
                throw exp;
            }
            return response.getResult();
        }
    }
}
