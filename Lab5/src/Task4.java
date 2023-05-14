import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task4 extends RecursiveTask<double[][]> {
    private final double[] B;
    private final double[] D;
    private final double[][] MD;
    private final double[][] MT;
    private double[][] MA2;
    private final double[][] result4;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;
    private final Data data;

    Task4(double[] B, double[] D, double[][] MD, double[][] MT, double[][] MA2, double[][] result4, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        this.B = B;
        this.D = D;
        this.MD = MD;
        this.MT = MT;
        this.MA2 = MA2;
        this.result4 = result4;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
        this.data = data;
    }

    @Override
    protected double[][] compute() {
        MA2 = data.multiplyMatrixByMatrix(
                data.multiplyMatrixByScalar(data.findMaxValue(data.subtractVectors(B, D)), MD), MT);
        lock.lock();
        try {
            for (int i = 0; i < MA2.length; i++) {
                System.arraycopy(MA2[i], 0, result4[i], 0, MA2[i].length);
            }
            System.out.println("\nResult max(B-D)*MD*MT: " + Arrays.deepToString(MA2));
            writer.println("\nResult max(B-D)*MD*MT: " + Arrays.deepToString(result4));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return MA2;
    }

    public static ForkJoinTask<double[][]> createTask(double[] B, double[] D, double[][] MD, double[][] MT, double[][] MA2, double[][] result4, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        return new Task4(B, D, MD, MT, MA2, result4, writer, lock, latch, data);
    }
}