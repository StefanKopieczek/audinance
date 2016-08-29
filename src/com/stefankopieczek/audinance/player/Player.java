package com.stefankopieczek.audinance.player;

import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;
import com.stefankopieczek.audinance.renderer.JavaRenderer;
import com.stefankopieczek.audinance.renderer.MediaPlayer;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Player extends JFrame
{
    public static final int WIDTH = 480;
    public static final int HEIGHT = 580;

    private JTextArea outputConsole;
    private JButton loadButton;
    private JButton playButton;

    private EncodedAudio loadedAudio = null;

    public Player()
    {
        init();
    }

    private void init()
    {
        setTitle("Audinance Player");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        outputConsole = buildOutputConsole();
        loadButton = buildLoadButton();
        playButton = buildPlayButton();
        JPanel buttonPanel = buildButtonPanel(loadButton, playButton);

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(outputConsole, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);
        add(main);
    }

    private void showLoadFileDialog()
    {
        JFileChooser dialog = new JFileChooser();
        int rc = dialog.showOpenDialog(this);
        if (rc == JFileChooser.APPROVE_OPTION)
        {
            File chosen = dialog.getSelectedFile();

            try
            {
                loadedAudio = FileLoader.loadAudio(chosen);
            }
            catch (IOException e)
            {
                showError("IO error loading " + chosen + ": " + e);
            }
            catch (UnsupportedFormatException e)
            {
                showError(chosen + " was not a recognised audio file.");
            }
            catch (Exception e)
            {
                showError("Error loading " + chosen + ": " + e);
            }
        }
    }

    private void play()
    {
        if (loadedAudio != null)
        {
            try
            {
                MediaPlayer.play(loadedAudio);
            }
            catch (Exception e)
            {
                showError("Error playing audio: " + e);
            }
        }
        else
        {
            showError("You must first select an audio file to play.");
        }
    }

    private void showError(String s)
    {
        outputConsole.append("ERROR: " + s + "\n");
    }

    private static JTextArea buildOutputConsole()
    {
        JTextArea console = new JTextArea();
        console.setEditable(false);
        return console;
    }

    private JButton buildLoadButton()
    {
        JButton button = new JButton("Load file");
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                Player.this.showLoadFileDialog();
            }
        });

        return button;
    }

    private JButton buildPlayButton()
    {
        JButton button = new JButton("Play");
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                Player.this.play();
            }
        });

        return button;
    }

    private static JPanel buildButtonPanel(JButton... buttons)
    {
        JPanel buttonPanel = new JPanel();
        for (JButton button : buttons)
        {
            buttonPanel.add(button);
        }

        return buttonPanel;
    }

    public static void main(String[] args)
    {
        JFrame player = new Player();
        player.setVisible(true);
    }
}
