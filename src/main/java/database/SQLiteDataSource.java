package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDataSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static
    {
        try
        {
            final File dbFile = new File("database.db");

            if(!dbFile.exists())
            {
                if(dbFile.createNewFile())
                {
                    LOGGER.info("Created database file");
                }
                else
                {
                    LOGGER.info("Could not create database file");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCachesize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try(final Statement statement = getConnection().createStatement())
        {
            statement.execute("DROP TABLE IF EXISTS roles");
            statement.execute("DROP TABLE IF EXISTS guild_settings");
            statement.execute("VACUUM");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        try (final Statement statement = getConnection().createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS roles(" +
                    "role_id INTEGER PRIMARY KEY," +
                    "perm_play INTEGER" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    //"id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id VARCHAR(20) PRIMARY KEY," +
                    "guild_name VARCHAR(100) NOT NULL," +
                    "guild_roles INTEGER," +
                    "FOREIGN KEY(guild_roles) REFERENCES roles(role_id)" +
                    ");");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private SQLiteDataSource()
    {

    }

    public static Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }
}
