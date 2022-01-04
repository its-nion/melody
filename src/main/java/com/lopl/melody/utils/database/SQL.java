package com.lopl.melody.utils.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {

  public ResultSet query(String sql, Object... args) {
    try {
      String call = String.format(sql, args);
      return DataBase.getConnection().prepareStatement(call).executeQuery();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return null;
    }
  }

  public boolean execute(String sql, Object... args) {
    try {
      String call = String.format(sql, args);
      DataBase.getConnection().prepareStatement(call).execute();
      return true;
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      return false;
    }
  }

}
