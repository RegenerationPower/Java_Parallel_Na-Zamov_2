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
            ForkJoinPool pool = new ForkJoinPool();

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


            Task1 task1 = (Task1) Task1.createTask(B, MT, A1, result1, writer, lock, latch, data);
            result1 = pool.invoke(task1);

            Task2 task2 = (Task2) Task2.createTask(a, C, D, MC, MM, A2, result2, writer, lock, latch, data);
            result2 = pool.invoke(task2);

            Task3 task3 = (Task3) Task3.createTask(MC, MT, MM, MA1, result3, writer, lock, latch, data);
            result3 = pool.invoke(task3);

            Task4 task4 = (Task4) Task4.createTask(B, D, MD, MT, MA2, result4, writer, lock, latch, data);
            result4 = pool.invoke(task4);

            Task5 task5 = (Task5) Task5.createTask(result1, result2, result3, result4, A, MA, latch, data);
            Task5Result task5Result = pool.invoke(task5);

            A = task5Result.getA();
            MA = task5Result.getMA();

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