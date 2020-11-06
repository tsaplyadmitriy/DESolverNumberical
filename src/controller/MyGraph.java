package controller;

import javafx.scene.chart.XYChart;

import java.util.function.Function;

public class MyGraph {

    private XYChart<Double, Double> graph;


    public MyGraph(final XYChart<Double, Double> graph) {

        this.graph = graph;


        graph.setLegendVisible(true);

    }


    public void plotData(  XYChart.Series<Double, Double> series){


        graph.getData().add(series);

    }


    public void clearData(){
        graph.getData().clear();
    }




}