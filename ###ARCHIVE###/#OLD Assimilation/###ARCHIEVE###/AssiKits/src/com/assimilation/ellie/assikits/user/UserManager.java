package com.assimilation.ellie.assikits.user;

import com.assimilation.ellie.assicore.manager.ModuleManager;
import com.assimilation.ellie.assicore.manager.SQLManager;
import com.assimilation.ellie.assikits.AssiKits;
import com.assimilation.ellie.assikits.kit.AssiKit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Ellie on 18/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UserManager {

    private final String TABLE = "assimilation_kits_player";

    private AssiKits assiKits;
    private SQLManager sqlManager;

    private HashMap<String, Integer> currentKits;

    public UserManager(AssiKits assiKits){
        this.assiKits = assiKits;
        this.sqlManager = ModuleManager.getModuleManager().getSQLManager();
        this.currentKits = new HashMap<>();

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, "+
                    "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                    "`name` TEXT, "+
                    "`coins` SMALLINT, "+
                    "`kits` LONGTEXT, "+
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

            preparedStatement.executeUpdate();
            connection.close();

        }catch(SQLException e){
            e.printStackTrace();
            assiKits.logE("[Players] Failed to make initial statement to database!");
        }

    }

    public void givePlayer(AssiKit assiKit, Player player){



    }

    public void buyKit(AssiKit assiKit, Player player){
        if(canAfford(assiKit, player)){



        }

    }

    public boolean canAfford(AssiKit assiKit, Player player){
        if(assiKit.isFree()) return true;


        return false;
    }







}
