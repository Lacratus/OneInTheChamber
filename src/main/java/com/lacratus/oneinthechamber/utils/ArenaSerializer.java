package com.lacratus.oneinthechamber.utils;

import com.lacratus.oneinthechamber.objects.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.util.UUID;

public class ArenaSerializer {
    /**
     * All locations and sings will be serialized to a String.
     * @param arena Arena of the locations that will be serialized
     * @return Serialized String with locations
     */
    public static String getSerializedLocations(Arena arena) {
        StringBuilder stringBuilder = new StringBuilder();
        if(arena == null){
            return null;
        }

        if(arena.getSpawnLocation() != null){
            Location location = arena.getSpawnLocation();
            stringBuilder.append("Spawn:").append(location.getX()).append(";").append(location.getY()).append(";").append(location.getZ())
                    .append(";").append(location.getWorld().getUID()).append("@");
        }

        if(arena.getLocations().size() > 0) {
            for (Location location : arena.getLocations()) {
                stringBuilder.append("Location:").append(location.getX()).append(";").append(location.getY()).append(";").append(location.getZ())
                        .append(";").append(location.getWorld().getUID()).append("@");
            }
        }

        if(arena.getSignLocations().size() > 0){
            for(Sign sign : arena.getSignLocations()){
                Location location = sign.getLocation();
                org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) sign.getData();
                stringBuilder.append("Sign:").append(location.getX()).append(";").append(location.getY()).append(";").append(location.getZ())
                        .append(";").append(location.getWorld().getUID()).append("@");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Serialized string of locations will be deserialized to lobbylocation, spawnlocations and signs
     * @param arenaName This is the name of the arena
     * @param serializedString This is the serialized string of all locations of an Arena
     * @return Created arena with Lobbylocation, spawnlocations and signs will be returned
     */
    public static Arena getDeserializedLocations(String arenaName, String serializedString) {
        Arena arena = new Arena(arenaName);
        String[] locations = serializedString.split("@");
        for(String locationString : locations){
            String[] locationType = locationString.split(":");
            String[] parts = locationType[1].split(";");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            UUID u = UUID.fromString(parts[3]);
            World w = Bukkit.getServer().getWorld(u);
            Location location = new Location(w,x,y,z);
            if(locationType[0].equals("Spawn")){
                arena.setSpawnLocation(location);
            }
            if(locationType[0].equals("Location")){
                arena.getLocations().add(location);
            }
            if(locationType[0].equals("Sign")){
                Sign sign = (Sign) location.getBlock().getState();
                arena.getSignLocations().add(sign);
            }

        }

        return arena;
    }
}
