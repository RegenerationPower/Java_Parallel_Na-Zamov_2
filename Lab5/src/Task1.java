import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task1 extends RecursiveTask<double[]> {
    private final double[] B;
    private final double[][] MT;
    private double[] A1;
    private final double[] result1;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;
    private final Data data;

    Task1(double[] B, double[][] MT, double[] A1, double[] result1, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        this.B = B;
        this.MT = MT;
        this.A1 = A1;
        this.result1 = result1;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
        this.data = data;
    }

    @Override
    protected double[] compute() {
        A1 = data.multiplyVectorByMatrix(B, MT);
        lock.lock();
        try {
            System.arraycopy(A1, 0, result1, 0, A1.length);
            System.out.println("\nResult B*MT: " + Arrays.toString(A1));
            writer.println("\nResult B*MT: " + Arrays.toString(result1));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return A1;
    }

    public static ForkJoinTask<double[]> createTask(double[] B, double[][] MT, double[] A1, double[] result1, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        return new Task1(B, MT, A1, result1, writer, lock, latch, data);
    }
}