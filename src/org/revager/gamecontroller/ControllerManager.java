package org.revager.gamecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class ControllerManager {

	private Dashboard dashboard;
	private List<Controller> controllers = Collections.synchronizedList(new ArrayList<>());
	/**
	 * Was any controller ever connected?
	 */
	private final boolean controllersConnected;

	public ControllerManager(Dashboard dashboard) {
		this.dashboard = dashboard;
		ControllerEnvironment defaultEnvironment = ControllerEnvironment.getDefaultEnvironment();
		for (Controller controller : Arrays.asList(defaultEnvironment.getControllers())) {
			if (isController(controller)) {
				controllers.add(controller);
			} else {
				System.out.println("filtered out : " + controller.getType());
			}
		}
		Thread thread = new Thread(() -> setupControllersQueue());
		thread.start();
		controllersConnected = !controllers.isEmpty();
	}

	public int getControllerCount() {
		return controllers.size();
	}

	public boolean controllersConnected() {
		return controllersConnected;
	}

	private boolean isController(Controller controller) {
		String name = StringUtils.defaultString(controller.getName());
		return controller.getType() == Type.STICK && !name.contains("eyboard");
	}

	private void setupControllersQueue() {
		while (true) {
			for (Controller controller : controllers) {
				if (!controller.poll()) {
					controllers.remove(controller);
					continue;
				}
				EventQueue eventQueue = controller.getEventQueue();
				Event event = new Event();
				if (eventQueue.getNextEvent(event) && !isReleaseEvent(event)) {
					reactOnEvent(controller, event);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}

	private boolean isReleaseEvent(Event event) {
		final float value = event.getValue();
		return -0.5f < value && value < 0.5f;
	}

	private void reactOnEvent(Controller controller, Event event) {
		Component component = event.getComponent();
		switch (component.getIdentifier().getName()) {
		case "2":
		case "Top":
			new ClassificationEvent(dashboard, controller.hashCode(), Classification.CRITICAL_ERROR);
			break;
		case "9":
		case "Base 4":
		case "Base 3":
			new ClassificationEvent(dashboard, controller.hashCode(), Classification.RATHER_NO_ERROR);
			break;
		case "1":
		case "Trigger":
			new ClassificationEvent(dashboard, controller.hashCode(), Classification.MAIN_ERROR);
			break;
		case "0":
		case "Thumb":
			new ClassificationEvent(dashboard, controller.hashCode(), Classification.MINOR_ERROR);
			break;
		case "3":
		case "Thumb 2":
			new ClassificationEvent(dashboard, controller.hashCode(), Classification.GOOD);
			break;
		case "4":
		case "6":
		case "5":
		case "7":
		case "Top 2":
		case "Pinkie":
		case "Base":
		case "Base 2":
			new BreakEvent(dashboard);
			break;
		case "y":
		case "8":
		case "x":
			new FocusEvent(dashboard);
			break;
		default:
			System.out
					.println(component.getName() + ";" + component.getIdentifier().getName() + ";" + event.getValue());
		}
	}

}
