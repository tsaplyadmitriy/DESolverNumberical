package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;


import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Controller implements Initializable {


    private boolean isToggledToShowErrors = false;

    @FXML
    private LineChart<Double, Double> equationGraph;

    @FXML
    AnchorPane chartPane;


    @FXML
    private NumberAxis xAxis ;

    @FXML
    private NumberAxis yAxis ;

    @FXML
    public Button redrawButton ;

    @FXML
    public TextField textX0 ;

    @FXML
    public TextField textY0 ;

    @FXML
    public TextField textUpperX ;

    @FXML
    public TextField textGridStep ;

    @FXML
    public CheckBox exactCheck;

    @FXML
    public CheckBox eulerCheck;

    @FXML
    public CheckBox impEulerCheck;

    @FXML
    public CheckBox rungeKuttaCheck;


    @FXML
    public Label errorLabel;

    @FXML
    public TextField textTimeLimit;

    @FXML
    ToggleButton showLTEButton;

    @FXML
    Button investigateGTEButton;

    @FXML
    public TextField gteStart;

    @FXML
    public TextField gteEnd;

    @FXML
    public TextField gteIncrement;

    private MyGraph mathsGraph;
    private InitialData currentData;

    XYChart.Series<Double, Double> exactSeries;
    XYChart.Series<Double, Double> eulerSeries;
    XYChart.Series<Double, Double> impEulerSeries;
    XYChart.Series<Double, Double> rungeKuttaSeries;

    boolean [] graphChecks = {true,true,true,true};



    public void initialize(URL url, ResourceBundle resourceBundle) {

        InitialData launchData = new InitialData(6.0, 1.0, 2.0, 0.01, 4000);
        currentData = launchData;

        setInitialDatatoGUI(currentData); //updates giui with launchData

        mathsGraph = new MyGraph(equationGraph);

        setGraphGridSettings(launchData); //sets up axis sizes
        setGraphNumberFormat();//sets up axis number format
        setListeners(); //sets up listeners for buttons and checkboxes

        drawGraphs(launchData);

    }


    private void drawEquationGraph(InitialData initialData) throws ExecutionException, InterruptedException, TimeoutException {

       exactSeries = null;
      exactSeries = NumericalMethods.exactSolution(
                initialData,
                (x->y(x,initialData))).get(initialData.getTL(), TimeUnit.MILLISECONDS);

        exactSeries.setName("Exact Solution");
        mathsGraph.plotData(exactSeries);


    }

    private void drawEulerGraph(InitialData initialData) throws ExecutionException, InterruptedException, TimeoutException {



       eulerSeries = NumericalMethods.numericalEuler(
               initialData,
                (x->y(x,initialData)),
                y_prime).get(initialData.getTL(), TimeUnit.MILLISECONDS);

        eulerSeries.setName("Euler");
        mathsGraph.plotData(eulerSeries);


    }

    private void drawImprovedEuler(InitialData initialData) throws InterruptedException, ExecutionException, TimeoutException {

        impEulerSeries = NumericalMethods.numericalImprovedEuler(
                initialData,
                (x->y(x,initialData)),
                y_prime).get(initialData.getTL(), TimeUnit.MILLISECONDS);

        impEulerSeries.setName("Imp.Euler");



        mathsGraph.plotData(impEulerSeries);

    }

    private void drawRungeKutta(InitialData initialData) throws InterruptedException, ExecutionException, TimeoutException {

         rungeKuttaSeries = NumericalMethods.numericalRungeKutta(
                initialData,
                (x->y(x,initialData)),
                y_prime).get(initialData.getTL(), TimeUnit.MILLISECONDS);
         rungeKuttaSeries.setName("Runge-Kutta");

        mathsGraph.plotData(rungeKuttaSeries);

    }


    public void drawGraphs(InitialData data){
        try {

            if(graphChecks[0]){
                drawEquationGraph(data);
            }
            if(graphChecks[1]){
                drawEulerGraph(data);
            }
            if(graphChecks[2]){
                drawImprovedEuler(data);
            }
            if(graphChecks[3]){
                drawRungeKutta(data);
            }




            errorLabel.setText("");
        }catch (TimeoutException e){
            errorLabel.setText("Time limit has exceeded");
        }
        catch (ExecutionException | InterruptedException e){

            errorLabel.setText("Computation has failed");
        }
    }

    private void drawGraphsFromData(){

        mathsGraph.clearData();;
        equationGraph.getData().clear();

        mathsGraph.plotData(exactSeries);

        mathsGraph.plotData(eulerSeries);

        mathsGraph.plotData(impEulerSeries);

        mathsGraph.plotData(rungeKuttaSeries);

    }
    private void drawErrorsFromData(){
         mathsGraph.clearData();
        System.out.println(NumericalMethods.lteNumbericalEuler.getData());
        System.out.println(NumericalMethods.lteImpNumbericalEuler.getData());
        System.out.println(NumericalMethods.lteRungeKutta.getData());
        mathsGraph.plotData(NumericalMethods.lteNumbericalEuler);
        mathsGraph.plotData(NumericalMethods.lteImpNumbericalEuler);
        mathsGraph.plotData(NumericalMethods.lteRungeKutta);


    }


    private InitialData getInitialData(){
        return currentData = new InitialData(textUpperX.getText(), textX0.getText(), textY0.getText(), textGridStep.getText(), textTimeLimit.getText());
    }

    private void setInitialDatatoGUI(InitialData launchData){
        textX0.setText("" + launchData.getX0());
        textY0.setText("" + launchData.getY0());
        textUpperX.setText("" + launchData.getX());
        textGridStep.setText("" + launchData.getStep());
        textTimeLimit.setText("" + launchData.getTL());
        gteStart.setText(""+0.01);
        gteEnd.setText(""+1);
        gteIncrement.setText(""+0.001);
        gteIncrement.setText(""+0.001);

    }


    private void setListeners(){


        investigateGTEButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            mathsGraph.clearData();;

            try {


                double startStep = Double.parseDouble(gteStart.getText());
                double endStep = Double.parseDouble(gteEnd.getText());
                double stepInc = Double.parseDouble(gteIncrement.getText());


                XYChart.Series<Double, Double> gteEuler = NumericalMethods.gteEulerGraph(startStep,endStep,stepInc,currentData,(x->y(x,currentData)),y_prime).get();
                XYChart.Series<Double, Double> gteImpEuler = NumericalMethods.gteImprovedEulerGraph(startStep,endStep,stepInc,currentData,(x->y(x,currentData)),y_prime).get();
                XYChart.Series<Double, Double> gteRungeKutta = NumericalMethods.gteRungeKuttaGraph(startStep,endStep,stepInc,currentData,(x->y(x,currentData)),y_prime).get();
                xAxis.setLowerBound(gteEuler.getData().get(0).getXValue());

                xAxis.setUpperBound(gteEuler.getData().get(gteEuler.getData().size()-1).getXValue());
                xAxis.setTickUnit(gteEuler.getData().get(0).getXValue()/10);


                yAxis.setLowerBound(gteEuler.getData().get(0).getYValue());
                yAxis.setUpperBound(gteEuler.getData().get(gteEuler.getData().size()-1).getYValue());
                yAxis.setTickUnit(gteEuler.getData().get(gteEuler.getData().size()-1).getYValue()/50);

                mathsGraph.plotData(gteEuler);
                mathsGraph.plotData(gteImpEuler);
                mathsGraph.plotData(gteRungeKutta);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (NumberFormatException ex) {
                errorLabel.setText("Wrong format of input");

            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }

        });



        redrawButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

            errorLabel.setText("");
            InitialData currentData = null;
            try {

                currentData = new InitialData(textUpperX.getText(), textX0.getText(), textY0.getText(), textGridStep.getText(), textTimeLimit.getText());


            } catch (NumberFormatException ex) {
                errorLabel.setText("Wrong format of input");

            }

            if (currentData != null) {

                if (currentData.getX() <= currentData.getX0()) {
                    errorLabel.setText("X cannot be less than X0");
                } else {

                    isToggledToShowErrors = false;
                    showLTEButton.setSelected(false);
                    setGraphGridSettings(currentData);
                    mathsGraph.clearData();

                    drawGraphs(currentData);
                }
            }
        });

        showLTEButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if(isToggledToShowErrors){
                    exactCheck.setDisable(false);
                    eulerCheck.setDisable(false);
                    impEulerCheck.setDisable(false);
                    rungeKuttaCheck.setDisable(false);
                    setGraphGridSettings(currentData);
                    drawGraphsFromData();
                    isToggledToShowErrors = false;
                }else{

                    exactCheck.setDisable(true);
                    eulerCheck.setDisable(true);
                    impEulerCheck.setDisable(true);
                    rungeKuttaCheck.setDisable(true);

                    drawErrorsFromData();

                    xAxis.setLowerBound(NumericalMethods.lteNumbericalEuler.getData().get(0).getXValue());

                    xAxis.setUpperBound(NumericalMethods.lteNumbericalEuler.getData().get(NumericalMethods.lteNumbericalEuler.getData().size()-1).getXValue());
                    xAxis.setTickUnit(NumericalMethods.lteNumbericalEuler.getData().get(0).getXValue()/50);


                    yAxis.setLowerBound(NumericalMethods.lteNumbericalEuler.getData().get(0).getYValue());
                    yAxis.setUpperBound(NumericalMethods.lteNumbericalEuler.getData().get(NumericalMethods.lteNumbericalEuler.getData().size()-1).getYValue());
                    yAxis.setTickUnit(NumericalMethods.lteNumbericalEuler.getData().get(NumericalMethods.lteNumbericalEuler.getData().size()-1).getYValue()/50);

                    isToggledToShowErrors = true;
                }


            }
        });

        exactCheck.setSelected(true);
        eulerCheck.setSelected(true);
        impEulerCheck.setSelected(true);
        rungeKuttaCheck.setSelected(true);

        exactCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(graphChecks[0]){
                    graphChecks[0] = false;
                }else{
                    graphChecks[0] = true;
                }

                equationGraph.getData().clear();
                drawGraphs(getInitialData());

            }

        });

        eulerCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(graphChecks[1]){
                    graphChecks[1] = false;
                }else{
                    graphChecks[1] = true;
                }

                equationGraph.getData().clear();
                drawGraphs(getInitialData());
            }

        });


        impEulerCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(graphChecks[2]){
                    graphChecks[2] = false;
                }else{
                    graphChecks[2] = true;
                }
                equationGraph.getData().clear();
                drawGraphs(getInitialData());
            }

        });


        rungeKuttaCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(graphChecks[3]){
                    graphChecks[3] = false;
                }else{
                    graphChecks[3] = true;
                }
                equationGraph.getData().clear();
                drawGraphs(getInitialData());
            }

        });




    }

    private void setGraphGridSettings(InitialData initialData){

        xAxis.setLowerBound(initialData.getX0());
        System.out.println("/"+initialData.getMaxValue());
        xAxis.setUpperBound(initialData.getX());
        xAxis.setTickUnit(initialData.getX()/50);


        yAxis.setLowerBound(0);
        yAxis.setUpperBound(initialData.getMaxValue());
        yAxis.setTickUnit(initialData.getMaxValue()/50);
    }

    private void  setGraphNumberFormat(){
        NumberFormat format = new DecimalFormat("#####.#E0");
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number number) {
                return format.format(number.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return format.parse(string);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0 ;
                }
            }

        });
    }

    public double y(double x,InitialData initialData){

        return Math.sqrt(Math.pow(((2*x+1)/6+initialData.getC()*Math.exp(2*x)),3));
    }



    public ComplexFunction<Double,Double> y_prime = new ComplexFunction<Double, Double>() {
        @Override
        public Double apply(Double x, Double y) {
            return 3*y - x*Math.pow(y,1.0/3.0);
        }
    };





}
