import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class Task5 extends RecursiveTask<Task5Result> {
    private final double[] A1;
    private final double[] A2;
    private double[] A;
    private final double[][] MA1;
    private final double[][] MA2;
    private double[][] MA;
    private final CountDownLatch latch;
    private final Data data;

    Task5(double[] A1, double[] A2, double[][] MA1, double[][] MA2, double[] A, double[][] MA, CountDownLatch latch, Data data) {
        this.A1 = A1;
        this.A2 = A2;
        this.MA1 = MA1;
        this.MA2 = MA2;
        this.A = A;
        this.MA = MA;
        this.latch = latch;
        this.data = data;
    }

    @Override
    protected Task5Result compute() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
         A = data.subtractVectors(A2, A1);
         MA = data.subtractMatrices(MA2, MA1);
        return new Task5Result(A, MA);
    }

    public static ForkJoinTask<Task5Result> createTask(double[] A1, double[] A2, double[][] MA1, double[][] MA2, double[] A, double[][] MA, CountDownLatch latch, Data data) {
        return new Task5(A1, A2, MA1, MA2, A, MA, latch, data);
    }
}