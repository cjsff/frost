package com.cjsff.transport;

import lombok.Data;


/**
 * @author rick
 */
@Data
public abstract class Packet {

  /**
   * protocol version
   */
  private Byte version = 1;


}
