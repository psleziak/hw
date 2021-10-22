/**
 * Assignment 6 -- Prisoner's Dilemma -- 2ip90
 * main class
 * 
 * @author FILL IN
 * @author FILL IN
 * assignment group FILL IN
 * 
 * assignment copyright Kees Huizing
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class PrisonersDilemma implements ActionListener, ChangeListener {
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 800;
    private static final int MAX_DEFECTION_AWARD = 3;
    private static final int DEFAULT_DEFECTION_AWARD = 1;
    private static final int MAX_SPEED = 60;
    private static final int DEFAULT_SPEED = 1;

    private PlayingField playingField;
    private JButton buttonGoPause;
    private JButton buttonReset;
    private JSlider sliderDefectionAward;
    private JLabel labelDefectionValue;
    private JSlider sliderSpeed;
    private JLabel labelSpeedValue;
    private JCheckBox checkBoxPreferOwnStrategy;
    
    private void buildGUI() {
        SwingUtilities.invokeLater( () -> {
            this.playingField = new PlayingField();
            this.playingField.setAlpha(DEFAULT_DEFECTION_AWARD);


            JLabel labelDefection = new JLabel("defection \u03B1 = ");
            this.sliderDefectionAward = new JSlider(0, MAX_DEFECTION_AWARD * 10, DEFAULT_DEFECTION_AWARD * 10);
            this.sliderDefectionAward.addChangeListener(this);
            this.sliderDefectionAward.setPaintLabels(true);
            this.sliderDefectionAward.setPaintTicks(true);
            this.sliderDefectionAward.setMajorTickSpacing(10);
            this.sliderDefectionAward.setLabelTable(this.getSliderDefectionAwardLabels());
            this.labelDefectionValue = new JLabel(this.getDefectionLabelText());
            JPanel panelDefection = new JPanel();
            panelDefection.setLayout(new FlowLayout(FlowLayout.LEFT));
            panelDefection.add(labelDefection);
            panelDefection.add(this.sliderDefectionAward);
            panelDefection.add(this.labelDefectionValue);

            JLabel labelSpeed = new JLabel("frequency = ");
            this.sliderSpeed = new JSlider(0, MAX_SPEED, DEFAULT_SPEED);
            this.sliderSpeed.addChangeListener(this);
            this.sliderSpeed.setPaintLabels(true);
            this.sliderSpeed.setPaintTicks(true);
            this.sliderSpeed.setMajorTickSpacing(10);
            this.labelSpeedValue = new JLabel(this.getSpeedLabelText());
            JPanel panelSpeed = new JPanel();
            panelSpeed.setLayout(new FlowLayout(FlowLayout.LEFT));
            panelSpeed.add(labelSpeed);
            panelSpeed.add(this.sliderSpeed);
            panelSpeed.add(this.labelSpeedValue);

            this.checkBoxPreferOwnStrategy = new JCheckBox("prefer own strategy", this.playingField.getPreferOwnStrategy());
            this.checkBoxPreferOwnStrategy.addChangeListener(this);

            this.buttonGoPause = new JButton("Go");
            this.buttonGoPause.addActionListener(this);

            this.buttonReset = new JButton("Reset");
            this.buttonReset.addActionListener(this);

            JPanel panelButtons = new JPanel();
            panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
            panelButtons.add(buttonGoPause);
            panelButtons.add(buttonReset);

            JPanel panelLeftControls = new JPanel();
            panelLeftControls.setLayout(new BorderLayout());
            panelLeftControls.add(panelDefection, BorderLayout.PAGE_START);
            panelLeftControls.add(panelSpeed, BorderLayout.PAGE_END);

            JPanel panelRightControls = new JPanel();
            panelRightControls.setLayout(new BorderLayout());
            panelRightControls.add(this.checkBoxPreferOwnStrategy, BorderLayout.PAGE_START);
            panelRightControls.add(panelButtons, BorderLayout.PAGE_END);

            JPanel panelControls = new JPanel();
            panelControls.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
            panelControls.add(panelLeftControls);
            panelControls.add(panelRightControls);

            JPanel containerPanel = new JPanel();
            containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            containerPanel.setLayout(new BorderLayout());
            containerPanel.add(this.playingField, BorderLayout.CENTER);
            containerPanel.add(panelControls, BorderLayout.PAGE_END);
            
            JFrame frame = new JFrame();
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            frame.add(containerPanel, BorderLayout.CENTER);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonGoPause) {
            this.playingField.toggleTimer();
            this.buttonGoPause.setText(this.playingField.isRunning() ? "Pause" : "Go");
        }
        else if (e.getSource() == this.buttonReset) {
            this.playingField.resetGrid();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == this.sliderDefectionAward) {
            this.playingField.setAlpha(Double.valueOf(this.sliderDefectionAward.getValue()) / 10);
            this.labelDefectionValue.setText(this.getDefectionLabelText());
        }        
        else if (e.getSource() == this.sliderSpeed) {
            this.playingField.setSpeed(this.sliderSpeed.getValue());
            this.labelSpeedValue.setText(this.getSpeedLabelText());
        }        
        else if (e.getSource() == this.checkBoxPreferOwnStrategy) {
            this.playingField.setPreferOwnStrategy(this.checkBoxPreferOwnStrategy.isSelected());
        }        
    }

    private Dictionary<Integer, JLabel> getSliderDefectionAwardLabels() {
        Dictionary<Integer, JLabel> labels = new Hashtable<>();
        for (int index = 0; index <= MAX_DEFECTION_AWARD; index++) {
            String text = String.format("%.1f", Double.valueOf(index));
            labels.put(index * 10, new JLabel(text));
        }

        return labels;
    }

    private String getDefectionLabelText() {
        return "(" + this.playingField.getAlpha() + ")";
    }

    private String getSpeedLabelText() {
        return "(" + this.playingField.getSpeed() + ")";
    }
    
    public static void main( String[] a ) {
        (new PrisonersDilemma()).buildGUI();
    }
}
