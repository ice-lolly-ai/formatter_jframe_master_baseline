package com.example;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class VideoPlayer extends JFrame {

    private static final int INITIAL_WINDOW_WIDTH = 800;
    private static final int INITIAL_WINDOW_HEIGHT = 600;

    private static final int MIN_VIDEO_SIZE = 50; // Minimum video size
    private static final int MAX_VIDEO_SIZE = 200; // Maximum video size

    private static final int MIN_NUM_VIDEOS = 1;
    private static final int MAX_NUM_VIDEOS = 100;
    private static final int INITIAL_NUM_VIDEOS = 10;
    private static final int INITIAL_TIMER_DELAY = 20; // Initial timer delay (milliseconds)

    private List<VideoComponent> videoComponents;
    private JPanel controlPanel;
    private JSlider speedSlider;
    private JSlider sizeSlider;
    private JSlider numVideosSlider;
    private JButton randomizeButton;
    private JButton darkModeButton;
    private boolean isDarkMode = false;
    private Random random;
    private Timer animationTimer;

    public VideoPlayer() {
        super("VLCJ Video Player App");

        // Initialize variables
        videoComponents = new ArrayList<>();
        random = new Random();

        // Set up the main window
        setSize(INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Using absolute positioning
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);

        // Initialize video components
        initializeVideoComponents(INITIAL_NUM_VIDEOS);

        // Initialize control panel
        initializeControlPanel();

        // Start animation timer
        startAnimation();

        // Add a listener for window resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionControlPanel();
            }
        });
    }

    private void initializeVideoComponents(int numVideos) {
        // Remove existing video components
        for (VideoComponent vc : videoComponents) {
            vc.mediaPlayerComponent.release();
            remove(vc.mediaPlayerComponent);
        }
        videoComponents.clear();

        // Create new video components
        for (int i = 0; i < numVideos; i++) {
            // Create video component
            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

            // Set initial size and position
            int x = random.nextInt(getWidth() - MAX_VIDEO_SIZE);
            int y = random.nextInt(getHeight() - MAX_VIDEO_SIZE);
            mediaPlayerComponent.setBounds(x, y, MAX_VIDEO_SIZE, MAX_VIDEO_SIZE);

            // Create VideoComponent wrapper
            VideoComponent videoComponent = new VideoComponent(mediaPlayerComponent, x, y);
            videoComponents.add(videoComponent);

            // Add to frame
            add(mediaPlayerComponent);

            // Play video
            String mediaPath = "/Users/icelolly/Desktop/dev/java_video_attempts/VLCJ_maven/demo/src/res/wealth_1_pure.mp4"; // Update this path
            MediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
            mediaPlayer.media().play(mediaPath);
            mediaPlayer.controls().setRepeat(true);
        }
        revalidate();
        repaint();
    }

    private void initializeControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Speed Slider
        JLabel speedLabel = new JLabel("Speed:");
        controlPanel.add(speedLabel);
        speedSlider = new JSlider(1, 100, INITIAL_TIMER_DELAY);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMajorTickSpacing(10); // Adjust as needed
        speedSlider.setMinorTickSpacing(1); // Adjust as needed
        speedSlider.addChangeListener((ChangeEvent e) -> adjustSpeed());
        controlPanel.add(speedSlider);

        // Size Slider
        JLabel sizeLabel = new JLabel("Size:");
        controlPanel.add(sizeLabel);
        sizeSlider = new JSlider(MIN_VIDEO_SIZE, MAX_VIDEO_SIZE, MAX_VIDEO_SIZE);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setMajorTickSpacing(25); // Adjust as needed
        sizeSlider.setMinorTickSpacing(5); // Adjust as needed
        sizeSlider.addChangeListener((ChangeEvent e) -> adjustVideoSize());
        controlPanel.add(sizeSlider);

        // Number of Videos Slider
        JLabel numVideosLabel = new JLabel("Number of Videos:");
        controlPanel.add(numVideosLabel);
        numVideosSlider = new JSlider(MIN_NUM_VIDEOS, MAX_NUM_VIDEOS, INITIAL_NUM_VIDEOS);
        numVideosSlider.setPaintTicks(true);
        numVideosSlider.setSnapToTicks(true);
        numVideosSlider.setMajorTickSpacing(10); // Adjust as needed
        numVideosSlider.setMinorTickSpacing(1); // Adjust as needed
        numVideosSlider.addChangeListener((ChangeEvent e) -> adjustNumberOfVideos());
        controlPanel.add(numVideosSlider);

        // Randomize Button
        randomizeButton = new JButton("Randomize");
        randomizeButton.addActionListener((ActionEvent e) -> randomizeVideos());
        controlPanel.add(randomizeButton);

        // Dark Mode Button
        darkModeButton = new JButton("Toggle Dark Mode");
        darkModeButton.addActionListener((ActionEvent e) -> toggleDarkMode());
        controlPanel.add(darkModeButton);

        // Add the control panel to the frame
        add(controlPanel);
        repositionControlPanel();
    }

    private void repositionControlPanel() {
        int controlPanelWidth = 250;
        int controlPanelHeight = 250;
        controlPanel.setBounds(getWidth() - controlPanelWidth - 10, 10, controlPanelWidth, controlPanelHeight);
    }

    private void startAnimation() {
        animationTimer = new Timer(INITIAL_TIMER_DELAY, (ActionEvent e) -> {
            for (VideoComponent vc : videoComponents) {
                vc.updatePosition();
            }
            repaint();
        });
        animationTimer.start();
    }

    private void adjustSpeed() {
        int delay = speedSlider.getValue();
        animationTimer.setDelay(delay);
    }

    private void adjustVideoSize() {
        int newSize = sizeSlider.getValue();
        for (VideoComponent vc : videoComponents) {
            vc.mediaPlayerComponent.setBounds(vc.x, vc.y, newSize, newSize);
            // Update the border for dark mode
            if (isDarkMode) {
                vc.mediaPlayerComponent.setBorder(new LineBorder(Color.GREEN, 2));
            }
        }
    }

    private void adjustNumberOfVideos() {
        int numVideos = numVideosSlider.getValue();
        initializeVideoComponents(numVideos);
    }

    private void randomizeVideos() {
        for (VideoComponent vc : videoComponents) {
            vc.dx = random.nextInt(7) + 3;
            vc.dy = random.nextInt(7) + 3;
            vc.dx *= random.nextBoolean() ? 1 : -1;
            vc.dy *= random.nextBoolean() ? 1 : -1;
        }
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        updateUIForDarkMode();
    }

    private void updateUIForDarkMode() {
        if (isDarkMode) {
            getContentPane().setBackground(Color.DARK_GRAY);
            controlPanel.setBackground(Color.DARK_GRAY);
            speedSlider.setBackground(Color.DARK_GRAY);
            sizeSlider.setBackground(Color.DARK_GRAY);
            numVideosSlider.setBackground(Color.DARK_GRAY);
            speedSlider.setForeground(Color.WHITE);
            sizeSlider.setForeground(Color.WHITE);
            numVideosSlider.setForeground(Color.WHITE);
            speedSlider.setBorder(new LineBorder(Color.GREEN, 1));
            sizeSlider.setBorder(new LineBorder(Color.GREEN, 1));
            numVideosSlider.setBorder(new LineBorder(Color.GREEN, 1));
        } else {
            getContentPane().setBackground(Color.LIGHT_GRAY);
            controlPanel.setBackground(Color.LIGHT_GRAY);
            speedSlider.setBackground(Color.LIGHT_GRAY);
            sizeSlider.setBackground(Color.LIGHT_GRAY);
            numVideosSlider.setBackground(Color.LIGHT_GRAY);
            speedSlider.setForeground(Color.BLACK);
            sizeSlider.setForeground(Color.BLACK);
            numVideosSlider.setForeground(Color.BLACK);
            speedSlider.setBorder(null);
            sizeSlider.setBorder(null);
            numVideosSlider.setBorder(null);
        }
        // Update borders for video components
        for (VideoComponent vc : videoComponents) {
            vc.mediaPlayerComponent.setBorder(isDarkMode ? new LineBorder(Color.GREEN, 2) : null);
        }
    }

    public static void main(String[] args) {
        // Set the path to the VLC libraries (specific to Apple Silicon / ARM64 architecture)
        System.setProperty("jna.library.path", "/Applications/VLC.app/Contents/MacOS/lib");

        SwingUtilities.invokeLater(() -> new VideoPlayer());
    }

    // Inner class to handle video component and movement logic
    private class VideoComponent {
        EmbeddedMediaPlayerComponent mediaPlayerComponent;
        int x, y;
        int dx, dy;

        public VideoComponent(EmbeddedMediaPlayerComponent mediaPlayerComponent, int x, int y) {
            this.mediaPlayerComponent = mediaPlayerComponent;
            this.x = x;
            this.y = y;

            // Random initial direction and speed
            dx = random.nextInt(7) + 3; // Speed between 3 and 9
            dy = random.nextInt(7) + 3;
            dx *= random.nextBoolean() ? 1 : -1; // Random direction
            dy *= random.nextBoolean() ? 1 : -1;

            // Set initial border color
            if (isDarkMode) {
                mediaPlayerComponent.setBorder(new LineBorder(Color.GREEN, 2));
            }
        }

        public void updatePosition() {
            x += dx;
            y += dy;

            // Bounce off walls
            if (x <= 0 || x >= getWidth() - mediaPlayerComponent.getWidth()) {
                dx = -dx;
            }
            if (y <= 0 || y >= getHeight() - mediaPlayerComponent.getHeight()) {
                dy = -dy;
            }

            mediaPlayerComponent.setLocation(x, y);
        }
    }
}
