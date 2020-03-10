package core.gfx;

public class Counter {
	private double stepValue;
	private double startCounter;
	private double value, counterValue;
	private double count = 0;
	private String name;

	public Counter(String name, double startCounter, double step) {
		this.name = name;
		this.startCounter = startCounter;
		this.stepValue = step;
		this.count = startCounter;
	}

	public void reset() {
		this.count = startCounter;
		this.value = 0;
	}

	public void tick(double elapsed) {
		this.value += elapsed;

		if (value > stepValue) {
			count++;
		}
		if(value>1000){
			this.counterValue = count;
			reset();
		}

	}

	public double getCounter() {
		return counterValue;
	}

	public String getName() {
		return name;
	}

	public boolean isReached() {
		return value>stepValue;
	}

	@Override
	public String toString() {
		return String.format("value:%04f, count:%04f, counter:%04f, step:%04f",value,count, counterValue,stepValue);
	}
}