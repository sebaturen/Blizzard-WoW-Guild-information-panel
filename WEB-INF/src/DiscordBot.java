/**
 * File : DiscordBot.java
 * Desc : DiscordBot controller~
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.gameObject.FactionAssaultControl;
import com.blizzardPanel.gameObject.ServerTime;
import java.text.SimpleDateFormat;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Random;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.ReadyEvent;

public class DiscordBot extends ListenerAdapter
{ 
    private String guildDiscordChanel;
    private String botChanel;
    private long chanelId;
    private JDA jda;
    
    public DiscordBot()
    {
        
    }
    
    public DiscordBot build()
    {
        try
        {
            jda = new JDABuilder(GeneralConfig.getStringConfig("DISCORD_BOT_TOKEN"))
                .addEventListener(this)
                .build();
            this.guildDiscordChanel = GeneralConfig.getStringConfig("DISCORD_GUILD_NAME");
            this.botChanel = GeneralConfig.getStringConfig("DISCORD_CHANEL_NAME");
        } catch (LoginException e) {
            Logs.errorLog(DiscordBot.class, "Fail to build Discord BOT "+ e);
        }
        return this;
    }    
    
    public void sendMessajeNotification(String message) 
    {
        if(this.chanelId > 0)
        {
            MessageChannel chanel = jda.getTextChannelById(this.chanelId);
            chanel.sendMessage(message).queue();            
        }
        else
            Logs.errorLog(DiscordBot.class, "Fail to send messaje, channel ID not found.");
    }
    
    @Override
    public void onReady(ReadyEvent event) 
    {
        //Save channel id
        for(TextChannel tx : jda.getTextChannels()) 
        {
            if(tx.getGuild().getName().equals(this.guildDiscordChanel) && tx.getName().equals(this.botChanel))
            {
                this.chanelId = tx.getIdLong();
                break;
            }
        }
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        net.dv8tion.jda.core.entities.User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
                                                        //  This could be a TextChannel, PrivateChannel, or Group!

        //String msg = message.getContentDisplay();              //This returns a human readable version of the Message. Similar to
                                                        // what you would see in the client.

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that
                                                        // sent the Message is a BOT or not!
        if(!bot)
        {
            if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
            {
                //Because we now know that this message was sent in a Guild, we can do guild specific things
                // Note, if you don't check the ChannelType before using these methods, they might return null due
                // the message possibly not being from a Guild!

                Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
                TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
                Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

                String name;
                if (message.isWebhookMessage())
                {
                    name = author.getName();                //If this is a Webhook message, then there is no Member associated
                }                                           // with the User, thus we default to the author for name.
                else
                {
                    name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
                }                                           // otherwise it will default to their username. (User#getName())

                if(!guild.getName().equals(this.guildDiscordChanel))
                {
                    //channel.sendMessage("The BOT only work in "+ this.guildDiscordChanel).queue();
                }
                else
                {
                    botRespond(message, channel, author, guild);
                }
                //System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
            }
            else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
            {
                //The message was sent in a PrivateChannel.
                //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
                PrivateChannel privateChannel = event.getPrivateChannel();
                channel.sendMessage("The BOT only work in "+ this.guildDiscordChanel).queue();

                //System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
            }
            else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!
            {
                //The message was sent in a Group. It should be noted that Groups are CLIENT only.
                Group group = event.getGroup();
                String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

                channel.sendMessage("The BOT only work in "+ this.guildDiscordChanel).queue();

                //System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
            }   
        }     
    }
    
    private void botRespond(Message message, MessageChannel channel, net.dv8tion.jda.core.entities.User author, Guild guild)
    {
        //Valid if is command msg
        String msg = message.getContentDisplay();
        if(msg.length() > 2 && msg.substring(0, 1).equals("!"))
        {
            String msgCommand = (msg.substring(1)).toLowerCase();
            if(msgCommand.indexOf(" ") > 0)
                msgCommand = msgCommand.substring(0, msgCommand.indexOf(" "));
            /*net.dv8tion.jda.core.entities.User author = new User
            System.out.println("author> "+ author.getId());*/
            //Now that you have a grasp on the things that you might see in an event, specifically MessageReceivedEvent,
            // we will look at sending / responding to messages!
            //This will be an extremely simplified example of command processing.
            //Remember, in all of these .equals checks it is actually comparing
            // message.getContentDisplay().equals, which is comparing a string to a string.
            // If you did message.equals() it will fail because you would be comparing a Message to a String!
            switch (msgCommand) {
                case "ping":
                    //This will send a message, "pong!", by constructing a RestAction and "queueing" the action with the Requester.
                    // By calling queue(), we send the Request to the Requester which will send it to discord. Using queue() or any
                    // of its different forms will handle ratelimiting for you automatically!
                    channel.sendMessage("pong!").queue();
                    break;
                case "servertime":
                    channel.sendMessage("Server time: "+ ServerTime.getServerTime()).queue();
                    break;
                case "nextassault":
                    FactionAssaultControl fAssault = new FactionAssaultControl();
                    if(fAssault.isCurrent())
                        channel.sendMessage
                        (    "The assault is currently happening!, GO KILL HORDES! "
                            + "\nTime Remaining: ["+ fAssault.getTimeRemainingCurrentAssault()[0] +"h:"
                            + fAssault.getTimeRemainingCurrentAssault()[1] +"m]"
                        ).queue();
                    else
                        channel.sendMessage
                        (   "Next assault is in ["
                            + fAssault.getTimeRemaining(fAssault.getNextAssault())[0] +"h:"
                            + fAssault.getTimeRemaining(fAssault.getNextAssault())[1] +"m]"
                            + " ("+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fAssault.getNextAssault()) +" Server Time)"
                            //" (server time: "+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fAssault.getServerTime()) +") "
                        ).queue();
                    break;
                case "roll":
                    //In this case, we have an example showing how to use the Success consumer for a RestAction. The Success consumer
                    // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
                    // have object returns and will instead have Void returns. You can still use the success consumer to determine when
                    // the action has been completed!

                    Random rand = new Random();
                    int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
                    channel.sendMessage(author.getAsMention() +" Your roll: " + roll).queue(sentMessage ->  //This is called a lambda statement. If you don't know
                    {                                                               // what they are or how they work, try google!
                        if (roll < 3)
                        {
                            channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n").queue();
                        }
                    });
                    break;
                case "regist":
                    channel.purgeMessages(message);
                    try 
                    {
                        String registCode = (msg.substring(msg.indexOf(" "), msg.length())).trim();
                        try {
                            User us = new User(registCode);
                            if(us.setDiscordUserId(author.getIdLong()+""))
                            {
                                channel.sendMessage("Hey "+ author.getAsMention() +" your account is ready! ("+ us.getBattleTag().substring(0, us.getBattleTag().indexOf("#")) +")").queue();
                                //Add channel role!
                                Role artMember = guild.getRolesByName(this.guildDiscordChanel +" Members", true).get(0);
                                guild.getController().addSingleRoleToMember(guild.getMember(author), artMember).queue();
                            }
                            else
                            {
                                channel.sendMessage("Hey "+ author.getAsMention() +" your registry failed... If your discord account change, ask to admin").queue();
                            }
                        } catch (DataException ex) {
                            channel.sendMessage("Hey "+ author.getAsMention() +"... your code is incorrect...").queue();
                        }
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        channel.sendMessage("Hey "+ author.getAsMention() +" look your account info discord regist code in 'account info' web page.").queue();                        
                    }
                    break;
                case "move":
                    //Only admin members can move ppl!
                    if(guild.getMember(author).hasPermission(Permission.ADMINISTRATOR))
                    {
                        try 
                        {
                            //Get from - to channels move members
                            String moveChanels = (msg.substring(msg.indexOf(" "), msg.length())).trim();
                            String from = moveChanels.substring(moveChanels.indexOf("'")+1, moveChanels.indexOf("'",1)).trim();
                            //+4 becouse first have 2 ' <apost> and + spaces + 1 apost
                            String to = moveChanels.substring(from.length()+4, moveChanels.length()-1).trim(); 
                            //Aply move
                            for(VoiceChannel vChannel : guild.getVoiceChannels())
                            {
                                if(vChannel.getName().equals(from))
                                {
                                    VoiceChannel vChMove = guild.getVoiceChannelsByName(to, false).get(0);
                                    for(Member mb : vChannel.getMembers())
                                    {                                        
                                        guild.getController().moveVoiceMember(mb, vChMove).queue();
                                        //System.out.println("mb> "+ mb.get);
                                    }
                                }
                            }
                            channel.sendMessage("Sir, yes, sir!").queue();  
                        } catch (java.lang.StringIndexOutOfBoundsException e) {
                            channel.sendMessage("Em.. sir, use `!move '<from>' '<to>'` like: `!move 'General' 'Raid'`").queue();                            
                        }
                    }
                    else
                        channel.sendMessage("you!? you do not have permits").queue();                            
                    break;
                case "play": case "queue": case "skip": case "lyrics": case "stop": case "next": case "p":
                    if(!channel.getName().equals("musik"))
                    {
                        channel.purgeMessages(message);
                        channel.sendMessage("Hey "+ author.getAsMention() +"... use 'musik' channel to play music!").queue();
                    }
                    break;
                case "opening":
                    String searchInfo;
                    try {
                        searchInfo = (msg.substring(msg.indexOf(" "), msg.length())).trim();
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        searchInfo = "anime name <temp>";
                    }
                    channel.sendMessage(author.getAsMention() +" FRIKII!!! - try `!play "+ searchInfo +" opening`").queue();                    
                    break;
            }
        }  
    }
    
    public boolean removeRank(String discId)
    {
        Guild guild = jda.getGuildById(GeneralConfig.getStringConfig("DISCORD_GUILD_ID"));
        net.dv8tion.jda.core.entities.User discUser = jda.getUserById(discId);
        //remove role
        Role artMember = guild.getRolesByName(this.guildDiscordChanel +" Members", true).get(0);
        guild.getController().removeSingleRoleFromMember(guild.getMember(discUser), artMember).queue();
        sendMessajeNotification("Member "+ discUser.getAsMention() +" leave guild, role remove");
        return true;
    }
}