/*
 * Copyright 2016-2017 Adrien 'Litarvan' Navratil
 *
 * This file is part of Shenron.
 *
 * Shenron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shenron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shenron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.litarvan.shenron.command.group;

import net.dv8tion.jda.core.Permission;
import org.krobot.command.CommandContext;
import org.krobot.command.CommandHandler;
import org.krobot.command.SuppliedArgument;
import org.krobot.config.ConfigProvider;
import org.krobot.permission.BotRequires;
import org.krobot.permission.UserRequires;
import org.krobot.util.Dialog;
import fr.litarvan.shenron.GroupTrigger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.tuple.ImmutablePair;
import fr.litarvan.shenron.Group;
import org.jetbrains.annotations.NotNull;

@UserRequires({Permission.ADMINISTRATOR})
@BotRequires({Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION})
public class CommandGroupTrigger implements CommandHandler
{
    @Inject
    private ConfigProvider config;

    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception
    {
        String text = args.get("message").getAsString();
        List<String> triggerStrings = args.get("emote#group").getAsStringList();

        Message message = context.sendMessage(text).get();

        List<ImmutablePair<String, String>> entries = new ArrayList<>();

        for (String group : triggerStrings)
        {
            String[] split = group.split("#");

            String groupName = split[0];
            String emoteName = split[1];

            List<Emote> emotes = context.getChannel().getJDA().getEmotesByName(emoteName, true);

            if (emotes.size() == 0)
            {
                context.sendMessage(Dialog.warn("Erreur", "Impossible de trouver l'emote '" + emoteName + "'"));
                message.delete().queue();

                return;
            }

            Group gr = null;

            Group[] groups = config.at("groups." + context.getGuild().getId(), Group[].class);

            if (groups == null)
            {
                context.sendMessage(Dialog.warn("Erreur", "Il n'y a pas encore de groupe sur ce serveur"));
                return;
            }

            for (Group g : groups)
            {
                if (g.getName().trim().equalsIgnoreCase(groupName.trim()))
                {
                    gr = g;
                    break;
                }
            }

            if (gr == null)
            {
                context.sendMessage(Dialog.warn("Erreur", "Impossible de trouver le groupe '" + groupName + "'"));
                message.delete().queue();

                return;
            }

            Emote emote = emotes.get(0);

            message.addReaction(emote).queue();
            entries.add(new ImmutablePair<>(emote.getId(), gr.getName()));
        }

        config.get("groups").append("triggers", GroupTrigger[].class, new GroupTrigger(message.getId(), entries));
    }
}
