package trixt0r.map.fat;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class FatInputMultiplexer implements InputProcessor {
	
	private Array<InputProcessor> processors = new Array<InputProcessor>(4);

	public FatInputMultiplexer () {
	}

	public FatInputMultiplexer (InputProcessor... processors) {
		for (int i = 0; i < processors.length; i++)
			this.processors.add(processors[i]);
	}

	public void addProcessor (int index, InputProcessor processor) {
		processors.insert(index, processor);
	}

	public void removeProcessor (int index) {
		processors.removeIndex(index);
	}

	public void addProcessor (InputProcessor processor) {
		processors.add(processor);
	}

	public void removeProcessor (InputProcessor processor) {
		processors.removeValue(processor, true);
	}

	/** @return the number of processors in this multiplexer */
	public int size () {
		return processors.size;
	}

	public void clear () {
		processors.clear();
	}

	public void setProcessors (Array<InputProcessor> processors) {
		this.processors = processors;
	}

	public Array<InputProcessor> getProcessors () {
		return processors;
	}

	public boolean keyDown (int keycode) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= processors.get(i).keyDown(keycode);
		return processed;
	}

	public boolean keyUp (int keycode) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).keyUp(keycode)) ;
		return processed;
	}

	public boolean keyTyped (char character) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).keyTyped(character)) ;
		return processed;
	}

	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).touchDown(screenX, screenY, pointer, button)) ;
		return processed;
	}

	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).touchUp(screenX, screenY, pointer, button)) ;
		return processed;
	}

	public boolean touchDragged (int screenX, int screenY, int pointer) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).touchDragged(screenX, screenY, pointer)) ;
		return processed;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).mouseMoved(screenX, screenY)) ;
		return processed;
	}

	@Override
	public boolean scrolled (int amount) {
		boolean processed = false;
		for (int i = 0, n = processors.size; i < n; i++)
			processed |= (processors.get(i).scrolled(amount)) ;
		return processed;
	}
}