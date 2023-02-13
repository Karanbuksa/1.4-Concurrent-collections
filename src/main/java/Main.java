import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static BlockingQueue<String> queueFor_a = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueFor_b = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueFor_c = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        Thread fillingTheQueue = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String string = generateText("abc", 100000);
                try {
                    queueFor_a.put(string);
                    queueFor_b.put(string);
                    queueFor_c.put(string);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread checkingFor_a = new Thread(() -> processingString("a*", queueFor_a));
        Thread checkingFor_b = new Thread(() -> processingString("b*", queueFor_b));
        Thread checkingFor_c = new Thread(() -> processingString("c*", queueFor_c));
        List<Thread> threads = new ArrayList<>();
        threads.add(checkingFor_a);
        threads.add(checkingFor_b);
        threads.add(checkingFor_c);
        fillingTheQueue.start();
        threads.forEach(Thread::start);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void processingString(String regex, BlockingQueue blockingQueue) {
        Pattern pattern = Pattern.compile(regex);
        int max = 0;
        String theLongestSequenceString = null;
        for (int i = 0; i < 10000; i++) {
            try {
                String string = (String) blockingQueue.take();
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    int f = matcher.end() - matcher.start();
                    if (f > max) {
                        max = f;
                        theLongestSequenceString = string;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(theLongestSequenceString);
    }
}
