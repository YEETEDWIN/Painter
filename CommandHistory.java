import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

interface Command {
	void execute();

	void undo();

	String getDescription();
}

public final class CommandHistory {
	private final Deque<Command> undoStack = new ArrayDeque<>();
	private final Deque<Command> redoStack = new ArrayDeque<>();
	private final List<String> actionHistory = new ArrayList<>();

	public void execute(Command command) {
		Objects.requireNonNull(command, "command");
		command.execute();
		undoStack.push(command);
		redoStack.clear();
		actionHistory.add("Executed: " + command.getDescription());
	}

	public boolean undo() {
		if (undoStack.isEmpty()) {
			return false;
		}

		Command command = undoStack.peek();
		command.undo();
		undoStack.pop();
		redoStack.push(command);
		actionHistory.add("Undid: " + command.getDescription());
		return true;
	}

	public boolean redo() {
		if (redoStack.isEmpty()) {
			return false;
		}

		Command command = redoStack.peek();
		command.execute();
		redoStack.pop();
		undoStack.push(command);
		actionHistory.add("Redid: " + command.getDescription());
		return true;
	}

	public List<String> getActionHistory() {
		return Collections.unmodifiableList(new ArrayList<>(actionHistory));
	}
}
