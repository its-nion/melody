package com.lopl.melody.gui.panes;

import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.json.JsonProperties;
import com.lopl.melody.utils.json.PropertiesData;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropertiesPane extends JPanel {

  private final PaneManager manager;
  private final List<JTextField> inputs = new ArrayList<>();

  public PropertiesPane(PaneManager manager) {
    this.manager = manager;
    setupLayout();
  }

  private void setupLayout() {
    PropertiesData data = JsonProperties.getData();
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutConstraints = new GridBagConstraints();
    layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
    layoutConstraints.weighty = 0;
    layoutConstraints.weightx = 0;
    layoutConstraints.anchor = GridBagConstraints.NORTH;
    layoutConstraints.insets = new Insets(4,4,2,4);  //top padding
    layoutConstraints.gridx = 0;
    setLayout(layout);
    setAlignmentY(0);

    List<String> ignoredFields = List.of("secretPropertiesLocation");
    List<Field> dataFields = Arrays.stream(data.getClass().getDeclaredFields()).filter(f -> !ignoredFields.contains(f.getName())).collect(Collectors.toList());

    // adding labels with increasing y coordinate
    layoutConstraints.gridy = 0;
    List<Label> labels = new ArrayList<>();
    for (Field field : dataFields){
      String fieldName = field.getName();
      Label label = new Label(fieldName);
      labels.add(label);
      add(label, layoutConstraints);
      layoutConstraints.gridy++;
    }

    layoutConstraints.gridx = 1;
    layoutConstraints.weightx = 1;

    // adding input fields with increasing y coordinate
    layoutConstraints.gridy = 0;
    for (Field field : dataFields){
      String value;
      try {
        value = (String) field.get(data);
      } catch (IllegalAccessException e) {
        layoutConstraints.gridy++;
        continue;
      }
      JTextField textField = new JTextField(value);
      textField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          fieldChanged(field, textField);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          fieldChanged(field, textField);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          fieldChanged(field, textField);
        }
      });
      inputs.add(textField);
      add(textField, layoutConstraints);
      layoutConstraints.gridy++;
    }

    // empty space below
    layoutConstraints.weightx = 0;
    layoutConstraints.gridy = 20;
    layoutConstraints.gridx = 0;
    layoutConstraints.weighty = 1;
    add(new Label(""), layoutConstraints);
    setVisible(true);
  }

  public void fieldChanged(Field field, JTextField textField){
    PropertiesData data = JsonProperties.getData();
    try {
      field.set(data, textField.getText());
      new JsonProperties.Writer().updateData(data);
      Logging.debug(getClass(), null, null, "Updated properties data");
    } catch (IllegalAccessException | IOException ignored) {
    }
  }

  public void deactivateFields(){
    for (JTextField textField : inputs){
      textField.setEnabled(false);
    }
  }

  public void activateFields(){
    for (JTextField textField : inputs){
      textField.setEnabled(true);
    }
  }

}
