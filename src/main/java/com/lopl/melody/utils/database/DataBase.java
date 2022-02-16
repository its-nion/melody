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

/**
 * This class is the accessor to the database.
 * The file and all the tables are managed here.
 * Do not use this class to make changes to any table.
 * Change this class only if you want to make changes to the general behaviour of the database.
 * If you want to execute sql statements use the {@link SQL} class.
 */
public class DataBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataBase.class);
  private static final HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;
  private static Connection connection;

  /*
   * These static calls are executed before the first call of anything else in this class.
   * The database file is created if needed. Then the database is linked.
   * Afterwards all necessary tables are created if needed and updated.
   */
  static {
    createDatabaseFile();
    setup();
//        dropTables();
    createTables();
  }

  /**
   * This will check if the database file exists.
   * If not a new one is created.
   */
  private static void createDatabaseFile() {
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

  /**
   * This will link the database file with this code and create a
   * {@link HikariDataSource} object. This object is used to create a connection to the database.
   * This Connection can than be used with the {@link #getConnection()} method to execute {@link SQL} {@link Statement}s.
   */
  private static void setup() {
    config.setJdbcUrl("jdbc:sqlite:database.db");
    config.setConnectionTestQuery("SELECT 1");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCachesize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    ds = new HikariDataSource(config);
  }

  /**
   * This will create and update all tables if needed.
   * Every table should have its own function, that is called here.
   */
  public static void createTables() {
    try (final Statement statement = getConnection().createStatement()) {
      createSettingsTable(statement);
      createMixerTable(statement);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This will create the settings table if necessary.
   * Then it will also check if all columns are present.
   * If columns are missing these are added.
   * TODO: check for double columns, check for obsolete columns
   * FutureTODO: if a rename of a column is necessary, a easy to add implementation should be built.
   * @param statement an empty Statement that can be used
   * @throws SQLException when there is any sql error
   */
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

  /**
   * This will create the mixer table if necessary.
   * @param statement an empty Statement that can be used
   * @throws SQLException when there is any sql error
   */
  private static void createMixerTable(Statement statement) throws SQLException {
    statement.execute("CREATE TABLE IF NOT EXISTS guild_music_mixer(guild_id VARCHAR(20) PRIMARY KEY, lows TINYINT, mids TINYINT, highs TINYINT);");
  }

  @Deprecated
  private static void dropTables() {
    try (final Statement statement = getConnection().createStatement()) {
      // WHY???
      statement.execute("DROP TABLE IF EXISTS roles");
      statement.execute("DROP TABLE IF EXISTS guild_settings");
//            statement.execute("VACUUM");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Use this method to get a connection to the database not something else!
   * This method will make sure, there is only on connection.
   * Multiple connections are not allowed.
   * @return a ready to use Connection
   * @throws SQLException when there is any sql error
   */
  public static Connection getConnection() throws SQLException {
    if (connection != null) return connection;
    return connection = ds.getConnection();
  }

}
