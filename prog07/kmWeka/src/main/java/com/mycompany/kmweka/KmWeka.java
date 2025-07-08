/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.kmweka;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.SimpleKMeans;

/**
 *
 * @author kmikawa
 */
public class KmWeka {


    public static void main(String[] args) {
        try {
            // Irisデータセットの読み込み
            System.out.println("Loading data...");
            DataSource source = new DataSource("iris2D.arff");
            Instances data = source.getDataSet();
            System.out.println("Data loaded. Number of instances: " + data.numInstances());
            // 属性名を表示
            for (int i = 0; i < data.numAttributes(); i++) {
                System.out.println("Attribute " + (i + 1) + ": " + data.attribute(i).name());
            }
            
            // クラス属性の設定（ターゲット変数を設定）
            // カテゴリ属性を除外してクラスタリング
            // （arffファイルにカテゴリ属性も入ってしまっているので．これを利用しない）
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
            System.out.println("Class attribute set to: " + data.classAttribute().name());
            
            Instances clusterData = new Instances(data);
            clusterData.setClassIndex(-1);
            System.out.println("Class attribute removed for clustering. Number of attributes: " + clusterData.numAttributes());

            // k-meansクラスタリングの実行
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setNumClusters(3); // クラスターの数を設定
            kmeans.setPreserveInstancesOrder(true); // インスタンスの順序を保持
            kmeans.buildClusterer(clusterData); // clusterDataを対象としたクラスタリングを実施
            System.out.println("k-means clustering completed.");

            // クラスタリング結果の取得
            int[] assignments = kmeans.getAssignments();
            
            // クラスタ重心の出力
            Instances centroids = kmeans.getClusterCentroids();
            System.out.println("Cluster Centroids:");
            for (int i = 0; i < centroids.numInstances(); i++) {
                System.out.println(centroids.instance(i));
            }

            // クラスタごとのインスタンス数の出力
            double[] sizes = kmeans.getClusterSizes();
            System.out.println("\nCluster Sizes:");
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("Cluster " + (i+1) + ": " + sizes[i] + " instances");
                // 出力結果として得られる品種はそのクラスタに多く含まれるものが表示される．
            }

            // クラスタリング結果を表示
            displayClusterResults(data, assignments);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayClusterResults(Instances data, int[] assignments) {
        for (int i = 0; i < data.numInstances(); i++) {
            System.out.printf("Instance %d -> Cluster %d%n", i, assignments[i]);
        }
    }
}
