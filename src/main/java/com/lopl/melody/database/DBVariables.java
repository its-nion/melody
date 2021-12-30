package com.lopl.melody.database;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBVariables
{
    public static void initiateCurrentServers(JDA jda)
    {
        for (Guild guild : jda.getGuilds())
        {
            try (final PreparedStatement insertStatement = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("INSERT OR IGNORE INTO guild_settings(guild_id, guild_name, guild_roles) VALUES(?, ?, ?)")) {
                insertStatement.setString(1, String.valueOf(guild.getIdLong()));
                insertStatement.setString(2, guild.getName());

                insertStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getVolume (long guildID)
    {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
        .getConnection()
        .prepareStatement("SELECT volume FROM guild_settings WHERE guild_id = ?"))
        {
            preparedStatement.setString(1, String.valueOf(guildID));

            try (final ResultSet resultSet = preparedStatement.executeQuery())
            {
                if(resultSet.next())
                {
                    return resultSet.getInt("volume");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 75;
    }

    public static void setVolume (long guildID, int vol)
    {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("UPDATE guild_settings SET volume = ? WHERE guild_id = ?"))
        {
            preparedStatement.setInt(1, vol);
            preparedStatement.setString(2, String.valueOf(guildID));

            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
