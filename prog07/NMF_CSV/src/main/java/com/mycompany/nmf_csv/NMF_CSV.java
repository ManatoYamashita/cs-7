/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.nmf_csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;

/**
 *
 * @author kmikawa
 */
public class NMF_CSV {

    public static void main(String[] args) {
        // CSVファイルのパスを指定
        String csvFilePath = "wine.csv";
        
        // CSVファイルを読み込み、行列を生成
        double[][] matrix = readMatrixFromCSV(csvFilePath);

        // NMFを実行
        int numComponents = 3;
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
    private static double[][] readMatrixFromCSV(String filePath) {
        List<double[]> matrix = new ArrayList<>();
        try (Reader reader = new FileReader(filePath); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                double[] row = new double[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = Double.parseDouble(record.get(i));
                }
                matrix.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix.toArray(new double[0][0]);
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
