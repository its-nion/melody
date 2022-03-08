package com.lopl.melody.gui;

import com.lopl.melody.Melody;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BotStatus extends JPanel {

  private final DataHolder data;

  private GridLayout layout;
  private Button startButton;
  private Button stopButton;
  private Label statusLabel;

  public BotStatus(){
    super();
    this.data = new DataHolder();
    setupLayout();
    onRunningChanged();
  }

  private void setupLayout(){
    layout = new GridLayout();
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
    add(statusLabel);
    add(startButton);
    add(stopButton);
//    setLayout(layout);
    setVisible(true);

  }

  private void onStartClicked(ActionEvent event){
    new Thread(() -> {
      Melody.onReady(jda -> jda.addEventListener(new ListenerAdapter() {
        @Override
        public void onStatusChange(@NotNull StatusChangeEvent event) {
          updateData();
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
    }else{
      startButton.setEnabled(true);
      stopButton.setEnabled(false);
      statusLabel.setBackground(Color.RED);
    }
  }

  private void onStatusChanged(){
    statusLabel.setText(data.getState());
  }

  public void updateData(){
    String newState = jdaState();
    if (!newState.equals(data.state)){
      data.state = newState;
      onStatusChanged();
    }
    boolean newRunning = jdaRunning();
    if (newRunning != data.running)
      data.running = newRunning;
      onRunningChanged();
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

  public static class DataHolder{
    private boolean running;
    private String state;

    public boolean isRunning() {
      return running;
    }

    public String getState() {
      return state;
    }
  }

}
