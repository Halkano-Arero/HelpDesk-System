package com.helpdesk.util;

public enum SettingType {
    DEPARTMENT("department", "departments"),
    CATEGORY("category", "categories"),
    STATUS("status", "statuses");

    private final String requestValue;
    private final String tableName;

    SettingType(String requestValue, String tableName) {
        this.requestValue = requestValue;
        this.tableName = tableName;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public String getTableName() {
        return tableName;
    }

    public static SettingType fromRequestValue(String value) {
        for (SettingType type : values()) {
            if (type.requestValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported setting type.");
    }
}
