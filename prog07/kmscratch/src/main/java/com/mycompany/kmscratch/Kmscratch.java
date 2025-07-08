/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.kmscratch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 *
 * @author kmikawa
 */
public class Kmscratch {

    public static void main(String[] args) {
        try {
            // Irisデータセットの読み込み
            String filePath = "iris2DnoClass.csv";
            List<double[]> data = loadCSV(filePath);
            int numClusters = 3;
            int maxIterations = 100; // 最大繰り返し回数

            // クラスターの初期化とK-meansクラスタリングの実行
            KMeans kmeans = new KMeans(numClusters, maxIterations);
            int[] assignments = kmeans.cluster(data);

            // クラスタリング結果を表示
            displayClusterAssignments(data, assignments);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<double[]> loadCSV(String filePath) throws IOException {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // ヘッダー行を読み飛ばす
                }
                if (line.charAt(0) == '\uFEFF') {
                    line = line.substring(1);
                }
                String[] values = line.split(",");
                double[] instance = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                data.add(instance);
            }
        }
        return data;
    }

    private static void displayClusterAssignments(List<double[]> data, int[] assignments) {
        for (int i = 0; i < data.size(); i++) {
            System.out.printf("Instance %d -> Cluster %d%n", i, assignments[i]);
        }
    }

    static class KMeans {
        private final int numClusters;
        private final int maxIterations;
        private List<double[]> centroids;

        public KMeans(int numClusters, int maxIterations) {
            this.numClusters = numClusters;
            this.maxIterations = maxIterations;
            this.centroids = new ArrayList<>();
        }

        public int[] cluster(List<double[]> data) {
            initializeCentroids(data);
            int[] assignments = new int[data.size()];
            boolean changed;
            int iterations = 0;

            do {
                changed = false;
                for (int i = 0; i < data.size(); i++) {
                    int newCluster = nearestCentroid(data.get(i));
                    if (assignments[i] != newCluster) {
                        changed = true;
                        assignments[i] = newCluster;
                    }
                }
                updateCentroids(data, assignments);
                iterations++;
            } while (changed && iterations < maxIterations);

            System.out.printf("Converged in %d iterations.%n", iterations);
            return assignments;
        }

        private void initializeCentroids(List<double[]> data) {
            Random rand = new Random();
            for (int i = 0; i < numClusters; i++) {
                int index = rand.nextInt(_______);  // 入力されたデータのサイズ分乱数を生成
                centroids.add(data.get(index));
            }
        }

        private int nearestCentroid(double[] instance) {
            // 一番近いクラスタ重心への距離を算出するためのメソッド
            double minDistance = Double.MAX_VALUE;
            int cluster = 0;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = euclideanDistance(instance, centroids.get(i));
                /*
                    ここに，ユークリッド距離最小となるセントロイドの計算と
                    そのセントロイドのクラスタ番号を格納するめの処理を追加
                */
            }
            return cluster;
        }

        private void updateCentroids(List<double[]> data, int[] assignments) {
            int[] counts = new int[numClusters];
            List<double[]> newCentroids = new ArrayList<>(numClusters);
            for (int i = 0; i < numClusters; i++) {
                newCentroids.add(new double[data.get(0).length]);
            }

            for (int i = 0; i < data.size(); i++) {
                int cluster = assignments[i];
                counts[cluster]++;
                for (int j = 0; j < data.get(i).length; j++) {
                    newCentroids.get(cluster)[j] += data.get(i)[j];
                }
            }

            for (int i = 0; i < numClusters; i++) {
                for (int j = 0; j < newCentroids.get(i).length; j++) {
                    newCentroids.get(i)[j] /= counts[i];
                }
                centroids.set(i, newCentroids.get(i));
            }
        }

        private double euclideanDistance(double[] a, double[] b) {
            double sum = 0;
            // a, bのユークリッド距離を測定するための処理を作成しよう
        }
    }
}