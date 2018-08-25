package ru.csc.java2014.my_yar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ArchiverGUI extends JFrame {
    private DirPathField dirPathField;
    private ArchPathField archPathField;

    ArchiverGUI() {
        InputPathFields inPathFields = new InputPathFields();
        ControlButtons buttons = new ControlButtons();
        getContentPane().add(BorderLayout.CENTER, inPathFields);
        getContentPane().add(BorderLayout.SOUTH, buttons);
    }

    private class InputPathFields extends JPanel {
        public InputPathFields() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            dirPathField = new DirPathField();
            archPathField = new ArchPathField();

            add(dirPathField);
            add(archPathField);
        }
    }

    private class DirPathField extends JPanel {
        private JTextField field;

        DirPathField() {
            JLabel label = new JLabel("Directory path: ");
            field = new JTextField(20);
            add(label);
            add(field);
        }

        public String getText() {
            return field.getText();
        }
    }

    private class ArchPathField extends JPanel {
        private JTextField field;

        ArchPathField() {
            JLabel label = new JLabel("Archive path: ");
            field = new JTextField(20);
            add(label);
            add(field);
        }

        public String getText() {
            return field.getText();
        }
    }

    private class ControlButtons extends JPanel {
        public ControlButtons() {
            JButton packButton = new JButton("pack");
            JButton unpackButton = new JButton("unpack");
            packButton.addActionListener(new packListener());
            unpackButton.addActionListener(new unpackListener());
            add(packButton);
            add(unpackButton);
        }

        private class packListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                File dir = new File(dirPathField.getText());
                File archive = new File(archPathField.getText());

                try {
                    MyFileArchiver archiver = new MyFileArchiver(archive);
                    archiver.pack(dir);
                } catch (IOException ex) {
                }
            }
        }

        private class unpackListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                File dir = new File(dirPathField.getText());
                File archive = new File(archPathField.getText());

                try {
                    MyFileArchiver archiver = new MyFileArchiver(archive);
                    archiver.unpack(dir);
                } catch (IOException ex) {
                }
            }
        }
    }
}
