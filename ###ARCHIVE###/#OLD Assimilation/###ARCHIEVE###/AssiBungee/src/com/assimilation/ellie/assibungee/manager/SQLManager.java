package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.AssiBungee;
import com.assimilation.ellie.assibungee.util.SQLQuery;

import java.sql.*;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLManager implements IManager {

    private AssiBungee assiBungee;
    private transient String host, database, username, password;
    private transient int port;


    public SQLManager(AssiBungee assiBungee, final String host, final int port, final String database, final String username, final String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.assiBungee = assiBungee;
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
            assiBungee.logE("Failed to make initial statement to database.");
            return false;
        }

        return true;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public String getModuleID() {
        return "sql";
    }
}
