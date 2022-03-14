package com.lopl.melody.gui.panes;

import com.lopl.melody.Melody;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BotStatus extends JPanel {

  private final DataHolder data;
  private final PaneManager manager;
  private Button startButton;
  private Button stopButton;
  private Label statusLabel;

  public BotStatus(PaneManager manager){
    super();
    this.data = new DataHolder();
    this.manager = manager;
    setupLayout();
    onRunningChanged();
  }

  private void setupLayout(){
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutConstraints = new GridBagConstraints();
    layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
    layoutConstraints.weightx = .5;
    layoutConstraints.weighty = 0;
    layoutConstraints.anchor = GridBagConstraints.NORTH;
    layoutConstraints.insets = new Insets(4,4,2,4);  //top padding
    startButton = new Button("Start");
    startButton.setPreferredSize(new Dimension(40, 20));
    startButton.setFocusable(false);
    startButton.addActionListener(this::onStartClicked);
    stopButton = new Button("Stop");
    stopButton.setPreferredSize(new Dimension(40, 20));
    stopButton.setFocusable(false);
    stopButton.addActionListener(this::onStopClicked);
    statusLabel = new Label("SHUTDOWN");
    statusLabel.setAlignment(Label.CENTER);
    statusLabel.setPreferredSize(new Dimension(100, 20));
    statusLabel.setFocusable(false);
    setLayout(layout);
    setAlignmentY(0);
    layoutConstraints.gridx = 0;
    layoutConstraints.gridy = 0;
    layoutConstraints.gridwidth = 2;
    add(statusLabel, layoutConstraints);
    layoutConstraints.gridx = 0;
    layoutConstraints.gridy = 1;
    layoutConstraints.gridwidth = 1;
    add(startButton, layoutConstraints);
    layoutConstraints.gridx = 1;
    add(stopButton, layoutConstraints);


    // empty space below
    layoutConstraints.gridy = 2;
    layoutConstraints.gridx = 0;
    layoutConstraints.weighty = 1;
    add(new Label(""), layoutConstraints);
    setVisible(true);
  }

  private void onStartClicked(ActionEvent event){
    data.loading = true;
    onRunningChanged();
    new Thread(() -> {
      Melody.onReady(jda -> jda.addEventListener(new ListenerAdapter() {
        @Override
        public void onStatusChange(@NotNull StatusChangeEvent event) {
          updateData();
        }
        @Override
        public void onGuildJoin(@NotNull GuildJoinEvent event) {
          manager.serverPane.reloadData();
        }
      }));
      Melody.main(new String[0]);
    }).start();
  }

  private void onStopClicked(ActionEvent event){
    if (Melody.manager == null) return;
    Logging.info(Melody.class, null, null, "Shutting down now!");
    Melody.manager.shutdownNow();
    Melody.manager = null;
  }

  private void onRunningChanged(){
    if (data.isRunning()){
      startButton.setEnabled(false);
      stopButton.setEnabled(true);
      statusLabel.setBackground(Color.GREEN);
      manager.serverPane.reloadData();
    }else if (data.isLoading()){
      startButton.setEnabled(false);
      stopButton.setEnabled(false);
      statusLabel.setBackground(Color.YELLOW);
    }else{
      startButton.setEnabled(true);
      stopButton.setEnabled(false);
      statusLabel.setBackground(Color.RED);
    }
  }

  private void onStatusChanged(){
    statusLabel.setText(data.getStatus());
  }

  public void updateData(){
    String newState = jdaState();
    if (!newState.equals(data.status)){
      data.status = newState;
      onStatusChanged();
    }
    boolean newRunning = jdaRunning();
    if (newRunning != data.running) {
      data.running = newRunning;
      onRunningChanged();
    }
    boolean newLoading = jdaLoading();
    if (newLoading != data.loading) {
      data.loading = newLoading;
      onRunningChanged();
    }
  }

  private String jdaState(){
    JDA jda = Melody.manager;
    if (jda == null) return JDA.Status.SHUTDOWN.toString();
    JDA.Status status = jda.getStatus();
    return status.toString();
  }

  private boolean jdaRunning(){
    JDA jda = Melody.manager;
    if (jda == null) return false;
    JDA.Status status = jda.getStatus();
    return switch (status) {
      case CONNECTED -> true;
      case INITIALIZING, INITIALIZED, LOGGING_IN, CONNECTING_TO_WEBSOCKET, IDENTIFYING_SESSION,
          AWAITING_LOGIN_CONFIRMATION, LOADING_SUBSYSTEMS, DISCONNECTED, RECONNECT_QUEUED, WAITING_TO_RECONNECT,
          ATTEMPTING_TO_RECONNECT, SHUTTING_DOWN, SHUTDOWN, FAILED_TO_LOGIN -> false;
    };
  }

  private boolean jdaLoading(){
    JDA jda = Melody.manager;
    if (jda == null) return false;
    JDA.Status status = jda.getStatus();
    return switch (status) {
      case INITIALIZING, CONNECTING_TO_WEBSOCKET, IDENTIFYING_SESSION,
          AWAITING_LOGIN_CONFIRMATION, LOADING_SUBSYSTEMS, LOGGING_IN, ATTEMPTING_TO_RECONNECT, SHUTTING_DOWN -> true;
      case INITIALIZED, DISCONNECTED, RECONNECT_QUEUED, WAITING_TO_RECONNECT,
           SHUTDOWN, FAILED_TO_LOGIN, CONNECTED -> false;
    };
  }

  public static class DataHolder{
    private boolean running;
    private boolean loading;
    private String status;

    public boolean isRunning() {
      return running;
    }

    public boolean isLoading() {
      return loading;
    }

    public String getStatus() {
      return status;
    }
  }

}
