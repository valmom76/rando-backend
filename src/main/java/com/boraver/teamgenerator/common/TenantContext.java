package com.boraver.teamgenerator.common;

public final class TenantContext {
  private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
  public static void setTenantId(String tenantId) { TENANT.set(tenantId); }
  public static String getTenantId() { return TENANT.get(); }
  public static void clear() { TENANT.remove(); }
  private TenantContext() {}
}
