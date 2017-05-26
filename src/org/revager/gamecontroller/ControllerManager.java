package org.revager.gamecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;

public class ControllerManager {

	private Dashboard dashboard;
	private List<Controller> controllers = new ArrayList<>();

	public ControllerManager(Dashboard dashboard) {
		this.dashboard = dashboard;
		ControllerEnvironment defaultEnvironment = ControllerEnvironment.getDefaultEnvironment();
		int controllerCounter = 0;
		controllers = Arrays.asList(defaultEnvironment.getControllers());
		for (Controller controller : controllers) {
			if (controller.getType() == Type.MOUSE || controller.getType() == Type.KEYBOARD) {
				System.out.println("filtered out : " + controller.getType());
				continue;
			}
			controllerCounter++;
			dashboard.setNumberControllers(controllerCounter);
			System.out.println(controller.getType());
			// if (controller.getType() == Type.STICK) {
			// controller.poll();
			setupControllerQueue(controller);
			// }
		}
	}

	public synchronized void rumble() {
		Thread thread = new Thread(() -> {
			for (Controller controllers : controllers) {
				List<Rumbler> rumblers = Arrays.asList(controllers.getRumblers());
				rumblers.stream().forEach(r -> r.rumble(1.0f));
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			for (Controller controllers : controllers) {
				List<Rumbler> rumblers = Arrays.asList(controllers.getRumblers());
				rumblers.stream().forEach(r -> r.rumble(0.0f));
			}
		});
		thread.start();
	}

	private void setupControllerQueue(Controller controller) {
		Thread thread = new Thread(() -> {
			while (true) {
				if (!controller.poll()) {
					return;
				}
				EventQueue eventQueue = controller.getEventQueue();
				Event event = new Event();
				while (eventQueue.getNextEvent(event)) {
					if (isReleaseEvent(event)) {
						continue;
					}
					reactOnEvent(controller, event);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
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
			new VoteEvent(dashboard, controller.hashCode(), Vote.GOOD);
			break;
		case "9":
		case "8":
		case "Base 4":
		case "Base 3":
			new VoteEvent(dashboard, controller.hashCode(), Vote.POSSIBLE_NO_ERROR);
			break;
		case "1":
		case "Trigger":
			new VoteEvent(dashboard, controller.hashCode(), Vote.MINOR_ERROR);
			break;
		case "0":
		case "Thumb":
			new VoteEvent(dashboard, controller.hashCode(), Vote.MAIN_ERROR);
			break;
		case "3":
		case "Thumb 2":
			new VoteEvent(dashboard, controller.hashCode(), Vote.CRITICAL_ERROR);
			break;
		case "4":
		case "5":
		case "Top 2":
		case "Pinkie":
			new BreakEvent(dashboard);
			break;
		case "y":
		case "x":
			new YawnEvent(dashboard);
			break;
		default:
			System.out
					.println(component.getName() + ";" + component.getIdentifier().getName() + ";" + event.getValue());
		}
	}

}
