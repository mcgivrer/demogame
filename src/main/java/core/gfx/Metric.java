package core.gfx;

public class Metric {
	protected double value;
	protected String name;
	public Metric(String name) {
        this.name = name;
    }
    public double getValue(){
        return value;
    }
}