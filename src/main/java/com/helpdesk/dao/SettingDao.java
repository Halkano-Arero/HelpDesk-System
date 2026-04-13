package com.helpdesk.dao;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.SettingType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingDao {

    public List<String> findNames(SettingType type) throws Exception {
        List<String> values = new ArrayList<>();
        String sql = "SELECT name FROM " + type.getTableName() + " ORDER BY name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                values.add(resultSet.getString("name"));
            }
        }
        return values;
    }

    public List<Map<String, Object>> findEntries(SettingType type) throws Exception {
        List<Map<String, Object>> values = new ArrayList<>();
        String sql = "SELECT id, name FROM " + type.getTableName() + " ORDER BY name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", resultSet.getInt("id"));
                item.put("name", resultSet.getString("name"));
                values.add(item);
            }
        }
        return values;
    }
}
