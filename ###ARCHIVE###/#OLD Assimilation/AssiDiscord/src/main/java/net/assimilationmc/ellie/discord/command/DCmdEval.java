package net.assimilationmc.ellie.discord.command;

import net.assimilationmc.ellie.discord.AssiDiscord;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Ellie on 16.7.17 for votifier.
 * Affiliated with www.minevelop.com
 */
public class DCmdEval extends DisCommand {

    private final ScriptEngine scriptEngine;

    public DCmdEval(){
        super("eval", "Eval engine", Permission.ADMINISTRATOR, "eval <message>");

        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            scriptEngine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, Packages.net.dv8tion.jda.core, " +
                    "Packages.net.dv8tion.jda.core.entities, Packages.net.dv8tion.jda.core.managers);");
            scriptEngine.put("bot", AssiDiscord.getAssiDiscord());
        }catch(ScriptException e){
            e.printStackTrace();
            AssiDiscord.getAssiDiscord().logW("Failed to setup Eval!");
        }
    }


    @Override
    protected void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1){
            scriptEngine.put("jda", e.getJDA());
            scriptEngine.put("e", e);
            Object output;
            String input = e.getMessage().getRawContent().substring(AssiDiscord.getAssiDiscord().getJsonBotConfig().getBotPrefix().length());
            input = input.substring(input.indexOf(" "));
            try {
                output = scriptEngine.eval("(function() { with (imports) {\n" + input + "\n} })();");
            } catch (ScriptException ex) {
                e.getChannel().sendMessage("An error occurred: "+ex.getMessage()).queue();
                return;
            }
            String outputString;
            if(output == null) {
                outputString = "Executed successfully with no output.";
            } else {
                outputString = output.toString();
                if(outputString.length() >= 2048) {
                    e.getChannel().sendMessage("The output exceeds the Discord limit.").queue();
                    return;
                }
            }
            e.getChannel().sendMessage(outputString).queue();
        }else{
            e.getChannel().sendMessage(correctUsage()).queue();
        }


    }

}
