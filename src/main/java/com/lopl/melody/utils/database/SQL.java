package com.lopl.melody.utils.database;

import com.lopl.melody.utils.Logging;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Use this class to execute SQL {@link Statement}s.
 * View the {@link #execute(String, Object...)} and {@link #query(String, Object...)} method for more information.
 */
public class SQL {

  /**
   * This method will execute the passed sql String.
   * Use the returned ResultSet to get the information you need.
   * <p>
   * Example:
   * <pre>
   * ResultSet result = new SQL().query("SELECT name FROM sqlite_master WHERE type='table'");
   * ColumnLoader<String> columnLoader = new ColumnLoader<>(resultSet);
   * List<String> tables = columnLoader.every(row -> row.getString("name"));
   * </pre>
   * @param sql The sql query.
   * @param args String format variables. Works like {@link String#format(String, Object...)}.
   *             Look there for more information.
   * @return a ResultSet
   */
  public ResultSet query(String sql, Object... args) {
    try {
      String call = String.format(sql, args);
      Logging.debug(getClass(), null, null, "Executing sql: " + call);
      return DataBase.getConnection().prepareStatement(call).executeQuery();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return null;
    }
  }

  /**
   * This method will execute the passed sql String.
   * No data is returned cause this should only be used for non-query statements
   * <p>
   * Example:
   * <pre>
   * boolean result = new SQL().execute("DROP TABLE IF EXISTS guild_settings");
   * if (!result) Logging.debug(getClass(), null, null, "SQL Error occurred")
   * </pre>
   * @param sql The sql query.
   * @param args String format variables. Works like {@link String#format(String, Object...)}.
   *             Look there for more information.
   * @return a boolean showing the success of the execution
   */
  public boolean execute(String sql, Object... args) {
    try {
      String call = String.format(sql, args);
      Logging.debug(getClass(), null, null, "Executing sql: " + call);
      DataBase.getConnection().prepareStatement(call).execute();
      return true;
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return false;
    }
  }

}
