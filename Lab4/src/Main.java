/*
А=C*МС+D*MM*a-B*MT
MА=max(B-D)*MD*MT-MC*(MT+MM)
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final String INPUT = "src\\input.txt";
    private static final String OUTPUT = "src\\output.txt";
    private static double[] A1;
    private static double[] A2;
    private static double[] A;
    private static double[][] MA1;
    private static double[][] MA2;
    private static double[][] MA;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Data data = new Data();
        try {
            String outputFilePath = new File(OUTPUT).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);
            String inputFilePath = new File(INPUT).getAbsolutePath();
            Scanner scanner = new Scanner(new File(inputFilePath));
            int sizeMC = scanner.nextInt();
            int sizeMD = scanner.nextInt();
            int sizeMM = scanner.nextInt();
            int sizeMT = scanner.nextInt();
            int sizeC = scanner.nextInt();
            int sizeB = scanner.nextInt();
            int sizeD = scanner.nextInt();
            double a = scanner.nextDouble();
            scanner.close();

            A1 = new double[sizeC];
            A2 = new double[sizeC];
            A = new double[sizeC];
            MA1 = new double[sizeMC][sizeMC];
            MA2 = new double[sizeMC][sizeMC];
            MA = new double[sizeMC][sizeMC];
            double[] result1 = new double[sizeC];
            double[] result2 = new double[sizeC];
            double[][] result3 = new double[sizeMC][sizeMC];
            double[][] result4 = new double[sizeMC][sizeMC];
            CountDownLatch latch = new CountDownLatch(4);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Lock lock = new ReentrantLock();

            double[][] MC = new double[sizeMC][sizeMC];
            double[][] MD = new double[sizeMD][sizeMD];
            double[][] MM = new double[sizeMM][sizeMM];
            double[][] MT = new double[sizeMT][sizeMT];
            double[] C = new double[sizeC];
            double[] B = new double[sizeB];
            double[] D = new double[sizeD];

            data.readMatrix("src/Data/MC.txt", MC);
            data.readMatrix("src/Data/MD.txt", MD);
            data.readMatrix("src/Data/MM.txt", MM);
            data.readMatrix("src/Data/MT.txt", MT);
            data.readVector("src/Data/C.txt", C);
            data.readVector("src/Data/B.txt", B);
            data.readVector("src/Data/D.txt", D);

            Callable<double[]> task1 = () -> {
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
            };

            Callable<double[]> task2 = () -> {
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
            };

            Callable<double[][]> task3 = () -> {
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
            };

            Callable<double[][]> task4 = () -> {
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
            };

            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized(Main.class) {
                    A = data.subtractVectors(A2, A1);
                    MA = data.subtractMatrices(MA2, MA1);
                }
            });

            executor.submit(task1);
            executor.submit(task2);
            executor.submit(task3);
            executor.submit(task4);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }

            System.out.println("\nFinal results: \nA = " + Arrays.toString(A) + "\n\nMA = " + Arrays.deepToString(MA));
            writer.println("\nFinal results: \nA = " + Arrays.toString(A) + "\n\nMA = " + Arrays.deepToString(MA));

            long endTime = System.nanoTime();
            long resultTime = (endTime - startTime);
            System.out.println("\nProgram time: " + resultTime + " ns");
            writer.println("\nProgram time: " + resultTime + " ns");
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}