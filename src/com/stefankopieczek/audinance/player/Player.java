package com.stefankopieczek.audinance.player;

import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;
import com.stefankopieczek.audinance.renderer.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player extends JFrame
{
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private JTextArea outputConsole;
    private JButton loadButton;
    private JButton playButton;

    private static final Logger logger = Logger.getLogger(Player.class.getName());
    private static Logger packageLogger;

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

        JScrollPane scrollPane = new JScrollPane(outputConsole);
        main.add(scrollPane, BorderLayout.CENTER);

        main.add(buttonPanel, BorderLayout.SOUTH);
        add(main);

        outputConsole.append("Welcome to Audinance Player!\n");
    }

    private static void initLogging()
    {
        packageLogger = Logger.getLogger("com.stefankopieczek.audinance");
        packageLogger.setLevel(Level.ALL);
    }

    private void showLoadFileDialog()
    {
        logger.info("Displaying file chooser dialog");
        JFileChooser dialog = new JFileChooser();
        int rc = dialog.showOpenDialog(this);
        if (rc == JFileChooser.APPROVE_OPTION)
        {
            File chosen = dialog.getSelectedFile();
            logger.info("Loading file " + chosen);

            try
            {
                loadedAudio = FileLoader.loadAudio(chosen);
                logger.fine("Loaded file " + chosen + ": " + loadedAudio);
            }
            catch (IOException e)
            {
                logger.severe("IO error loading " + chosen + ": " + e);
            }
            catch (UnsupportedFormatException e)
            {
                logger.severe(chosen + " was not a recognised audio file");
            }
            catch (Exception e)
            {
                logger.severe("Error loading " + chosen + ": " + e);
            }
        }
        else
        {
            logger.info("File chooser dialog cancelled");
        }
    }

    private void play()
    {
        if (loadedAudio != null)
        {
            try
            {
                logger.info("Playing audio " + loadedAudio);
                MediaPlayer.play(loadedAudio);
            }
            catch (Exception e)
            {
                logger.severe("Error playing audio: " + e);
            }
        }
        else
        {
            logger.severe("You must first select an audio file to play.");
        }
    }

    private static JTextArea buildOutputConsole()
    {
        JTextArea console = new LoggingPanel(packageLogger);
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
        initLogging();
        JFrame player = new Player();
        player.setVisible(true);
    }
}
