package com.lopl.melody.utils.database;

import com.lopl.melody.utils.Logging;

import java.sql.ResultSet;
import java.sql.SQLException;

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
