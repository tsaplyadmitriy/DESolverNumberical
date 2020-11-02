package sample;

import javafx.scene.chart.XYChart;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class NumericalMethods {


    private static ExecutorService executor
            = Executors.newSingleThreadExecutor();


    public static XYChart.Series<Double, Double> lteNumbericalEuler = new XYChart.Series<>();

    public static XYChart.Series<Double, Double> lteImpNumbericalEuler = new XYChart.Series<>();
    public static XYChart.Series<Double, Double> lteRungeKutta = new XYChart.Series<>();


    public static Future<XYChart.Series<Double, Double>> exactSolution(InitialData initialData, DoubleUnaryOperator y){
        return executor.submit(() -> {

         XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();


            for (double x = initialData.getX0(); x < initialData.getX(); x = x + 0.01) {
                series.getData().add(new XYChart.Data<Double, Double>(x, y.applyAsDouble(x)));
            }

            return series;
        });
    }


    public static Future<XYChart.Series<Double, Double>> numericalEuler(InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){

        return executor.submit(() -> {

            XYChart.Series<Double, Double> eulerSeries = new XYChart.Series<Double, Double>();

            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();

            double iter = 0.0;
            lteNumbericalEuler.getData().clear();
            lteNumbericalEuler.setName("Euler");
            while(x0<=initialData.getX()+h) {

                double lte = y.applyAsDouble(x0+ h) - y.applyAsDouble(x0) - h * y_prime.apply(x0, y.applyAsDouble(x0));
                lteNumbericalEuler.getData().add(new XYChart.Data<Double, Double>(iter, lte));
                eulerSeries.getData().add(new XYChart.Data<Double, Double>(x0, y0));
                y0 = y0 + h * y_prime.apply(x0, y0);
                x0 = x0 + h;

                iter++;
            }

            return eulerSeries;
        });
    }


    public static Future<XYChart.Series<Double, Double>> numericalImprovedEuler(InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){

        return executor.submit(() -> {


            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();

            XYChart.Series<Double, Double> impEulerSeries = new XYChart.Series<Double, Double>();
            double iter = 0.0;
            lteImpNumbericalEuler.getData().clear();;
            lteImpNumbericalEuler.setName("Imp.Euler");
            while (x0 <= initialData.getX()+h) {

                double y_applied = y.applyAsDouble(x0);
                double yh_applied = y.applyAsDouble(x0 + h);
                double lte = yh_applied - y_applied + h * 0.5 * (y_prime.apply(x0, y_applied) + y_prime.apply(x0 + h, y.applyAsDouble(x0+h)));
                lteImpNumbericalEuler.getData().add(new XYChart.Data<Double, Double>(iter, lte));
                impEulerSeries.getData().add(new XYChart.Data<Double, Double>(x0, y0));
                y0 = y0 + 0.5 * h * (y_prime.apply(x0, y0) + y_prime.apply(x0 + h, y0 + h));
                x0 = x0 + h;
                iter++;
            }
            return impEulerSeries;
        });
    }



    public static Future<XYChart.Series<Double, Double>> numericalRungeKutta(InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){


        return executor.submit(() -> {

            XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();
            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();


            double iter  = 0.0;
            lteRungeKutta.getData().clear();
            lteRungeKutta.setName("Runge-Kutta");
            while(x0<=initialData.getX()+h){

                series.getData().add(new XYChart.Data<Double, Double>(x0, y0));

                double k1 = h*y_prime.apply(x0, y0);
                double k2 = h*y_prime.apply(x0 + 0.5*h, y0 + 0.5*k1);
                double k3 = h*y_prime.apply(x0 + 0.5*h, y0 + 0.5*k2);
                double k4 = h*y_prime.apply(x0 + h, y0 + k3);
                double lte = y.applyAsDouble(x0+h)-y.applyAsDouble(x0)- (1.0/6.0)*(k1 + 2*k2 + 2*k3 + k4);
                lteRungeKutta.getData().add(new XYChart.Data<Double, Double>(iter, lte));
                y0 = y0 + (1.0/6.0)*(k1 + 2*k2 + 2*k3 + k4);;

                x0 = x0+h;
                iter++;
            }

            return series;
        });
    }


}