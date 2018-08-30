package com.assimilation.ellie.assibungee.manager;

import com.assimilation.ellie.assibungee.command.helpop.HelpOP;
import com.assimilation.ellie.assibungee.server.AssiPlayer;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.SQLQuery;
import com.assimilation.ellie.assibungee.util.Util;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class HelpOPManager implements IManager {

    private SQLManager sqlManager;
    private LinkedHashMap<Integer, HelpOP> unhandled;
    private LinkedHashMap<Integer, HelpOP> session_handled;
    private int ssid;
    private int next_id;


    @Override
    public boolean load() {
        this.sqlManager = ModuleManager.getModuleManager().getSQLManager();
        unhandled = new LinkedHashMap<>();
        session_handled = new LinkedHashMap<>();

        ssid = ModuleManager.getModuleManager().getConfigManager().getSSID();

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.HELPOP.INITIAL_STATEMENT);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            PreparedStatement getnext = connection.prepareStatement(SQLQuery.HELPOP.GET_NEXT);

            ResultSet set = getnext.executeQuery();
            if(set.next()){
                next_id = (set.getInt("id")+1);
            } next_id = 1;

            getnext.close();


            PreparedStatement getUnhandled = connection.prepareStatement(SQLQuery.HELPOP.GET_UNHANDLED);
            set = getUnhandled.executeQuery();

            HelpOP helpOP;
            while(set.next()){
                int id = set.getInt("id");
                String sender = set.getString("sender");
                String server = set.getString("server");
                long sent = set.getLong("sent");
                int local_ssid = set.getInt("ssid");
                String content = set.getString("content");

                helpOP = new HelpOP(id, local_ssid, sender, server, content, sent);
                unhandled.put(id, helpOP);
            }
            set.close();
            getUnhandled.close();

            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
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
        return "helpop";
    }

    public LinkedHashMap<Integer, HelpOP> getUnhandled() {
        return unhandled;
    }

    public void createHelpOP(AssiPlayer sender, String content){
        if(!canSend(sender)){
            Set<HelpOP> helpOPs = getUnhandledHelpOPof(sender.getName(), 10);
            Util.mINFO(sender, "You may not send another HelpOP yet. Your other HelpOP(s) "+helpOPs+ (helpOPs.size() != 1 ? " are" : " is") +" still marked as pending.");
            return;
        }

        long now = System.currentTimeMillis();

        HelpOP helpOP = new HelpOP(next_id, ssid, sender.getName(), sender.getBase().getServer().getInfo().getName(), content, now);

        unhandled.put(helpOP.getID(), helpOP);
        next_id++;

        ModuleManager.getModuleManager().getStaffChatManager().helpopSentMessage(helpOP);

        try(Connection connection = sqlManager.openConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.HELPOP.PUT_NEW_HELPOP);


            preparedStatement.setString(1, sender.getName());
            preparedStatement.setString(2, sender.getBase().getServer().getInfo().getName());
            preparedStatement.setBoolean(3, false);
            preparedStatement.setLong(4, now);
            preparedStatement.setInt(5, ssid);
            preparedStatement.setString(6, content);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            Util.mINFO(sender, String.format(MessageLib.HELPOP_SENT_SENDER,""+helpOP.getID()));
        }catch(SQLException e){
            Util.mWARN(sender, "Error whilst sending off HelpOP: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    public void handle(int id, AssiPlayer handler){
        if(unhandled.get(id) != null && !unhandled.get(id).isHandled()){

            HelpOP helpOP = unhandled.get(id);
            helpOP.handle(handler.getName());

            session_handled.put(helpOP.getID(), helpOP);
            unhandled.remove(helpOP.getID());

            ModuleManager.getModuleManager().getStaffChatManager().helpopHandleMessage(helpOP);

            if(ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(helpOP.getSender()) != null){
                Util.mINFO(ModuleManager.getModuleManager().getPlayerManager().getOnlinePlayer(helpOP.getSender())
                    , String.format(MessageLib.HELPOP_HANDLE_SENDER, ""+helpOP.getID()));
            }

            try(Connection connection = sqlManager.openConnection()){

                PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.HELPOP.HANDLE_HELPOP);

                preparedStatement.setString(1, handler.getName());
                preparedStatement.setBoolean(2, true);
                preparedStatement.setInt(3, helpOP.getID());

                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();

            }catch(SQLException e){
                e.printStackTrace();
                Util.mWARN(handler, "Error whilst handling HelpOP: "+e.getLocalizedMessage());
                return;
            }

            return;
        }
    }

    public boolean canSend(AssiPlayer assiPlayer){
        if(!getUnhandledHelpOPof(assiPlayer.getName(), 1).isEmpty() && getUnhandledHelpOPof(assiPlayer.getName(), 100).stream().
                filter(helpOP -> helpOP.getSsid() == ssid).map(HelpOP::getSsid).collect(Collectors.toSet()).contains(ssid)) {
            return false;
        }
        return true;
    }

    public LinkedHashMap<Integer, HelpOP> getSession_handled() {
        return session_handled;
    }

    public HelpOP getHelpOP(int id, boolean checkHandled){
        if(unhandled.containsKey(id)){
            return unhandled.get(id);
        }else if(checkHandled){
            if(session_handled.containsKey(id)){
                return session_handled.get(id);
            }
        }
        return null;
    }

    public Set<HelpOP> getUnhandledHelpOPof(String name, int cap){
        Set<Integer> as = unhandled.values().parallelStream().filter(helpOP -> helpOP.getSender().equals(name)).map(HelpOP::getID).limit(cap).collect(Collectors.toSet());

        Set<HelpOP> helpOPs = new HashSet<>();
        as.forEach(integer -> helpOPs.add(unhandled.get(integer)));
        return helpOPs;
    }

     public Set<HelpOP> getHandledHelpOPof(String name, int cap){
        Set<Integer> as = unhandled.values().parallelStream().filter(helpOP -> helpOP.getSender().equals(name)).map(HelpOP::getID).limit(cap).collect(Collectors.toSet());
        Set<HelpOP> helpOPs = new HashSet<>();
        as.forEach(integer -> helpOPs.add(unhandled.get(integer)));
        return helpOPs;
    }





}
