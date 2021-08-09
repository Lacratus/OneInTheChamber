package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.ArenaSerializer;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MongoDataHandler implements DataHandler {

    private final MongoCollection<Document> oitcPlayerCollection;
    private final MongoCollection<Document> arenaCollection;

    public MongoDataHandler() {
        OneInTheChamberPlugin main = OneInTheChamberPlugin.getInstance();
        MongoClient mongoClient = MongoClients.create(main.getConfig().getString("DB.Mongodb.Hostname"));
        MongoDatabase database = mongoClient.getDatabase(main.getConfig().getString("DB.Mongodb.Database"));
        this.oitcPlayerCollection = database.getCollection("Players");
        this.arenaCollection = database.getCollection("Arenas");
    }

    @Override
    public CompletableFuture<OITCPlayer> getData(Player player) {
        String uuid = player.getUniqueId().toString();
        return CompletableFuture.supplyAsync(() -> {
            Document document;
            // Get correct document
            document = oitcPlayerCollection.find(Filters.eq("_id", uuid)).first();

            // player is created -> Not working with upsert because it is bugged for some shit reason.
            if (document == null) {
                OITCPlayer oitcPlayer = new OITCPlayer(player);
                document = new Document("_id", player.getUniqueId().toString());
                document.append("kills", 0);
                document.append("deaths", 0);
                oitcPlayerCollection.insertOne(document);
                return oitcPlayer;
            }

            int kills = document.getInteger("kills");
            int deaths = document.getInteger("deaths");

            return new OITCPlayer(player, kills, deaths);
        });
    }

    @Override
    public void saveData(OITCPlayer oitcPlayer) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document();
            System.out.println(oitcPlayer.getKills());
            System.out.println(oitcPlayer.getDeaths());
            document.append("_id", oitcPlayer.getUuid());
            document.append("kills", oitcPlayer.getKills());
            document.append("deaths", oitcPlayer.getDeaths());

            // Replaceoptions made the plugin crash for some reason.
            oitcPlayerCollection.replaceOne(Filters.eq("_id", oitcPlayer.getUuid().toString()), document);

        });
    }

    @Override
    public void saveArenas(Collection<Arena> arenas) {
        for (Arena arena : arenas) {
            Document document = new Document();

            document.append("name", arena.getName());
            document.append("locations", ArenaSerializer.getSerializedLocations(arena));
            document.append("duration", arena.getDuration());

            // Replaceoptions made the plugin crash for some reason.
            UpdateResult result = oitcPlayerCollection.replaceOne(Filters.eq("name", arena.getName()), document);
            if(result.getModifiedCount() == 0){
                arenaCollection.insertOne(document);
            }
        }
    }

    @Override
    public CompletableFuture<Map<String, Arena>> getArenas() {
        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> documents = arenaCollection.find();
            Iterator<Document> iterator = documents.iterator();
            HashMap<String, Arena> arenas = new HashMap<>();
            while (iterator.hasNext()) {
                Document document = iterator.next();
                String name = document.getString("name");
                String locations = document.getString("locations");
                int duration = document.getInteger("duration");

                Arena arena = ArenaSerializer.getDeserializedLocations(name,locations);
                arena.setDuration(duration);
                arena.updateSigns();
                arenas.put(name, arena);
            }
            return arenas;
        });
    }


}

