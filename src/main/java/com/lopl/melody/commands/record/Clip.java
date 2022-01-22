package com.lopl.melody.commands.record;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.audio.handler.AudioReceiveListener;
import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.Mp3Encoder;
import com.lopl.melody.utils.embed.EmbedError;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Clip extends SlashCommand {

  public static final String DURATION = "duration";
  public static final String MP3_NAME = "clip_name";

  public Clip() {
    super.name = "clip";
    super.category = new Category("Voice");
    super.help = "/clip [x] : creates a clip of the x last seconds\n" +
        "/clip [x] [name] : you can add an additional name";
    super.description = "Creates a clip of the last seconds";
  }

  private static String getPJSaltString() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 13) {
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();

    //check for a collision on the 1/2e62 chance that it matches another salt string (lulW)
    File dir = new File("/var/www/html/");
    if (!dir.exists())
      dir = new File("recordings/");

    if (dir.listFiles() != null) {
      for (File f : dir.listFiles()) {
        if (f.getName().equals(saltStr))
          saltStr = getPJSaltString();
      }
    }
    return saltStr;
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOptions(new OptionData(OptionType.INTEGER, DURATION, "the time in seconds back", true).setMaxValue(120).setMinValue(1))
        .addOption(OptionType.STRING, MP3_NAME, "a name for the file", false);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    Guild guild = event.getGuild();
    if (guild == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
      event.replyEmbeds(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    if (!AudioStateChecks.isMelodyInVC(event)) {
      event.replyEmbeds(EmbedError.with("This Command requires **Melody** to be **connected to a voice channel**")).queue();
      return;
    }

    if (!AudioStateChecks.isMemberAndMelodyInSameVC(event)) {
      GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
      if (voiceState == null) return; // should be covered by the check above
      VoiceChannel channel = voiceState.getChannel();
      if (channel == null) return; // should be covered by the check above
      event.replyEmbeds(EmbedError.withFormat("This Command requires **you** to be **connected to Melody's voice channel** <#%S>", channel.getId())).queue();
      return;
    }

    AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceivingHandler();
    if (ah == null) {
      event.replyEmbeds(EmbedError.with("I wasn't recording... Type /record to start recording")).queue();
      return;
    }

    OptionMapping dom = event.getOption(DURATION);
    OptionMapping fom = event.getOption(MP3_NAME);
    if (dom == null) return;
    int duration = (int) dom.getAsLong();
    String fileName = fom == null ? null : fom.getAsString();
    clip(guild, duration, fileName, event.getTextChannel());
    event.reply("Sending clip...").queue();
  }

  public void clip(Guild guild, int duration, String fileName, TextChannel textChannel) {
    File file = writeToFile(guild, duration, fileName);
    sendMp3File(file, textChannel);
  }

  private File writeToFile(Guild guild, int duration, String fileName) {
    if (fileName == null) fileName = getPJSaltString();
    AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceivingHandler();
    assert ah != null;

    File file;
    File folder;
    try {
      folder = new File("./recordings");
      if (!folder.exists() && !folder.mkdir()) return null;
      file = new File("./recordings/" + fileName + ".mp3");
      byte[] voiceData;
      if (duration <= AudioReceiveListener.PCM_MINS * 60 * 2) {
        voiceData = ah.getUncompromisedVoice(duration);
        voiceData = Mp3Encoder.encodePcmToMp3(voiceData);
      } else {
        voiceData = ah.getVoiceData();
      }

      FileOutputStream fos = new FileOutputStream(file);
      fos.write(voiceData);
      fos.close();

      new Thread(() -> {
        try {
          sleep(1000 * 20);
        } catch (Exception ignored) {
          Logging.debug(Clip.class, guild, null, "Interrupted deleting a temp file. Check this file: " + file.getName());
        }    //20 second life for files set to discord (no need to save)

        if (file.delete())
          Logging.debug(Clip.class, guild, null, "\tDeleting file " + file.getName() + "...");
        else
          Logging.debug(Clip.class, guild, null, "\tDeleting file " + file.getName() + " failed");

      }).start();

      return file;
    } catch (IOException ex) {
      Logging.debug(Clip.class, guild, null, "Error creating the clip file");
      return null;
    }
  }

  private void sendMp3File(@Nullable File file, TextChannel textChannel) {
    if (file == null || !file.exists()) return;
    if (file.length() / 1024 / 1024 < 8) {
      textChannel.sendFile(file).queue(null, (Throwable) -> textChannel.sendMessageEmbeds(EmbedError.with("I don't have permissions to send files in " + textChannel.getName() + "!")).queue());
    }
  }
}
