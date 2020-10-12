package com.cjsff.transport;


import lombok.*;

/**
 * @author rick
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@ToString
public class FrpcRequest extends BasePacket {
    private String id;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;
}
