package com.example.l.blewatch.utils;

public class UUIDUtil
{
//  6e4003c1-b5a3-f393-e0a9-e50e24dcca9e
  private static final String base_uuid_regex = "6e40([0-9a-f][0-9a-f][0-9a-f][0-9a-f])-b5a3-f393-e0a9-e50e24dcca9e";

  public static boolean isBaseUUID(String uuid)
  {
    return uuid.toLowerCase().matches(base_uuid_regex);
  }

  public static String UUID_128_to_16bit(String uuid, boolean lower_case)
  {
    if (uuid.length() == 36) {
      if (lower_case) {
        return uuid.substring(4, 8).toLowerCase();
      }
      return uuid.substring(4, 8).toUpperCase();
    }

    return null;
  }

  public static String UUID_16bit_128bit(String uuid, boolean lower_case)
  {
    if (lower_case) {
      return (base_uuid_regex.substring(0, 4) + uuid + base_uuid_regex.substring(38)).toLowerCase();
    }
    return (base_uuid_regex.substring(0, 4) + uuid + base_uuid_regex.substring(38)).toUpperCase();
  }
}