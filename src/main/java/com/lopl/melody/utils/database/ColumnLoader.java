package com.lopl.melody.utils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnLoader<O> {

  private final ResultSet resultSet;
  private final List<O> objects;

  public ColumnLoader(ResultSet resultSet) {
    this.resultSet = resultSet;
    this.objects = new ArrayList<>();
  }

  public List<O> every(RowExecutor<O> executor) {
    try {
      while (resultSet.next()) {
        O o = executor.row(resultSet);
        objects.add(o);
      }
      return objects;
    } catch (SQLException ignored) {
      return objects;
    }
  }

  public interface RowExecutor<O> {
    O row(ResultSet resultSet) throws SQLException;
  }

}
