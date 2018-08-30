package me.ellie.servermanager.display;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ellie on 14/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class MainFrame extends JFrame {

    public MainFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setResizable(false);
        setSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
        setTitle("Server Manager");
        setLayout(new FlowLayout());

        JLabel jLabel = new JLabel("Loading...");
        jLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        add(jLabel);

        setVisible(true);
    }



}
