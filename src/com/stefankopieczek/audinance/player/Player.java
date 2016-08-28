package com.stefankopieczek.audinance.player;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Player extends JFrame
{
    public static final int WIDTH = 480;
    public static final int HEIGHT = 580;

    private JTextArea outputConsole;
    private JButton loadButton;
    private JButton playButton;

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
        // TODO
    }

    private void play()
    {
        // TODO
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
