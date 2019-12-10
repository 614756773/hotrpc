package cn.hotpot.rpc.common.netty.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
@Data
@Accessors(chain = true)
public class Response {
    private String id;
    private Throwable throwable;
    private Object result;
}
