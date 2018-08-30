package net.assimilationmc.ellie.assicore.task.fakeenjin;

import java.lang.reflect.Type;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum  EnjinInstructionCode {

   // ADD_PLAYER_GROUP(PlayerGroupUpdateData.class),
//    REMOVE_PLAYER_GROUP(PlayerGroupUpdateData.class),
 //   EXECUTE(ExecuteData.class),  EXECUTE_AS(null),  CONFIRMED_COMMANDS(new TypeToken(){}.getType()),
  //  CONFIG(Object.class),  ADD_PLAYER_WHITELIST(String.class),  REMOVE_PLAYER_WHITELIST(String.class),  RESPONSE_STATUS(String.class),
  //  BAN_PLAYER(String.class),  UNBAN_PLAYER(String.class),  CLEAR_INGAME_CACHE(ClearInGameCacheData.class),
   // NOTIFICATIONS(NotificationData.class),  PLUGIN_VERSION(String.class);
        ;


    private Type type;

    public Type getType() {
        return this.type;
    }

    EnjinInstructionCode(Type type) {
        this.type = type;
    }
}
