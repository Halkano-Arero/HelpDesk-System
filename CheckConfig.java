public class CheckConfig {
  public static void main(String[] args) throws Exception {
    System.out.println("db.password=" + com.helpdesk.config.AppConfig.get("db.password"));
    try (java.sql.Connection c = com.helpdesk.db.DBConnection.getConnection()) {
      try (java.sql.Statement s = c.createStatement(); java.sql.ResultSet rs = s.executeQuery("SELECT 1")) {
        if (rs.next()) System.out.println("db=" + rs.getInt(1));
      }
    }
  }
}
