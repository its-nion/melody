package com.lopl.melody.utils.database;

import com.lopl.melody.commands.essentials.Settings;
import com.lopl.melody.settings.Setting;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DataBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataBase.class);
  private static final HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;
  private static Connection connection;

  static {
    createDatabaseFile();
    setup();
//        dropTables();
    createTables();
  }

  public static void createTables() {
    try (final Statement statement = getConnection().createStatement()) {
      createSettingsTable(statement);
      createMixerTable(statement);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void createSettingsTable(Statement statement) throws SQLException {
    List<String> settings = Settings.settings.stream().map(setting -> setting.getDatabaseName() + " TINYINT").collect(Collectors.toList());
    statement.execute("CREATE TABLE IF NOT EXISTS guild_settings(" +
        "guild_id VARCHAR(20) PRIMARY KEY," +
        String.join(", ", settings) +
        ");");

    // add all missing columns
    ResultSet columnResult = new SQL().query("PRAGMA table_info(guild_settings)");
    List<String> columns = new ColumnLoader<String>(columnResult).every(c -> c.getString("name"));
    List<String> wantedColumns = Settings.settings.stream().map(Setting::getDatabaseName).collect(Collectors.toList());
    for (String contained : columns)
      wantedColumns.remove(contained);
    for (String necessary : wantedColumns) {
      new SQL().execute("ALTER TABLE guild_settings ADD %s TINYINT", necessary);
    }
  }

  private static void createMixerTable(Statement statement) throws SQLException{
    statement.execute("CREATE TABLE IF NOT EXISTS guild_music_mixer(guild_id VARCHAR(20) PRIMARY KEY, lows TINYINT, mids TINYINT, highs TINYINT);");
  }

  @Deprecated
  public static void dropTables() {
    try (final Statement statement = getConnection().createStatement()) {
      // WHY???
      statement.execute("DROP TABLE IF EXISTS roles");
      statement.execute("DROP TABLE IF EXISTS guild_settings");
//            statement.execute("VACUUM");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void setup() {
    config.setJdbcUrl("jdbc:sqlite:database.db");
    config.setConnectionTestQuery("SELECT 1");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCachesize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    ds = new HikariDataSource(config);
  }

  public static void createDatabaseFile() {
    try {
      final File dbFile = new File("database.db");
      if (!dbFile.exists()) {
        if (dbFile.createNewFile()) LOGGER.info("Created database file");
        else LOGGER.info("Could not create database file");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws SQLException {
    if (connection != null) return connection;
    return connection = ds.getConnection();
  }

}
