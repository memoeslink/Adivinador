package com.app.memoeslink.adivinador;

import java.util.ArrayList;
import java.util.List;

public class PredictionHistory {
    List<Prediction> predictions;

    public PredictionHistory() {
        predictions = new ArrayList<>();
    }

    public void add(Prediction prediction) {
        if (prediction != null) {
            if (isFull())
                predictions.remove(0);
            predictions.add(prediction);
        }
    }

    public Prediction getOldest() {
        if (!isEmpty())
            return predictions.get(0);
        return new Prediction.PredictionBuilder().build();
    }

    public Prediction getLatest() {
        if (!isEmpty())
            return predictions.get(predictions.size() - 1);
        return new Prediction.PredictionBuilder().build();
    }

    public Prediction dispenseOldest() {
        if (!isEmpty()) {
            Prediction prediction = predictions.get(0);
            predictions.remove(0);
            return prediction;
        }
        return new Prediction.PredictionBuilder().build();
    }

    public Prediction dispenseLatest() {
        if (!isEmpty()) {
            Prediction prediction = predictions.get(predictions.size() - 1);
            predictions.remove(predictions.get(predictions.size() - 1));
            return prediction;
        }
        return new Prediction.PredictionBuilder().build();
    }

    public void removeOldest() {
        if (!isEmpty())
            predictions.remove(0);
    }

    public void removeLatest() {
        if (!isEmpty())
            predictions.remove(predictions.size() - 1);
    }

    public void clearHistory() {
        predictions.clear();
    }

    public boolean isEmpty() {
        return predictions.isEmpty();
    }

    public boolean isFull() {
        return predictions.size() >= 1000;
    }
}
