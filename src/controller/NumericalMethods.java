package controller;

import controller.ComplexFunction;
import controller.InitialData;
import javafx.scene.chart.XYChart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleUnaryOperator;

public class NumericalMethods {


    private static ExecutorService executor
            = Executors.newSingleThreadExecutor();


    public static XYChart.Series<Double, Double> lteNumbericalEuler = new XYChart.Series<>();

    public static XYChart.Series<Double, Double> lteImpNumbericalEuler = new XYChart.Series<>();
    public static XYChart.Series<Double, Double> lteRungeKutta = new XYChart.Series<>();

    public static double gteNumEuler;
    public static double gteImpNumEuler;
    public static double gteRungeKutta;


    public static Future<XYChart.Series<Double, Double>> exactSolution(InitialData initialData, DoubleUnaryOperator y){
        return executor.submit(() -> {

         XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();


            for (double x = initialData.getX0(); x < initialData.getX(); x = x + 0.01) {
                series.getData().add(new XYChart.Data<Double, Double>(x, y.applyAsDouble(x)));
            }

            return series;
        });
    }


    public static Future<XYChart.Series<Double, Double>> numericalEuler(InitialData initialData, DoubleUnaryOperator y,
                                                                       ComplexFunction<Double, Double> y_prime){
        return executor.submit(() -> {

            XYChart.Series<Double, Double> eulerSeries = new XYChart.Series<Double, Double>();

            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();
            double iter = 0.0;
            lteNumbericalEuler.getData().clear();
            lteNumbericalEuler.setName("Euler");
            gteNumEuler = -1;
            while(x0<=initialData.getX()+h) {

                double lte = y.applyAsDouble(x0+ h) - y.applyAsDouble(x0) - h * y_prime.apply(x0, y.applyAsDouble(x0));
                lteNumbericalEuler.getData().add(new XYChart.Data<Double, Double>(iter, lte));
                eulerSeries.getData().add(new XYChart.Data<Double, Double>(x0, y0));
                y0 = y0 + h * y_prime.apply(x0, y0);
                x0 = x0 + h;

                if(y.applyAsDouble(x0) - y0 >gteNumEuler){
                    gteNumEuler = y.applyAsDouble(x0) - y0;
                }
                iter++;
            }
            return eulerSeries;
        });
    }


    public static Future<XYChart.Series<Double, Double>> numericalImprovedEuler(InitialData initialData, DoubleUnaryOperator y,
                                                                                ComplexFunction<Double, Double> y_prime){
        return executor.submit(() -> {
            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();

            XYChart.Series<Double, Double> impEulerSeries = new XYChart.Series<Double, Double>();
            double iter = 0.0;
            lteImpNumbericalEuler.getData().clear();;
            lteImpNumbericalEuler.setName("Imp.Euler");
            gteImpNumEuler = -1;
            while (x0 <= initialData.getX()+h) {

                double y_applied = y.applyAsDouble(x0);
                double yh_applied = y.applyAsDouble(x0 + h);
                double lte = yh_applied - y_applied + h * 0.5 * (y_prime.apply(x0, y_applied) + y_prime.apply(x0 + h, y.applyAsDouble(x0+h)));
                lteImpNumbericalEuler.getData().add(new XYChart.Data<Double, Double>(iter, Math.abs(lte)));
                impEulerSeries.getData().add(new XYChart.Data<Double, Double>(x0, y0));
                y0 = y0 + 0.5 * h * (y_prime.apply(x0, y0) + y_prime.apply(x0 + h, y0+ h*y_prime.apply(x0,y0)  ));
                x0 = x0 + h;
                if(y.applyAsDouble(x0) - y0 >gteImpNumEuler){
                    gteImpNumEuler = y.applyAsDouble(x0) - y0;
                }

                iter++;
            }
            return impEulerSeries;
        });
    }



    public static Future<XYChart.Series<Double, Double>> numericalRungeKutta(InitialData initialData,
                                                                             DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){
        return executor.submit(() -> {
            XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();
            double y0 = initialData.getY0();
            double x0 = initialData.getX0();
            double h = initialData.getStep();
            double iter  = 0.0;
            lteRungeKutta.getData().clear();
            lteRungeKutta.setName("Runge-Kutta");
            gteRungeKutta = -1;
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


                if(y.applyAsDouble(x0) - y0 >gteRungeKutta){
                    gteRungeKutta = y.applyAsDouble(x0) - y0;
                }
                iter++;
            }

            return series;
        });
    }

    public static Future<XYChart.Series<Double, Double>> gteEulerGraph(double startStep, double endStep,double ds, InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){

        XYChart.Series<Double, Double> eulseries = new XYChart.Series<Double, Double>();
        return executor.submit(() -> {

            for(double d = startStep;d<endStep;d = d +ds){
                initialData.setStep(d);
                System.out.print(" /"+d);
                double y0 = initialData.getY0();
                double x0 = initialData.getX0();
                double h = initialData.getStep();


                lteNumbericalEuler.getData().clear();
                lteNumbericalEuler.setName("Euler");
                gteNumEuler = -1;
                while(x0<=initialData.getX()+h) {

                    y0 = y0 + h * y_prime.apply(x0, y0);
                    x0 = x0 + h;

                    if(Math.abs(y.applyAsDouble(x0)) - y0 >gteNumEuler){
                        gteNumEuler = y.applyAsDouble(x0) - y0;
                    }


                }

                eulseries.getData().add(new XYChart.Data<Double, Double>(d,gteNumEuler));
            }
            System.out.println(eulseries.getData());
            eulseries.setName("Euler");
            return eulseries;

        });
    }

    public static Future<XYChart.Series<Double, Double>> gteImprovedEulerGraph(double startStep, double endStep,double ds,InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){
        XYChart.Series<Double, Double> impseries = new XYChart.Series<Double, Double>();
        return executor.submit(() -> {

            for(double d = startStep;d<endStep;d = d +ds){
                initialData.setStep(d);
                double y0 = initialData.getY0();
                double x0 = initialData.getX0();
                double h = initialData.getStep();


                lteImpNumbericalEuler.getData().clear();;
                lteImpNumbericalEuler.setName("Imp.Euler");
                gteImpNumEuler = -1;
                while (x0 <= initialData.getX()+h) {



                    y0 = y0 + 0.5 * h * (y_prime.apply(x0, y0) + y_prime.apply(x0 + h, y0+ h*y_prime.apply(x0,y0)  ));
                    x0 = x0 + h;
                    if(y.applyAsDouble(x0) - y0 >gteImpNumEuler){
                        gteImpNumEuler = y.applyAsDouble(x0) - y0;
                    }


                }
                impseries.getData().add(new XYChart.Data<Double, Double>(d,gteImpNumEuler));
            }
            System.out.println(impseries.getData());
            impseries.setName("Imp.Euler");
            return impseries;
        });
    }

    public static Future<XYChart.Series<Double, Double>> gteRungeKuttaGraph(double startStep, double endStep,double ds,InitialData initialData, DoubleUnaryOperator y, ComplexFunction<Double, Double> y_prime){
        XYChart.Series<Double, Double> rkseries = new XYChart.Series<Double, Double>();
        return executor.submit(() -> {
            for(double d = startStep;d<endStep;d = d +ds){
                initialData.setStep(d);
                double y0 = initialData.getY0();
                double x0 = initialData.getX0();
                double h = initialData.getStep();



                lteRungeKutta.getData().clear();
                lteRungeKutta.setName("Runge-Kutta");
                gteRungeKutta = -1;
                while(x0<=initialData.getX()+h){



                    double k1 = h*y_prime.apply(x0, y0);
                    double k2 = h*y_prime.apply(x0 + 0.5*h, y0 + 0.5*k1);
                    double k3 = h*y_prime.apply(x0 + 0.5*h, y0 + 0.5*k2);
                    double k4 = h*y_prime.apply(x0 + h, y0 + k3);

                    y0 = y0 + (1.0/6.0)*(k1 + 2*k2 + 2*k3 + k4);;

                    x0 = x0+h;


                    if(y.applyAsDouble(x0) - y0 >gteRungeKutta){
                        gteRungeKutta = y.applyAsDouble(x0) - y0;
                    }

                }
                rkseries.getData().add(new XYChart.Data<Double, Double>(d,gteRungeKutta));
            }
            System.out.println(rkseries.getData());
            rkseries.setName("Runge-Kutta");
            return rkseries;
        });
    }

}