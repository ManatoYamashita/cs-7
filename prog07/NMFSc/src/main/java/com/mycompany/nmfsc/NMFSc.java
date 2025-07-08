/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.nmfsc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author kmikawa
 */
public class NMFSc {
    private static final int ROWS = 10;
    private static final int COLS = 20;
    private static final double DENSITY = 0.7;


    public static void main(String[] args) {
        // ランダムでスパースな行列を生成
        double[][] matrix = generateRandomSparseMatrix(ROWS, COLS, DENSITY);

        // 行列をCSV形式で保存
        saveMatrixToCSV(matrix, "random_sparse_matrix.csv");

        // NMFを実行
        int numComponents = 5;
        int maxIterations = 5000;
        double tolerance = 1e-6;
        NMF nmf = new NMF(matrix, numComponents, maxIterations, tolerance);
        nmf.factorize();

        // NMFの結果を表示
        System.out.println("Basis Matrix W:");
        for (double[] row : nmf.getW()) {
            for (double val : row) {
                System.out.printf("%.4f ", val);
            }
            System.out.println();
        }

        System.out.println("Coefficient Matrix H:");
        for (double[] row : nmf.getH()) {
            for (double val : row) {
                System.out.printf("%.4f ", val);
            }
            System.out.println();
        }
    }
    private static double[][] generateRandomSparseMatrix(int rows, int cols, double density) {
            Random rand = new Random(42);
            double[][] matrix = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (rand.nextDouble() < density) {
                        matrix[i][j] = rand.nextDouble();
                    } else {
                        matrix[i][j] = 0;
                    }
                }
            }
            return matrix;
        }

    private static void saveMatrixToCSV(double[][] matrix, String filePath) {
            try (FileWriter writer = new FileWriter(filePath)) {
                for (double[] row : matrix) {
                    for (int i = 0; i < row.length; i++) {
                        writer.append(Double.toString(row[i]));
                        if (i < row.length - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

class NMF {

    private final int rows;
    private final int cols;
    private final int numComponents;
    private double[][] V;
    private double[][] W;
    private double[][] H;
    private final int maxIterations;
    private final double tolerance;

    public NMF(double[][] V, int numComponents, int maxIterations, double tolerance) {
        this.V = V;
        this.rows = V.length;
        this.cols = V[0].length;
        this.numComponents = numComponents;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.W = new double[rows][numComponents];
        this.H = new double[numComponents][cols];
        initializeMatrices();
    }

    private void initializeMatrices() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < numComponents; j++) {
                W[i][j] = rand.nextDouble();
            }
        }
        for (int i = 0; i < numComponents; i++) {
            for (int j = 0; j < cols; j++) {
                H[i][j] = rand.nextDouble();
            }
        }
    }

    public void factorize() {
        for (int iter = 0; iter < maxIterations; iter++) {
            double[][] WH = multiplyMatrices(W, H);

            // Update H
            for (int i = 0; i < numComponents; i++) {
                for (int j = 0; j < cols; j++) {
                    double numerator = 0.0;
                    double denominator = 0.0;
                    for (int k = 0; k < rows; k++) {
                        numerator += W[k][i] * V[k][j];
                        denominator += W[k][i] * WH[k][j];
                    }
                    H[i][j] *= numerator / Math.max(denominator, 1e-9);
                }
            }

            WH = multiplyMatrices(W, H);

            // Update W
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < numComponents; j++) {
                    double numerator = 0.0;
                    double denominator = 0.0;
                    for (int k = 0; k < cols; k++) {
                        numerator += V[i][k] * H[j][k];
                        denominator += WH[i][k] * H[j][k];
                    }
                    W[i][j] *= numerator / Math.max(denominator, 1e-9);
                }
            }

            // Check for convergence
            if (calculateError(V, multiplyMatrices(W, H)) < tolerance) {
                break;
            }
        }
    }

    private double calculateError(double[][] V, double[][] WH) {
        double error = 0.0;
        for (int i = 0; i < V.length; i++) {
            for (int j = 0; j < V[i].length; j++) {
                error += Math.pow(V[i][j] - WH[i][j], 2);
            }
        }
        return Math.sqrt(error);
    }

    private double[][] multiplyMatrices(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    public double[][] getW() {
        return W;
    }

    public double[][] getH() {
        return H;
    }
}
