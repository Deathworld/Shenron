package fr.litarvan.shenron.support.command;

import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.krobot.MessageContext;
import org.krobot.command.ArgumentMap;
import org.krobot.command.Command;
import org.krobot.command.CommandHandler;
import org.krobot.command.KrobotCommand;
import org.krobot.config.ConfigProvider;
import org.krobot.permission.BotRequires;
import org.krobot.runtime.KrobotRuntime;

@Command(value = "faq [target:user]", desc = "Affiche le lien de la FAQ (Admin: Averti un membre)", aliases = "f")
@BotRequires({Permission.MANAGE_ROLES})
public class FAQCommand implements CommandHandler
{
    @Inject
    private ConfigProvider config;

    @Override
    public Object handle(MessageContext context, ArgumentMap args) throws Exception
    {
        String link = config.at("support.faq");

        if (!args.has("target") || !context.hasPermission(Permission.ADMINISTRATOR))
        {
            return "FAQ : " + link;
        }

        context.send(config.at("support.message"), args.get("target", User.class).getAsMention(), link);

        Guild guild = context.getGuild();
        Member member = guild.getMember(args.get("target", User.class));

        Role moche = guild.getRolesByName("Pabo", true).get(0);
        Role hyperMoche = guild.getRolesByName("Hyper Pabo", true).get(0);
        Role ultraMoche = guild.getRolesByName("Ultra Pabo", true).get(0);

        if (guild.getMembersWithRoles(ultraMoche).contains(member))
        {
            return "En plus t'es Ultra Pabo, t'es vraiment le pire des pabo omg";
        }
        else if (guild.getMembersWithRoles(hyperMoche).contains(member))
        {
            guild.getController().addRolesToMember(member, ultraMoche).queue();
        }
        else if (guild.getMembersWithRoles(moche).contains(member))
        {
            guild.getController().addRolesToMember(member, hyperMoche).queue();
        }
        else
        {
            guild.getController().addRolesToMember(member, moche).queue();
        }

        return null;
    }
}
