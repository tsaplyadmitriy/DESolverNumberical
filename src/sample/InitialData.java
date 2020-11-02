package sample;

public class InitialData {

    private double X;
    private double x0;
    private double y0;
    private double C;
    private double step;
    private  double maxValue;
    private long tl;

    public InitialData(double X, double x0, double y0, double step,long tl) {
        this.X = X;
        this.x0 = x0;
        this.y0 = y0;
        this.C = C(x0,y0);
        this.step = step;
        this.maxValue = y(X,this.C);
        this.tl = tl;

    }




    public InitialData(String X, String x0, String y0, String step,String tl) throws NumberFormatException{


            this.X = Double.parseDouble(X);
            this.x0 = Double.parseDouble(x0);
            this.y0 = Double.parseDouble(y0);
            this.step = Double.parseDouble(step);
            this.tl = Long.parseLong(tl);

        this.C = C(this.x0,this.y0);
        this.maxValue = y(this.X,this.C);

    }


    public long getTL(){
        return this.tl;
    }


    public double getX() {
        return X;
    }



    public double getX0() {
        return x0;
    }



    public double getY0() {
        return y0;
    }

    public double getC() {
        return C;
    }



    public double getStep() {
        return step;
    }



    public double getMaxValue(){
        return this.maxValue;
    }

    private double C(double x0,double y0){

        return (Math.pow(y0,(2.0/3.0)) - ((2*x0+1)/6.0))/(Math.exp(2.0*x0));
    }


    private double y(double x,double C){

        return Math.sqrt(Math.pow(((2*x+1)/6+C*Math.exp(2*x)),3));
    }

}
