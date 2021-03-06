package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.ArenaSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
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

    @Override
    public void saveArenas(Collection<Arena> arenas) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = openConnection()) {
                for (Arena arena : arenas) {
                    //PreparedStatement ps = connection.prepareStatement("UPDATE arena SET Locations = ?, Signlocations = ?, SpawnLocation = ?, Duration = ? WHERE Name= ?");
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO arena (Name, Locations,Duration) VALUES (?, ?, ?) ON DUPLICATE KEY " +
                                                                                                "UPDATE Locations = ?, Duration = ?");
                    ps.setString(1, arena.getName());
                    ps.setString(2, ArenaSerializer.getSerializedLocations(arena));
                    ps.setInt(3, arena.getDuration());
                    ps.setString(4, ArenaSerializer.getSerializedLocations(arena));
                    ps.setInt(5, arena.getDuration());
                    ps.executeUpdate();
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, Arena>> getArenas() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, Arena> arenas = new HashMap<>();
            try (Connection connection = openConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM arena")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("Name");
                    Arena arena = ArenaSerializer.getDeserializedLocations(name,rs.getString("Locations"));
                    arena.setDuration(rs.getInt("Duration"));
                    arena.updateSigns();
                    arenas.put(name, arena);
                }
                rs.close();
                return arenas;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }


    public Connection openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            return connection;
        }
        Bukkit.getLogger().warning(("Something went wrong when opening the connection"));
        return null;
    }

}
