package com.example.l.blewatch.ble;


import com.example.l.blewatch.utils.UUIDUtil;

import java.util.UUID;

public class BLEParameters
{
  public static final String BLE_UNKNOWN_SERVICE_UUID = UUIDUtil.UUID_16bit_128bit("03c1", true);
  // 通知 UUID
  public static final String RECEIVE_CHAR_UUID = UUIDUtil.UUID_16bit_128bit("0003", true);
  // 发送 UUID
  public static final String SEND_CHAR_UUID = UUIDUtil.UUID_16bit_128bit("0002", true);

  public static final UUID[] BLE_UNKNOWN_SERVICE_UUIDS = {UUID.fromString(BLE_UNKNOWN_SERVICE_UUID)};
  public static final UUID DESC_CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}






















