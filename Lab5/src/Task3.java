import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task3 extends RecursiveTask<double[][]> {
    private final double[][] MC;
    private final double[][] MT;
    private final double[][] MM;
    private double[][] MA1;
    private final double[][] result3;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;
    private final Data data;

    Task3(double[][] MC, double[][] MT, double[][] MM, double[][] MA1, double[][] result3, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        this.MC = MC;
        this.MT = MT;
        this.MM = MM;
        this.MA1 = MA1;
        this.result3 = result3;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
        this.data = data;
    }

    @Override
    protected double[][] compute() {
        MA1 = data.multiplyMatrixByMatrix(MC, data.addMatrixToMatrix(MT, MM));
        lock.lock();
        try {
            for (int i = 0; i < MA1.length; i++) {
                System.arraycopy(MA1[i], 0, result3[i], 0, MA1[i].length);
            }
            System.out.println("\nResult MC*(MT+MM): " + Arrays.deepToString(MA1));
            writer.println("\nResult MC*(MT+MM): " + Arrays.deepToString(result3));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return MA1;
    }

    public static ForkJoinTask<double[][]> createTask(double[][] MC, double[][] MT, double[][] MM, double[][] MA1, double[][] result3, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        return new Task3(MC, MT, MM, MA1, result3, writer, lock, latch, data);
    }
}