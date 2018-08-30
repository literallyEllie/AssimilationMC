package net.assimilationmc.assicore.achievement.achievements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.Set;

public class AchievementProgressList {

    private Map<String, Status> todoList;

    public AchievementProgressList() {
        todoList = Maps.newLinkedHashMap();
    }

    public AchievementProgressList addTodo(String message) {
        todoList.put(message, Status.TODO);
        return this;
    }

    public AchievementProgressList addDone(String message) {
        todoList.put(message, Status.DONE);
        return this;
    }

    public Set<String> compile() {
        Set<String> todo = Sets.newLinkedHashSet();
        todoList.forEach((s, status) -> todo.add(status.getPrefix() + s));
        todoList.clear();
        return todo;
    }

    public enum Status {
        TODO(ChatColor.RED + "✘ "),
        DONE(ChatColor.GREEN + "✔ ");

        private String prefix;

        Status(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

    }

}
