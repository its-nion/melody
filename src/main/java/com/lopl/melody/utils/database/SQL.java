package com.lopl.melody.utils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.lopl.melody.utils.Logging;

public class SQL {

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
