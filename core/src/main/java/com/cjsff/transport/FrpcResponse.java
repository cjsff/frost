package com.cjsff.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author rick
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class FrpcResponse<T> extends BasePacket {
  private String id;
  private T result;
  private String error;
}
