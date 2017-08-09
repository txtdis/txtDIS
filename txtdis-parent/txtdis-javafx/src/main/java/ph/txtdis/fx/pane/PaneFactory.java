package ph.txtdis.fx.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

import static javafx.geometry.Pos.*;

@Component("paneFactory")
public class PaneFactory {

	public HBox forDialogButtons(List<? extends Node> nodes) {
		return horizontal(nodes, CENTER_RIGHT, 0, 0, 20, 0);
	}

	private HBox horizontal(List<? extends Node> nodes, Pos pos, int top, int right, int bottom, int left) {
		return horizontal(pos, top, right, bottom, left, toArray(nodes));
	}

	private HBox horizontal(Pos pos, int top, int right, int bottom, int left, Node... nodes) {
		HBox b = horizontal(nodes);
		b.setAlignment(pos);
		b.setPadding(new Insets(top, right, bottom, left));
		return b;
	}

	private Node[] toArray(List<? extends Node> nodes) {
		return nodes.toArray(new Node[nodes.size()]);
	}

	public HBox horizontal(Node... nodes) {
		return new HBox(nodes);
	}

	public HBox forDialogMessages(Node... nodes) {
		return horizontal(CENTER, 0, 0, 0, 20, nodes);
	}

	public HBox forGridGroup(Node... nodes) {
		return forIdName(nodes);
	}

	public HBox forIdName(Node... nodes) {
		return horizontal(CENTER_LEFT, nodes);
	}

	private HBox horizontal(Pos pos, Node... nodes) {
		HBox b = horizontal(nodes);
		b.setAlignment(pos);
		b.setSpacing(10);
		return b;
	}

	public HBox centeredHorizontal(List<? extends Node> nodes) {
		return centeredHorizontal(toArray(nodes));
	}

	public HBox centeredHorizontal(Node... nodes) {
		HBox b = horizontal(CENTER, 0, 10, 10, 10, nodes);
		b.setSpacing(10);
		return b;
	}

	public HBox forMessageDialogButtons(List<? extends Node> nodes) {
		return horizontal(nodes, CENTER_RIGHT, 10, 0, 0, 0);
	}

	public HBox forSubheader(Label label) {
		return horizontal(CENTER, 20, 0, 0, 0, label);
	}

	public HBox forTableTotals(List<? extends Node> totalDisplays) {
		return horizontal(totalDisplays, CENTER_RIGHT, 0, 20, 0, 0);
	}

	public VBox topCenteredVertical(List<? extends Node> nodes) {
		return topCenteredVertical(toArray(nodes));
	}

	public VBox topCenteredVertical(Node... nodes) {
		VBox b = vertical(nodes);
		b.setAlignment(TOP_CENTER);
		return b;
	}

	public VBox vertical(Node... nodes) {
		return new VBox(nodes);
	}

	public VBox vertical(List<Node> nodes) {
		return vertical(toArray(nodes));
	}
}
