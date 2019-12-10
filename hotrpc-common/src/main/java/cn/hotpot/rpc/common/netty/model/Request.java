package cn.hotpot.rpc.common.netty.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
@Data
@Accessors(chain = true)
public class Request {

    private String id;

    private String className;

    private String methodName;

    private Class<?>[] paramTypes;

    private Object[] params;
}
