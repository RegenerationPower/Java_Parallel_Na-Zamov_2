import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task2 extends RecursiveTask<double[]> {
    private final double a;
    private final double[] C;
    private final double[] D;
    private final double[][] MC;
    private final double[][] MM;
    private double[] A2;
    private final double[] result2;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;
    private final Data data;

    Task2(double a, double[] C, double[] D, double[][] MC, double[][] MM, double[] A2, double[] result2, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        this.a = a;
        this.C = C;
        this.D = D;
        this.MC = MC;
        this.MM = MM;
        this.A2 = A2;
        this.result2 = result2;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
        this.data = data;
    }

    @Override
    protected double[] compute() {
        A2 = data.addVectorToVector(data.multiplyVectorByMatrix(C, MC),
                data.multiplyVectorByScalar(data.multiplyVectorByMatrix(D, MM), a));
        lock.lock();
        try {
            System.arraycopy(A2, 0, result2, 0, A2.length);
            System.out.println("\nResult C*МС+D*MM*a: " + Arrays.toString(A2));
            writer.println("\nResult C*МС+D*MM*a: " + Arrays.toString(result2));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return A2;
    }

    public static ForkJoinTask<double[]> createTask(double a, double[] C, double[] D, double[][] MC, double[][] MM, double[] A2, double[] result2, PrintWriter writer, Lock lock, CountDownLatch latch, Data data) {
        return new Task2(a, C, D, MC, MM, A2, result2, writer, lock, latch, data);
    }
}