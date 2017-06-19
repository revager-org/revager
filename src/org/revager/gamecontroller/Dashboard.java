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

public class Dashboard {

	private static Dashboard instance;

	private final List<Finding> findings = new ArrayList<>();
	private int breaks = 0;
	private Finding finding = new Finding();
	private ControllerManager controllerManager;
	private final Observer findingListener = (Observable o, Object arg) -> setFinding((Finding) o);;
	private final Observer protocolListener = (Observable o, Object arg) -> {
		if (arg instanceof Finding) {
			// A finding was added to the protocol. We don't care, that the
			// protocol "changed".
			Finding finding = (Finding) arg;
			finding.addObserver(findingListener);
		}
	};

	public static Dashboard getInstance() {
		if (instance == null) {
			instance = new Dashboard();
		}
		return instance;
	}

	private Dashboard() {
		controllerManager = new ControllerManager(this);
		UI.getInstance().getProtocolClockWorker().addPropertyChangeListener(evt -> {
			Object newValue = evt.getNewValue();
			if (newValue instanceof Integer) {
				Object oldValue = evt.getOldValue();
				if (oldValue instanceof Integer) {
					int newSeconds = (int) newValue;
					int oldSeconds = (int) oldValue;
					if (oldSeconds == 0 || newSeconds == 0) {
						resetTime();
					} else if (oldSeconds < newSeconds) {
						finding.getFindingStatus().addFindingTime(newSeconds - oldSeconds);
					}
				}
			}
		});
		addListener();
	}

	private void addListener() {
		Protocol protocol = UI.getInstance().getProtocolFrame().getMeeting().getProtocol();
		protocol.addObserver(protocolListener);
		for (Finding finding : protocol.getFindings()) {
			finding.addObserver(findingListener);
		}

	}

	private void setFinding(Finding finding) {
		if (!findings.contains(finding)) {
			findings.add(finding);
		}
		this.finding = finding;
	}

	public Finding getFinding() {
		return finding;
	}

	public Vote getVoteForOwner(Finding eventFinding, int owner) {
		return eventFinding.getFindingStatus().getVoteForOwner(owner);
	}

	public synchronized void addOrRemoveVote(Finding eventFinding, int owner, Vote vote) {
		eventFinding.getFindingStatus().addOrRemoveVote(owner, vote);
	}

	public synchronized void addBreak() {
		breaks++;
	}

	public int getBreaks() {
		return breaks;
	}

	public void addYawn(Finding eventFinding) {
		eventFinding.getFindingStatus().addYawn();
	}

	private void resetBreak() {
		breaks = 0;
	}

	public int getContinue() {
		return finding.getFindingStatus().getYawn();
	}

	public String getVotingsDetails() {
		FindingStatus findingStatus = finding.getFindingStatus();
		Collection<Vote> votings = findingStatus.getVotings();
		int numberOfVotes = votings.size();
		if (numberOfVotes < controllerManager.getControllerCount()) {
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append(numberOfVotes);
			builder.append("x <em>(");
			builder.append(translate("waiting for all votes..."));
			builder.append(")</em><html>");
			return builder.toString();
		}
		return findingStatus.buildVoteCountString();
	}

	public int getFindingTime() {
		return finding.getFindingStatus().getFindingTime();
	}

	public String getBreakText() {
		return Integer.toString(breaks);
	}

	private void resetTime() {
		resetBreak();
		for (Finding finding : findings) {
			finding.getFindingStatus().resetFindingTime();
		}
	}

}
