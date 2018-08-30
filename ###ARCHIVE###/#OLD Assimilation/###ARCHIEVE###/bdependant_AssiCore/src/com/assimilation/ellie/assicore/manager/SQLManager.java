package com.assimilation.ellie.assicore.manager;

import com.assimilation.ellie.assicore.AssiPlugin;
import com.assimilation.ellie.assicore.util.SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLManager implements IManager {

    private AssiPlugin assiPlugin;
    private transient String host, database, username, password;
    private transient int port;


    public SQLManager(AssiPlugin assiPlugin, final String host, final int port, final String database, final String username, final String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.assiPlugin = assiPlugin;
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database, username, password);
    }

    @Override
    public boolean load() {

        try(Connection connection = this.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.PLAYERS.INITIAL_STATEMENT);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e) {
            e.printStackTrace();
            assiPlugin.logE("Failed to make initial statement to database.");
            return false;
        }

        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public String getModuleID() {
        return "sql";
    }

}
