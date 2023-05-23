package com.gateweb.voucher.endpoint.rest.v1.request;

import java.util.HashMap;
import java.util.Map;

public enum FileTypes {
  GW("gw"),
  MOF("mof");
  final String type;

  FileTypes(String type) {
    this.type = type;
  }

  private static final Map<String, FileTypes> MAP = new HashMap<>();

  static {
    for (FileTypes value : FileTypes.values()) {
      MAP.put(value.type, value);
    }
  }

  public static FileTypes fromType(String type) throws Exception {
    final FileTypes fileTypes = MAP.get(type);
    if (fileTypes == null)
      throw new Exception(String.format("type(%s) error , only accept [gw,mof]", type));
    return fileTypes;
  }
}
