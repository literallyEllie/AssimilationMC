package com.assimilation.ellie.assikits.kit;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.manager.SQLManager;
import com.assimilation.ellie.assicore.util.MessageLib;
import com.assimilation.ellie.assikits.AssiKits;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Ellie on 18/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class KitManager {

    private final String TABLE = "assimilation_kits_kits";
    private AssiKits assiKits;
    private final SQLManager sqlManager;

    private int next_id;

    private SortedMap<Integer, AssiKit> kits;

    public KitManager(AssiKits assiKits){
        this.assiKits = assiKits;
        this.sqlManager = ModuleManager.getModuleManager().getSQLManager();
        this.kits = new TreeMap<>();

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT UNIQUE, "+
                    "`name` VARCHAR(100) NOT NULL, " +
                    "`permission` TEXT, "+
                    "`price` SMALLINT, "+
                    "`inventory` LONGTEXT, "+
                    "`enabled` TINYINT, "+
                    "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

            preparedStatement.executeUpdate();
            connection.close();

        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("[Kits] Failed to make initial statement to database!");
        }
        loadKits();
    }

    private void loadKits(){
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `"+TABLE+"`;");

            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while(resultSet.next()){

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String permission = resultSet.getString("permission");
                int price = resultSet.getInt("price");
                String inventory = resultSet.getString("inventory");
                boolean enabled = resultSet.getBoolean("enabled");

                AssiKit assiKit = new AssiKit(id, name, inventory);
                assiKit.setPermission(permission);
                assiKit.setPrice(price);
                assiKit.setEnabled(enabled);
                this.kits.put(id, assiKit);
                i++;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            next_id = kits.lastKey() + 1;

            assiKits.logI("Loaded "+i+" kits.");
        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("Failed to load kits!");
        }
    }

    public void createKit(String name, Inventory inventory, int price, boolean enabled){
        if(price > 1) price = -1;

        AssiKit assiKit = new AssiKit(next_id, name, inventory);
        assiKit.setPrice(price);
        assiKit.setEnabled(enabled);

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `"+TABLE+"` (name, permission, price, inventory, enabled) " +
                    "VALUES (?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, assiKit.getPermission());
            preparedStatement.setInt(3, assiKit.getPrice());
            preparedStatement.setString(4, assiKit.getSerializedInventory());
            preparedStatement.setBoolean(5, enabled);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            next_id = next_id + 1;

        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("Failed to create kit!");
            return;
        }
        kits.put(assiKit.getID(), assiKit);
    }

    public void saveKit(AssiKit assiKit){

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `"+TABLE+"` SET name = ?, permission = ?, price = ?, inventory = ?, enabled = ? WHERE ID = ?;");
            preparedStatement.setString(1, assiKit.getName());
            preparedStatement.setString(2, assiKit.getPermission());
            preparedStatement.setInt(3, assiKit.getPrice());
            preparedStatement.setString(4, assiKit.getSerializedInventory());
            preparedStatement.setBoolean(5, assiKit.isEnabled());
            preparedStatement.setInt(6, assiKit.getID());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("Failed to edit kit!");
        }
        kits.remove(assiKit.getID());
        kits.put(assiKit.getID(), assiKit);
    }

    public void deleteKit(int id){

        if(getKit(id) != null){

            try(Connection connection = sqlManager.openConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `"+TABLE+"` WHERE id = ?;");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            }catch(SQLException e){
                e.printStackTrace();
                assiKits.logE("Failed to delete kit!");
            }

            kits.remove(id);
        }
    }

    public void toggleKit(AssiKit assiKit, boolean enabled){
        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `"+TABLE+"` SET enabled = ? WHERE id = ?;");
            preparedStatement.setBoolean(1, enabled);
            preparedStatement.setInt(2, assiKit.getID());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("Failed to toggle kit!");
        }

        kits.remove(assiKit.getID());
        kits.put(assiKit.getID(), assiKit);
    }

    public void giveInventoryKit(Player player, int id){
        if(getKit(id) != null){
            try {
                player.getInventory().setContents(getKit(id).getInventory().getContents());
            }catch(IOException e){
                player.sendMessage(String.format(MessageLib.COMMAND_FAIL, e));
            }
        }
    }

    public SortedMap<Integer, AssiKit> getKits() {
        return kits;
    }

    public AssiKit getKit(int id) {
        return kits.get(id);
    }
}

