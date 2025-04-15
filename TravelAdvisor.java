
package model;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;

public class TravelAdvisor {
    private final J48 decisionTree;

    public TravelAdvisor() {
        decisionTree = new J48();
    }

    public void trainModel(double[][] features, String[] labels) throws Exception {
        ArrayList<Attribute> attributes = createAttributes();
        Instances dataset = createDataset(attributes, features, labels);
        decisionTree.buildClassifier(dataset);
    }

    public String predictBestTime(double[] features) throws Exception {
        ArrayList<Attribute> attributes = createAttributes();
        Instances dataset = new Instances("Prediction", attributes, 0);
        dataset.setClassIndex(attributes.size() - 1);

        DenseInstance instance = new DenseInstance(1.0, features);
        instance.setDataset(dataset);

        double prediction = decisionTree.classifyInstance(instance);
        return dataset.classAttribute().value((int) prediction);
    }

    private ArrayList<Attribute> createAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("temperature"));
        attributes.add(new Attribute("precipitation"));
        attributes.add(new Attribute("humidity"));
        attributes.add(new Attribute("traffic_level"));
        attributes.add(new Attribute("travel_time"));

        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("good");
        classValues.add("average");
        classValues.add("bad");
        attributes.add(new Attribute("recommendation", classValues));

        return attributes;
    }

    private Instances createDataset(ArrayList<Attribute> attributes, double[][] features, String[] labels) {
        Instances dataset = new Instances("TravelData", attributes, features.length);
        dataset.setClassIndex(attributes.size() - 1);

        for (int i = 0; i < features.length; i++) {
            DenseInstance instance = new DenseInstance(attributes.size());
            for (int j = 0; j < features[i].length; j++) {
                instance.setValue(attributes.get(j), features[i][j]);
            }
            instance.setValue(attributes.get(attributes.size() - 1), labels[i]);
            dataset.add(instance);
        }

        return dataset;
    }
}