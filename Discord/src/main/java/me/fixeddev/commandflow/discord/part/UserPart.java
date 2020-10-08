package me.fixeddev.commandflow.discord.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.discord.DiscordCommandManager;
import me.fixeddev.commandflow.discord.utils.ArgumentsUtils;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.kyori.text.TranslatableComponent;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class UserPart implements ArgumentPart {

    private final String name;

    public UserPart(String name) {
        this.name = name;
    }

    @Override
    public List<? extends User> parseValue(CommandContext context, ArgumentStack stack) throws ArgumentParseException {
        Message message = context.getObject(Message.class, DiscordCommandManager.MESSAGE_NAMESPACE);
        Guild guild = message.getTextChannel().getGuild();

        String target = stack.next();

        User user = null;

        if (ArgumentsUtils.isValidSnowflake(target)) {
            user = guild.getJDA().getUserById(target);
        }

        if (user == null && ArgumentsUtils.isValidTag(target)) {
            user = guild.getJDA().getUserByTag(target);
        }

        if (user == null && ArgumentsUtils.isUserMention(target)) {
            String id = target.substring(3, target.length() - 1);
            user = guild.getJDA().getUserById(id);
        }

        if (user == null) {
            ArgumentParseException exception = new ArgumentParseException(TranslatableComponent.of("unknown.user"));
            exception.setArgument(this);

            throw exception;
        }

        return Collections.singletonList(user);
    }

    @Override
    public Type getType() {
        return User.class;
    }

    @Override
    public String getName() {
        return name;
    }
}
