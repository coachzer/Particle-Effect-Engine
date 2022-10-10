import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimedExit {

    Timer timer = new Timer();
    final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            System.out.println("Length of fpsArray: " + Main.fpsList.size());
            System.out.println("Average frames per second: " + Main.averageFps());
            long end = System.currentTimeMillis();
            System.out.println("Time running: " + ((end - Main.start) / 1000) + " seconds.");
            System.exit(0);
        }
    };

    public TimedExit() {
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + 10 * 1000));
    }
}
