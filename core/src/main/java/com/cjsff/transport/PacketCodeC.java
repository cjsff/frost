package com.cjsff.transport;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * protocol format:
 * <p>
 * | magic_number | protocol_version | serialization_algorithm | packet_type |data_length | data |
 * |      4       |         1        |            1            |      1      |     4      |      |
 * <p>
 *
 * @author rick
 */
public class PacketCodeC {

  public static final int MAGIC_NUMBER = 0x12345678;

  private static final byte REQUEST_PACKET_TYPE = 1;

  private static final byte RESPONSE_PACKET_TYPE = 2;

  public static final PacketCodeC INSTANCE = new PacketCodeC();

  private final Map<Byte, Serialization> serializationMap;

  private final Map<Byte, Class<? extends Packet>> packetTypeMap;

  private PacketCodeC() {
    packetTypeMap = new HashMap<>();
    packetTypeMap.put(REQUEST_PACKET_TYPE, FrpcRequest.class);
    packetTypeMap.put(RESPONSE_PACKET_TYPE, FrpcResponse.class);

    serializationMap = new HashMap<>();
    Serialization serialization = new JsonSerializer();
    serializationMap.put(serialization.getSerializationAlgorithm(), serialization);
  }

  public void encode(ByteBuf byteBuf, Packet packet) {
    byte packetType = -1;
    if (packet instanceof FrpcRequest) {
      packetType = REQUEST_PACKET_TYPE;
    } else if (packet instanceof FrpcResponse) {
      packetType = RESPONSE_PACKET_TYPE;
    }

    byteBuf.writeInt(MAGIC_NUMBER);
    byteBuf.writeByte(packet.getVersion());
    byteBuf.writeByte(SerializationConstant.JSON);
    byteBuf.writeByte(packetType);

    // serialization java object
    byte[] bytes = serializationMap.get(SerializationConstant.JSON).serialize(packet);
    byteBuf.writeInt(bytes.length);
    byteBuf.writeBytes(bytes);
  }


  public Packet decode(ByteBuf byteBuf) {
    // 跳过 magic number
    byteBuf.skipBytes(4);

    // 跳过版本号
    byteBuf.skipBytes(1);

    // 序列化算法
    byte serializeAlgorithm = byteBuf.readByte();

    // packet type
    byte packetType = byteBuf.readByte();

    // 数据包长度
    int length = byteBuf.readInt();

    byte[] bytes = new byte[length];
    byteBuf.readBytes(bytes);

    Serialization serialization = serializationMap.get(serializeAlgorithm);

    if (serialization != null) {
      return serialization.deserialize(bytes,packetTypeMap.get(packetType));
    }

    return null;
  }

}
