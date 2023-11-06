// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class SliderExampleApplication {
    private JFrame frame;
    private JTextField sliderValueField;
    private JSlider slider;
    private JLabel priorityLabel1;
    private JTextField priorityField1;
    private JLabel priorityLabel2;
    private JTextField priorityField2;
    private JButton startBothButton;
    private JButton stopBothButton;
    private JButton startButton1;
    private JButton startButton2;
    private JButton stopButton1;
    private JButton stopButton2;

    private int semaphore = 0; // Глобальна змінна для семафора
    private Thread thread1;
    private Thread thread2;

    public SliderExampleApplication() {
        frame = new JFrame("Slider Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 230);

        sliderValueField = new JTextField(10);
        sliderValueField.setEditable(false);

        slider = new JSlider(10, 90);
        slider.setOrientation(JSlider.HORIZONTAL);
        slider.setValue(10);

        priorityLabel1 = new JLabel("Пріоритет потока 1:");
        priorityField1 = new JTextField("1", 5);

        priorityLabel2 = new JLabel("Пріоритет потока 2:");
        priorityField2 = new JTextField("10", 5);

        startBothButton = new JButton("Пуск обидва");
        stopBothButton = new JButton("СТОП обидва");
        startButton1 = new JButton("ПУСК 1");
        startButton2 = new JButton("ПУСК 2");
        stopButton1 = new JButton("СТОП 1");
        stopButton2 = new JButton("СТОП 2");

        addComponentsToFrame();
        addListeners();
    }

    private void addComponentsToFrame() {
        JPanel panel = new JPanel();
        panel.add(sliderValueField);
        panel.add(slider);
        panel.add(priorityLabel1);
        panel.add(priorityField1);
        panel.add(priorityLabel2);
        panel.add(priorityField2);
        panel.add(startBothButton);
        panel.add(stopBothButton);

        panel.add(startButton1);
        panel.add(startButton2);
        panel.add(stopButton1);
        panel.add(stopButton2);

        frame.add(panel);
    }

    private void addListeners() {
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                updateSliderValueField(value);
            }
        });

        startBothButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int priority1 = Integer.parseInt(priorityField1.getText());
                int priority2 = Integer.parseInt(priorityField2.getText());

                thread1 = new SliderThread(slider, priority1, 10);
                thread2 = new SliderThread(slider, priority2, 90);

                thread1.start();
                thread2.start();
                startButton1.setEnabled(false);
                startBothButton.setEnabled(false);
                startButton2.setEnabled(false);
                stopButton1.setEnabled(false);
                stopButton2.setEnabled(false);
            }
        });

        startButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (semaphore == 0) {
                    semaphore = 1;
                    thread1 = new SliderThread(slider, Thread.MIN_PRIORITY, 10);
                    thread1.start();
                    startButton1.setEnabled(false);
                    startBothButton.setEnabled(false);
                    stopBothButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Зайнято потоком 2");
                }
            }
        });

        startButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (semaphore == 0) {
                    semaphore = 2;
                    thread2 = new SliderThread(slider, Thread.MAX_PRIORITY, 90);
                    thread2.start();
                    startButton2.setEnabled(false);
                    startBothButton.setEnabled(false);
                    stopBothButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Зайнято потоком 1");
                }
            }
        });

        stopButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (thread1 != null) {
                    thread1.interrupt();
                    semaphore = 0;
                    thread1 = null;
                    System.out.println("Потік 1 зупинений");
                    startButton1.setEnabled(true);
                    startBothButton.setEnabled(true);
                    stopBothButton.setEnabled(true);
                }
            }
        });

        stopButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (thread2 != null) {
                    thread2.interrupt();
                    semaphore = 0;
                    thread2 = null;
                    System.out.println("Потік 2 зупинений");
                    startButton2.setEnabled(true);
                    startBothButton.setEnabled(true);
                    stopBothButton.setEnabled(true);
                }
            }
        });

        stopBothButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                Thread[] threads = new Thread[currentGroup.activeCount()];
                currentGroup.enumerate(threads);
                for (Thread thread : threads) {
                    if (thread.getName().startsWith("SliderThread-")) {
                        thread.interrupt();
                    }
                }
                startButton1.setEnabled(true);
                startBothButton.setEnabled(true);
                startButton2.setEnabled(true);
                stopButton1.setEnabled(true);
                stopButton2.setEnabled(true);
            }
        });
    }

    public void display() {
        frame.setVisible(true);
    }

    private void updateSliderValueField(int value) {
        sliderValueField.setText(Integer.toString(value));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SliderExampleApplication gui = new SliderExampleApplication();
                gui.display();
            }
        });
    }
}

class SliderThread extends Thread {
    private JSlider slider;
    private int priority;
    private int targetValue;

    public SliderThread(JSlider slider, int priority, int targetValue) {
        this.slider = slider;
        this.priority = priority;
        this.targetValue = targetValue;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int position = slider.getValue();
                if (targetValue == 10) {
                    if (position < 10) {
                        position = 10;
                        slider.setValue(position);
                    } else if (position > 10) {
                        position--;
                        slider.setValue(position);
                    }
                } else {
                    if (position > 90) {
                        position = 90;
                        slider.setValue(position);
                    } else if (position < 90) {
                        position++;
                        slider.setValue(position);
                    }
                }
                Thread.sleep(1000 / priority);
            } catch (InterruptedException ex) {
                if (targetValue == 10) {
                    System.out.println("Потік 1 перерваний");
                } else {
                    System.out.println("Потік 2 перерваний");
                }
                break;
            }
        }
    }
}