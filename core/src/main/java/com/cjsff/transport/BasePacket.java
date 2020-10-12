package com.cjsff.transport;

import lombok.Getter;


/**
 * @author rick
 */
@Getter
public abstract class BasePacket {

  /**
   * protocol version
   */
  private final Byte version = 1;


}
