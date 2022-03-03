package com.lopl.melody;

import com.lopl.melody.audio.util.BotRightsManager;
import com.lopl.melody.slash.*;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmojiGuild;
import com.lopl.melody.utils.embed.EmojiGuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Melody {
  public static JDA manager;

  public static void main(String[] args) throws InterruptedException, AWTException, IOException {
    new Melody();
  }

  private Melody() throws InterruptedException, AWTException, IOException {
        createAndShowGUI();

    /*
    try {
      setup();
    } catch (LoginException e) {
      Logging.error(Melody.class, null, null, "Bot not registered! Head to https://discord.com/developers/applications to register the bot. Open the properties.json to set the bots key afterwards.");
    }
    */
  }

    public void createAndShowGUI() throws AWTException, IOException {
      String url = System.getProperty("user.dir") + "\\build\\resources\\main\\bitmap\\melody_transparent.png";
      ImageIcon image = new ImageIcon(url);

      // General Settings
      final JFrame frame = new JFrame("Melody");
      frame.setIconImage(image.getImage());
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.setSize(640, 360);
      frame.setLocationRelativeTo(null);

      // If OS does support SystemTray
      if (SystemTray.isSupported()) {
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        final SystemTray tray = SystemTray.getSystemTray();
        final PopupMenu popup = new PopupMenu();
        final TrayIcon icon   = new TrayIcon(image.getImage(), "Melody", popup);
        icon.setImageAutoSize(true);
        icon.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
              frame.setVisible(true);
            }
          }
        });

        tray.add(icon);

        MenuItem item1 = new MenuItem("Open Melody");
        item1.addActionListener(new ActionListener() {
          @Override public void actionPerformed(ActionEvent e) {
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
          }
        });
        MenuItem item2 = new MenuItem("Quit");
        item2.addActionListener(new ActionListener() {
          @Override public void actionPerformed(ActionEvent e) {
            tray.remove(icon);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.dispose();
            //System.exit(0);
          }
        });
        popup.add(item1);
        popup.add(item2);
      }

      // Makes window visible
      frame.setVisible(true);
    }

  private void setup() throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.LISTENING, "Music"));
    builder.setBulkDeleteSplittingEnabled(false);
    builder.setCompression(Compression.NONE);
    builder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
    builder.enableCache(CacheFlag.VOICE_STATE);
    builder.setAutoReconnect(true);
    builder.setStatus(OnlineStatus.ONLINE);

    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.getCommands().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.addEventListeners(builder, SlashCommands.getCommands().toArray(SlashCommand[]::new));
    SlashCommandClient slashCommandClient = slashCommandClientBuilder.build();
    slashCommandClient.start();

    builder.addEventListeners(new SlashCommandHandler(), slashCommandClient);
    builder.addEventListeners(new BotRightsManager());

    Melody.manager = builder.build();
    Logging.info(getClass(), null, null, "Loaded! Melody is now ready.");
    slashCommandClient.ready(Melody.manager);
    Melody.manager.awaitReady();

    EmojiGuildManager emojiGuildManager = new EmojiGuildManager().withGuild(new EmojiGuild(Melody.manager));
    boolean isInEmoteServer = emojiGuildManager.isAvailable();
    if (!isInEmoteServer) Melody.manager.shutdownNow();
    emojiGuildManager.loadEmotes();
  }
}
