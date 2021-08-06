package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MySQLDataHandler implements DataHandler {

    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLDataHandler() {
        OneInTheChamberPlugin main = OneInTheChamberPlugin.getInstance();
        this.host = main.getConfig().getString("DB.Mysql.Host");
        this.port = main.getConfig().getInt("DB.Mysql.Port");
        this.database = main.getConfig().getString("DB.Mysql.Database");
        this.username = main.getConfig().getString("DB.Mysql.Username");
        this.password = main.getConfig().getString("DB.Mysql.Password");
    }

    @Override
    public CompletableFuture<OITCPlayer> getData(Player player) {
        String uuid = player.getUniqueId().toString();
        return CompletableFuture.supplyAsync(() -> {
            // First login makes row in table
            try (Connection connection = openConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM oitcplayer WHERE uuid = ?")) {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                // New player created in database
                if (!rs.next()) {
                    PreparedStatement ps2 = connection.prepareStatement("INSERT INTO oitcplayer(uuid,kills,deaths) VALUES(?,DEFAULT,DEFAULT)");
                    ps2.setString(1, uuid);
                    ps2.executeUpdate();
                    ps2.close();
                    rs.close();
                    return new OITCPlayer(player);
                }

                // Player already exists
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                rs.close();
                return new OITCPlayer(player, kills, deaths);

            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public void saveData(OITCPlayer oitcPlayer) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = openConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE oitcplayer SET kills = ?, deaths = ? WHERE Uuid= ?")) {
                ps.setInt(1, oitcPlayer.getKills());
                ps.setInt(2, oitcPlayer.getDeaths());
                ps.setString(3, oitcPlayer.getUuid().toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // Week 3
    @Override
    public void addLocation(Location location) {

    }

    public Connection openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            return connection;
        }
        System.out.println("Something went wrong when opening the connection");
        return null;
    }

}
