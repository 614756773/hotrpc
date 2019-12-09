package cn.hotpot.rpc.common.protocol;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2019/12/9
 *
 * 传输数据的pojo
 */
@Data
@Accessors(chain = true)
public class Message {
    /**
     * 参考jdk做一个魔数进行数据校验，嘿嘿
     */
    private int magicNumber;

    private int dateLength;

    private byte[] data;

}
