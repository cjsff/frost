package com.cjsff.transport;

import com.cjsff.serialization.Serialization;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * protocol format:
 * <p>
 * | magic_number | protocol_version | packet_type |data_length |     data        |
 * |      4       |         1        |      1      |     4      |  data_length    |
 * <p>
 *
 * @author rick
 */
public class PacketCodeC {

  public static final int MAGIC_NUMBER = 0X9527;

  private static final byte REQUEST_PACKET_TYPE = 1;

  private static final byte RESPONSE_PACKET_TYPE = 2;

  public static final PacketCodeC INSTANCE = new PacketCodeC();

  private final Map<Byte, Class<? extends BasePacket>> packetTypeMap;

  private PacketCodeC() {
    packetTypeMap = new HashMap<>();
    packetTypeMap.put(REQUEST_PACKET_TYPE, FrpcRequest.class);
    packetTypeMap.put(RESPONSE_PACKET_TYPE, FrpcResponse.class);
  }

  public void encode(ByteBuf byteBuf, BasePacket packet) {
    byte packetType = -1;
    if (packet instanceof FrpcRequest) {
      packetType = REQUEST_PACKET_TYPE;
    } else if (packet instanceof FrpcResponse) {
      packetType = RESPONSE_PACKET_TYPE;
    }

    byteBuf.writeInt(MAGIC_NUMBER);
    byteBuf.writeByte(packet.getVersion());

    SerializationManager serializationManager = SerializationManager.getInstance();

    byteBuf.writeByte(packetType);

    byte[] bytes = serializationManager.getSerialization().serialize(packet);
    byteBuf.writeInt(bytes.length);
    byteBuf.writeBytes(bytes);
  }


  public BasePacket decode(ByteBuf byteBuf) {
    // skip magic number
    byteBuf.skipBytes(4);

    // skip version number
    byteBuf.skipBytes(1);

    // packet type
    byte packetType = byteBuf.readByte();

    // packet length
    int length = byteBuf.readInt();

    byte[] bytes = new byte[length];
    byteBuf.readBytes(bytes);

    SerializationManager serializationManager = SerializationManager.getInstance();

    Serialization serialization = serializationManager.getSerialization();

    if (serialization != null) {
      return serialization.deserialize(bytes, packetTypeMap.get(packetType));
    }

    return null;
  }

}
