package cn.hotpot.Test;

import java.lang.reflect.Proxy;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
public class App2 {
    public static void main(String[] args) {
        Service o = (Service) Proxy.newProxyInstance(ServiceImpl.class.getClassLoader(),
                new Class<?>[]{Service.class},
                new MyInvocationHandler(ServiceImpl.class));
        System.out.println(o.getName());
    }
}
