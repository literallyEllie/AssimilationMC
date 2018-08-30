package net.assimilationmc.ellie.assicore.api.plugin;

import net.assimilationmc.ellie.assicore.AssiPlugin;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.manager.IManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Ellie on 9.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class AssiPluginManager implements IManager {

    private HashMap<String, AssiChildPlugin> plugins = new HashMap<>();

    public AssiPluginManager() {

    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean unload() {
        disablePlugins();
        return true;
    }

    @Override
    public String getModuleID() {
        return "plugin";
    }

    public void loadPlugin(AssiChildPlugin plugin) {
        plugins.put(plugin.getID(), plugin);
    }

    public void loadExternalPlugins(File rootDirectory) {

        File dir = new File(rootDirectory, "plugins");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IllegalArgumentException("Failed to create directory");
        }

        for (File file : dir.listFiles()) {
            try {
                JarFile j = new JarFile(file);
                JarEntry pluginYml = j.getJarEntry("assi-plugin.yml");
                if (pluginYml != null) {
                    InputStreamReader stream = new InputStreamReader(j.getInputStream(pluginYml), "UTF-8");
                    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(stream);

                    String name = yamlConfiguration.getString("id");
                    String mainClass = yamlConfiguration.getString("mainClass");

                    String version;
                    if (yamlConfiguration.get("version") != null) {
                        version = yamlConfiguration.getString("version");
                    } else version = "Unspecified";

                    String author;
                    if (yamlConfiguration.get("author") != null) {
                        author = yamlConfiguration.getString("author");
                    } else author = "Unspecified";

                    Class clazz = Class.forName(mainClass, true, new URLClassLoader(new URL[] { file.toURI().toURL() }, AssiPlugin.class.getClassLoader()));
                    Class pluginClass = clazz.asSubclass(AssiChildPlugin.class);
                    AssiChildPlugin childPlugin = (AssiChildPlugin) pluginClass.newInstance();
                    childPlugin.setId(name);
                    childPlugin.setAuthor(author);
                    childPlugin.setVersion(version);
                    childPlugin.onEnable();
                    plugins.put(name, childPlugin);
                }else throw new FileNotFoundException("assi-plugin.yml not found!");

            } catch (Throwable e) { // to avoid anything bad happening effecting startup
                AssiCore.getCore().getAssiPlugin().logE("Error whilst loading " + file.getName() + ".");
                e.printStackTrace();
            }
        }
    }

    public AssiChildPlugin getPlugin(String id) {
        return plugins.get(id);
    }

    public void disablePlugins() {
        plugins.forEach((s, assiChildPlugin) -> {
            try {
                assiChildPlugin.onDisable();
            } catch (Exception e) {
                AssiCore.getCore().getAssiPlugin().logE("Failed to shutdown plugin " + s);
                e.printStackTrace();
            }
        });
        plugins.clear();
    }

}
