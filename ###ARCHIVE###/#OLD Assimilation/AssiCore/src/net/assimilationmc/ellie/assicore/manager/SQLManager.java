package net.assimilationmc.ellie.assicore.manager;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.util.SQLQuery;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.sql.SQLException;

/**
 * Created by Ellie on 11/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLManager implements IManager {

    private AssiPlugin assiPlugin;
    private Sql2o sql2o;

    SQLManager(AssiPlugin assiPlugin, final String host, final int port, final String database, final String username, final String password){
        sql2o = new Sql2o("jdbc:mysql://"+host+":"+port+"/"+database, username, password);
        this.assiPlugin = assiPlugin;
    }

    public Sql2o getSql2o() {
        return sql2o;
    }


    @Override
    public boolean load() {
        try(Connection connection = this.sql2o.open()) {
            connection.createQuery(SQLQuery.PLAYERS.INITIAL_STATEMENT).executeUpdate().close();
        }
        return true;
    }

    @Override
    public boolean unload() {
        if(sql2o != null){
            try {
                sql2o.getDataSource().getConnection().close();
            }catch(SQLException e){
                e.printStackTrace();
                assiPlugin.logE("Failed to close connection safely");
            }finally {
                sql2o = null;
            }
        }
        return true;
    }

    @Override
    public String getModuleID() {
        return "sql";
    }

}
