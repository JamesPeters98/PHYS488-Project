package simulations;

import tracker.Event;
import tracker.EventSimulation;
import utils.GraphValues;

public class NewSimulation extends Simulation {

	public NewSimulation(String name, int simulationLoops) throws Exception {
		super(name, simulationLoops);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EventSimulation EventLoop(Event event, int currentLoop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSimulationLoop() {
		// TODO Auto-generated method stub

	}

	@Override
	public GraphValues configureGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
