package trixt0r.map.fat.widget.layer;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.widget.FatWidget;
import trixt0r.map.fat.widget.RemoveDialog;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetRemoveLayer;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetRemoveObject;
import trixt0r.map.fat.widget.layer.nodes.ButtonNode;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

public class LayerWidget extends FatWidget{
	
	public final Tree layerTree;
	public final TextButton addLayerButton;
	public final TextButton deleteLayerButton;
	public final NewLayerDialog newLayerDialog;
	public final NewLayerObjectDialog newObjectDialog;
	public final RemoveDialog removeDialog;
	public final float fadeSpeed = 10f;
	public boolean fadeOut = false;
	private Node selectedLayer;
	
	public LayerWidget(FatMapEditor editor){
		super(editor);
		
		layerTree = new Tree(skin);
		window.setTitle("Layers");
		
		addLayerButton = new TextButton("ADD", skin);
		deleteLayerButton = new TextButton("DELETE", skin);
		newLayerDialog = new NewLayerDialog("New layer", this.editor, layerTree);
		newObjectDialog = new NewLayerObjectDialog(super.skin, (OrthographicCamera) editor.getMap().getCamera());
		removeDialog = new RemoveDialog("Delete layer",skin, layerTree, "Do you really want to remove this layer?");
		
		Dialog.fadeDuration = 0.2f;
		window.setSize(200, Gdx.graphics.getHeight());
		window.setMovable(false);
		window.setClip(true);
		window.setTitleAlignment(Align.left);
		
		table.add(layerTree);
		table.align(Align.top | Align.left);
		
		window.addActor(scrollPane);
		
		addLayerButton.setClip(true);
		window.addActor(addLayerButton);
		
		deleteLayerButton.setClip(true);
		window.addActor(deleteLayerButton);
		
		layout();
	}
	
	@Override
	public void layout(){
		window.setSize(Math.min(Gdx.graphics.getWidth(), 200), Gdx.graphics.getHeight()-225f);
		this.setBounds(Gdx.graphics.getWidth(), 185f, 200f, window.getHeight()-window.getY());
		if(!this.fadeOut) this.setX(this.getX()-window.getWidth());
		scrollPane.setBounds(0, 0, window.getWidth(), window.getHeight()-45);
		
		layerTree.setWidth(scrollPane.getWidth());
		table.setWidth(scrollPane.getWidth());
		table.align(Align.top | Align.left);
		
		addLayerButton.setPosition(0, window.getHeight()-45);
		deleteLayerButton.setPosition(addLayerButton.getWidth()+5, window.getHeight()-45);
		
		if(layerTree.getNodes().size > 0){
			Node node = layerTree.getNodes().get(0);
			layerTree.remove(node);
			layerTree.insert(0,node);
		}
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		if(this.fadeOut){
			this.setWidth(Math.max(this.getWidth()-fadeSpeed,0));
			this.setX(Math.min(this.getX()+fadeSpeed,Gdx.graphics.getWidth()));
		}
		else{
			this.setWidth(Math.min(this.getWidth()+fadeSpeed,200f));
			this.setX(Math.max(Gdx.graphics.getWidth()-this.getWidth(),this.getX()-fadeSpeed));
		}
		
		final Array<Node> selected = layerTree.getSelection();
		this.checkLayerActions(selected);
		this.checkObjectActions(selected);
		
		for(Node node: this.layerTree.getNodes())
			((LayerNode)node).layer.setTransformable(false);
		
		if(this.selectedLayer != null) ((LayerNode)this.selectedLayer).layer.setTransformable(true);
	}
	
	private void checkLayerActions(Array<Node> selected){
		checkLayerNodesFrom(selected);
		if(!selected.contains(selectedLayer, true) && this.selectedLayer != null) selected.insert(selected.size, selectedLayer);
		
		if(addLayerButton.isPressed() && this.newLayerDialog != this.stage.getScrollFocus())
			newLayerDialog.show(stage);
		
		if(selected.size > 0 && this.selectedLayer != null){
			deleteLayerButton.setDisabled(false);
			if(deleteLayerButton.isPressed() && this.removeDialog != this.stage.getScrollFocus()){
				removeDialog.setTitle("Remove layer");
				removeDialog.setQuestion("Do you really want to remove " + /*((layerNodes.size == 1) ? "*/"this layer"/*" : "these layers") */ + "?");
				removeDialog.setRemoveAction(new LayerWidgetRemoveLayer(layerTree, new Array<Node>(new Node[] {this.selectedLayer}), this.editor.getMap()));
				removeDialog.show(stage);
			}
		}
		else deleteLayerButton.setDisabled(true);
	}
	
	private void checkObjectActions(Array<Node> selected){
		Array<Node> nodes = layerTree.getNodes();
		this.selectObjects(selected, nodes);
		
		if(this.selectedLayer == null) return;
		
		this.removeObjectsFromNonSelectedLayer(selected);
		
		for(Node object: this.selectedLayer.getChildren()){
			if(!(object instanceof ButtonNode)) continue;
			ButtonNode buttonNode = (ButtonNode)object;
			
			//Check for selection
			boolean found = false;
			for(Node sel: selected)
				if(this.selectedLayer.getChildren().contains(sel, true)){
					found = true; break;
				}
			if(!found) buttonNode.getDelButton().setDisabled(true);
			else buttonNode.getDelButton().setDisabled(false);
			
			if(buttonNode.getAddButton().isPressed() && this.newObjectDialog != this.stage.getScrollFocus()){
				newObjectDialog.setLayerNode((LayerNode)this.selectedLayer);
				newObjectDialog.show(stage);
			}
			if(buttonNode.getDelButton().isPressed() && found && this.removeDialog != this.stage.getScrollFocus()){
				removeDialog.setTitle("Remove object");
				removeDialog.setQuestion("Do you really want to remove " + ((selected.size == 1) ? "this object" : "these objects") + "?");
				removeDialog.setRemoveAction(new LayerWidgetRemoveObject((LayerNode)this.selectedLayer, selected));
				removeDialog.show(stage);
			}
		}
	}
	
	private void removeObjectsFromNonSelectedLayer(Array<Node> selected){
		for(Node node: selected)
			if(node.getParent() != this.selectedLayer && node != this.selectedLayer)
				selected.removeValue(node, true);
	}
	
	private void selectObjects(Array<Node> selected, Array<Node> nodes){
		for(Node node: nodes){
			for(Node child: node.getChildren()){
				if(!(child instanceof ObjectNode)) continue;
				ObjectNode obj = (ObjectNode)child;
				if(obj.object == null) continue;
				boolean select = selected.contains(obj, true);
				obj.object.select(select);
			}
		}
	}
	
	private void checkLayerNodesFrom(Array<Node> selected){
		if(selected.size == 0) return;
		Node tempLayer = null;
		for(Node node: selected){
			if(node instanceof LayerNode && tempLayer == null)
				tempLayer = node;
			if(node instanceof ObjectNode && tempLayer == null)
				tempLayer = node.getParent();
		}
		if(tempLayer != null) this.selectedLayer = tempLayer;
		((LayerNode)this.selectedLayer).layer.isCurrentLayer = true;
		if(this.selectedLayer == null) return;
		
		for(Node node: selected)
			if(node instanceof LayerNode && node != this.selectedLayer)
				selected.removeValue(node, true);
	}
	
	public Array<FatMapObject> getSelectedObjects(){
		Array<Node> selected = this.layerTree.getSelection();
		Array<FatMapObject> objects = new Array<FatMapObject>();
		for(Node node: this.layerTree.getNodes()){
			for(Node child: node.getChildren()){
				if(!(child instanceof ObjectNode)) continue;
				ObjectNode obj = (ObjectNode)child;
				if(obj.object == null) continue;
				if(selected.contains(obj, true)) objects.add(obj.object);
			}
		}
		return objects;
	}
	
	public boolean hasFocus(){
		boolean dialogsHaveFocus = this.stage.getScrollFocus() == this.newLayerDialog || this.stage.getScrollFocus() == this.newObjectDialog
				|| this.stage.getKeyboardFocus() == this.newLayerDialog || this.stage.getKeyboardFocus() == this.newObjectDialog
				|| this.stage.getKeyboardFocus() == this.removeDialog || this.stage.getScrollFocus() == this.removeDialog;
		return super.hasFocus() || dialogsHaveFocus;
	}
}
