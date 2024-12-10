import javax.swing.*; // Для створення графічного інтерфейсу користувача (GUI).
import java.awt.*; // Для роботи з графікою, створення об'єктів Robot, Rectangle тощо.
import java.awt.event.ActionEvent; // Для обробки подій (наприклад, натискання кнопок).
import java.awt.event.ActionListener; // Інтерфейс для відслідкування подій.
import java.awt.image.BufferedImage; // Для роботи з буферизованими зображеннями.
import java.io.File; // Для роботи з файловою системою.
import java.io.IOException; // Для обробки виключень вводу/виводу.
import java.util.ArrayList; // Для зберігання списку кадрів.
import javax.imageio.ImageIO; // Для збереження зображень у файли.

/**
 * ScreenRecorderApp - програма для запису екрану, відтворення записаних кадрів
 * і збереження їх у файли зображень.
 */
public class ScreenRecorderApp {

    // Для перевірки, чи триває запис.
    private boolean isRecording = false;

    // Список для зберігання кадрів екрану.
    private ArrayList<BufferedImage> frames = new ArrayList<>();

    // Таймер для періодичного захоплення кадрів.
    private Timer timer;

    /**
     * Точка входу в програму. Викликає запуск графічного інтерфейсу.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScreenRecorderApp::new);
    }

    /**
     * Конструктор. Налаштовує головне вікно програми і додає кнопки для керування.
     */
    public ScreenRecorderApp() {
        // Створення головного вікна програми.
        JFrame frame = new JFrame("Screen Recorder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        // Створення кнопок для керування записом.
        JButton startButton = new JButton("Start Recording");
        JButton stopButton = new JButton("Stop Recording");
        JButton playButton = new JButton("Play Recording");

        // Додавання відслідковування натискання кнопок.
        startButton.addActionListener(e -> startRecording());
        stopButton.addActionListener(e -> stopRecording());
        playButton.addActionListener(e -> playRecording());

        // Додавання кнопок у вікно.
        frame.add(startButton);
        frame.add(stopButton);
        frame.add(playButton);

        frame.setVisible(true);
    }

    /**
     * Починає запис екрану, захоплюючи кадри через регулярні інтервали часу.
     */
    private void startRecording() {
        if (isRecording) {
            JOptionPane.showMessageDialog(null, "Recording is already in progress!");
            return;
        }

        isRecording = true;
        frames.clear(); // Очищення списку кадрів перед новим записом.

        // Налаштування таймера для захоплення кадрів.
        timer = new Timer(1000 / 19, new ActionListener() { // Частота: 19 кадрів на секунду.
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Robot robot = new Robot(); // Використовується для захоплення екрану.
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                    frames.add(screenCapture);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });
        timer.start();
        JOptionPane.showMessageDialog(null, "Recording started!");
    }

    /**
     * Зупиняє запис екрана і зберігає кадри як зображення у файли.
     */
    private void stopRecording() {
        if (!isRecording) {
            JOptionPane.showMessageDialog(null, "Recording is not started yet!");
            return;
        }

        isRecording = false;
        timer.stop(); // Зупинка таймера.

        try {
            saveFramesAsImages(); // Збереження кадрів.
            JOptionPane.showMessageDialog(null, "Recording stopped and saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Зберігає кожен кадр у вигляді PNG-файлу в директорії "frames".
     *
     * @throws IOException якщо виникають помилки під час запису файлів.
     */
    private void saveFramesAsImages() throws IOException {
        File outputDir = new File("resources");
        if (!outputDir.exists()) {
            outputDir.mkdir(); // Створення директорії, якщо вона не існує.
        }

        for (int i = 0; i < frames.size(); i++) {
            File outputFile = new File(outputDir, "frame_" + i + ".png");
            ImageIO.write(frames.get(i), "png", outputFile); // Запис кадру у файл.
        }
    }

    /**
     * Відтворює записані кадри у новому вікні з використанням JLabel.
     */
    private void playRecording() {
        if (frames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No recording to play!");
            return;
        }

        // Створення вікна для відтворення запису.
        JFrame playerFrame = new JFrame("Video Player");
        playerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        playerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Відкриття у повноекранному режимі.

        JLabel label = new JLabel(); // Відображення кадрів у JLabel.
        playerFrame.add(label);
        playerFrame.setVisible(true);

        // Таймер для послідовного показу кадрів.
        new Timer(1000 / 19, new ActionListener() {
            private int currentFrame = 0; // Поточний кадр.

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFrame >= frames.size()) {
                    ((Timer) e.getSource()).stop(); // Зупинка відтворення після останнього кадру.
                    return;
                }
                label.setIcon(new ImageIcon(frames.get(currentFrame))); // Встановлення іконки кадру.
                currentFrame++;
            }
        }).start();
    }
}
