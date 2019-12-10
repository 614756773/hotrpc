package cn.hotpot.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
public class MyInvocationHandler implements InvocationHandler {

    private Object object;

    public MyInvocationHandler(Class aClass) {
        try {
            this.object = aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(this.object);
        return method.invoke(this.object, args);
    }
}
