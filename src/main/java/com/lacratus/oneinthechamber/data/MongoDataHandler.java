package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class MongoDataHandler implements DataHandler{

    private final MongoCollection<Document> oitcPlayerCollection;

    public MongoDataHandler() {
        OneInTheChamberPlugin main = OneInTheChamberPlugin.getInstance();
        MongoClient mongoClient = MongoClients.create(main.getConfig().getString("DB.Mongodb.Hostname"));
        MongoDatabase database = mongoClient.getDatabase(main.getConfig().getString("DB.Mongodb.Database"));
        this.oitcPlayerCollection = database.getCollection("Players");
    }

    @Override
    public CompletableFuture<OITCPlayer> getData(Player player) {
        String uuid = player.getUniqueId().toString();
        return CompletableFuture.supplyAsync(() -> {
           Document document;
           document = oitcPlayerCollection.find(Filters.eq("_id", uuid)).first();

           // player is created -> Not working with upsert because it is bugged for some shit reason.
           if(document == null){
               OITCPlayer oitcPlayer = new OITCPlayer(player);
               document = new Document("_id",player.getUniqueId().toString());
               document.append("kills",0);
               document.append("deaths",0);
               oitcPlayerCollection.insertOne(document);
               return oitcPlayer;
           }

           int kills = document.getInteger("kills");
           int deaths = document.getInteger("deaths");

           return new OITCPlayer(player,kills,deaths);
        });
    }

    @Override
    public void saveData(OITCPlayer oitcPlayer) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document();
            System.out.println(oitcPlayer.getKills());
            System.out.println(oitcPlayer.getDeaths());
            document.append("_id",oitcPlayer.getUuid());
            document.append("kills",oitcPlayer.getKills());
            document.append("deaths",oitcPlayer.getDeaths());

            // Replaceoptions made the plugin crash for some reason.
            oitcPlayerCollection.replaceOne(Filters.eq("_id", oitcPlayer.getUuid().toString()), document);
        });
    }

    // Week 3
    @Override
    public void addLocation(Location location) {

    }
}
