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
package fr.litarvan.shenron.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.krobot.command.CommandContext;
import org.krobot.command.CommandHandler;
import org.krobot.command.SuppliedArgument;
import org.krobot.util.Dialog;
import fr.litarvan.shenron.MusicPlayer;
import java.util.Map;
import javax.inject.Inject;
import fr.litarvan.shenron.Music;
import org.jetbrains.annotations.NotNull;

public class CommandMusic implements CommandHandler
{
    @Inject
    private MusicPlayer player;

    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception
    {
        if (!args.containsKey("action"))
        {
            if (player.getQueue().size() == 0)
            {
                context.sendMessage(Dialog.info("Pas de musique", "Il n'y a pas de musique en train d'être jouée actuellement"));
                return;
            }

            AudioTrack track = player.getQueue().get(0);
            context.sendMessage(Dialog.info("Musique actuelle", "\n" + track.getInfo().title + "\n" + Music.parseTime(track.getPosition()) + " / " + Music.parseTime(track.getDuration())));

            return;
        }

        switch (args.get("action").getAsString())
        {
            case "unpause":
                player.play();
                break;
            case "pause":
                player.pause();
                break;
            case "next":
                player.next();
                break;
            case "stop":
                player.stop();
                break;
        }
    }
}
