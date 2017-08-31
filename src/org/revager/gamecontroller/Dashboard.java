package org.revager.gamecontroller;

import static org.revager.app.model.Data.translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.presentationView.StatusPanel;

/**
 * This gives access to the state of the current classification process, breaks
 * and focus feedback.
 * 
 * @see StatusPanel
 * @see DashboardEvent
 */
public class Dashboard {

	private static Dashboard instance;

	private int breaks = 0;
	private Finding finding = new Finding();
	private ControllerManager controllerManager;
	private boolean timingActive;

	private final List<Finding> findings = new ArrayList<>();
	/**
	 * This timeout object is used to detect when the pause button is clicked.
	 */
	private final Timeout breakResetTimeout;
	private final Observer findingListener = (Observable o, Object arg) -> setFinding((Finding) o);;
	private final Observer protocolListener = (Observable o, Object arg) -> {
		if (arg instanceof Finding) {
			// A finding was added to the protocol. We don't care, that the
			// protocol "changed".
			Finding finding = (Finding) arg;
			finding.addObserver(findingListener);
		}
	};

	public static synchronized Dashboard getInstance() {
		if (instance == null) {
			instance = new Dashboard();
		}
		return instance;
	}

	private Dashboard() {
		controllerManager = new ControllerManager(this);
		breakResetTimeout = new Timeout(3) {

			@Override
			public void timeout() {
				Dashboard.this.resetBreak();
			}
		};
		addClockListener();
		addListener();
	}

	/**
	 * Update the current {@link Finding}.
	 */
	public void setFinding(Finding finding) {
		if (!findings.contains(finding)) {
			findings.add(finding);
		}
		this.finding = finding;
	}

	public boolean controllersConnected() {
		return controllerManager.controllersConnected();
	}

	public Finding getFinding() {
		return finding;
	}

	public synchronized void addClassification(Finding eventFinding, int owner, Classification classification) {
		eventFinding.getFindingStatus().addClassification(owner, classification);
	}

	public synchronized void addBreak() {
		breaks++;
	}

	public void addFocus(Finding eventFinding) {
		eventFinding.getFindingStatus().addFocus();
	}

	public int getFocusNumber() {
		return finding.getFindingStatus().getFocusNumber();
	}

	/**
	 * Returns the classification result or the number of classifications.
	 * 
	 * @see FindingStatus#buildClassificationCountString()
	 */
	public String getClassificationDetails() {
		FindingStatus findingStatus = finding.getFindingStatus();
		Collection<Classification> votings = findingStatus.getClassifications();
		int numberOfClassifications = votings.size();
		if (numberOfClassifications < controllerManager.getControllerCount()) {
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append(numberOfClassifications);
			builder.append("x <em>(");
			builder.append(translate("waiting for all votes..."));
			builder.append(")</em><html>");
			return builder.toString();
		}
		return findingStatus.buildClassificationCountString();
	}

	public int getFindingTime() {
		return finding.getFindingStatus().getFindingTime();
	}

	public String getBreakText() {
		return Integer.toString(breaks);
	}

	public void startTiming() {
		timingActive = true;
	}

	public void stopTiming() {
		timingActive = false;
	}

	private void resetBreak() {
		breaks = 0;
	}

	private void resetTime() {
		resetBreak();
		for (Finding finding : findings) {
			finding.getFindingStatus().resetFindingTime();
		}
	}

	private void addListener() {

		Protocol protocol = UI.getInstance().getProtocolFrame().getMeeting().getProtocol();
		protocol.addObserver(protocolListener);
		for (Finding finding : protocol.getFindings()) {
			finding.addObserver(findingListener);
		}
	}

	private void addClockListener() {
		UI.getInstance().getProtocolClockWorker().addPropertyChangeListener(evt -> {
			Object newValue = evt.getNewValue();
			if (newValue instanceof Integer) {
				Object oldValue = evt.getOldValue();
				if (oldValue instanceof Integer) {
					updateTime((int) oldValue, (int) newValue);
				}
			}
		});
	}

	private void updateTime(int oldSeconds, int newSeconds) {
		if (oldSeconds == 0 || newSeconds == 0) {
			resetTime();
		} else if (oldSeconds < newSeconds) {
			if (timingActive) {
				finding.getFindingStatus().addFindingTime(newSeconds - oldSeconds);
			}
			breakResetTimeout.reset();
		}

	}

}
