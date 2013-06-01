package trixt0r.map.fat.widget.layer;

import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.widget.RemoveDialog;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetRemoveLayer;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetRemoveObject;
import trixt0r.map.fat.widget.layer.nodes.ButtonNode;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

public class LayerWidget extends WidgetGroup{
	
	public final Tree layerTree;
	public final Table layerTable;
	public final Window window;
	public final ScrollPane scrollPane;
	public final TextButton addLayerButton;
	public final TextButton deleteLayerButton;
	public final NewLayerDialog newLayerDialog;
	public final NewLayerObjectDialog newObjectDialog;
	public final RemoveDialog removeLayerDialog;
	public final Skin skin;
	public final float fadeSpeed = 10f;
	public boolean fadeOut = false;
	
	private Stage stage;
	
	public LayerWidget(Stage stage, Skin skin){
		this.stage = stage;
		this.skin = skin;
		
		this.stage.addActor(this);
		
		layerTree = new Tree(skin);
		layerTable = new Table();
		window = new Window("Layers", skin);
		scrollPane = new ScrollPane(layerTable, skin);
		addLayerButton = new TextButton("ADD", skin);
		deleteLayerButton = new TextButton("DELETE", skin);
		newLayerDialog = new NewLayerDialog("New layer",skin, layerTree);
		newObjectDialog = new NewLayerObjectDialog(skin);
		removeLayerDialog = new RemoveDialog("Delete layer",skin, layerTree, "Do you really want to remove this layer?");
		
		Dialog.fadeDuration = 0.2f;
		window.setSize(200, Gdx.graphics.getHeight());
		window.setMovable(false);
		window.setClip(true);
		window.setTitleAlignment(Align.left);
		this.addActor(window);
		
		layerTable.add(layerTree);
		layerTable.align(Align.top | Align.left);
		
		window.addActor(scrollPane);
		
		addLayerButton.setClip(true);
		window.addActor(addLayerButton);
		
		deleteLayerButton.setClip(true);
		window.addActor(deleteLayerButton);
		
		this.layout();
	}
	
	@Override
	public void layout(){
		super.layout();
		window.setSize(Math.min(Gdx.graphics.getWidth(), 200), Gdx.graphics.getHeight()-225f);
		this.setBounds(Gdx.graphics.getWidth(), 200f, 200f, window.getHeight()-window.getY());
		if(!this.fadeOut) this.setX(this.getX()-window.getWidth());
		scrollPane.setBounds(0, 0, window.getWidth(), window.getHeight()-45);
		
		layerTree.setWidth(scrollPane.getWidth());
		layerTable.setWidth(scrollPane.getWidth());
		layerTable.align(Align.top | Align.left);
		
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
	}
	
	private void checkLayerActions(Array<Node> selected){
		final Array<Node> layerNodes = getLayerNodesFrom(selected);
		if(addLayerButton.isPressed()) newLayerDialog.show(stage);
		if(selected.size > 0 && layerNodes.size > 0){
			deleteLayerButton.setDisabled(false);
			if(deleteLayerButton.isPressed()){
				removeLayerDialog.setTitle("Remove layer");
				removeLayerDialog.setQuestion("Do you really want to remove " + ((layerNodes.size == 1) ? "this layer" : "these layers") + "?");
				removeLayerDialog.setRemoveAction(new LayerWidgetRemoveLayer(layerTree, layerNodes));
				removeLayerDialog.show(stage);
			}
		}
		else deleteLayerButton.setDisabled(true);
	}
	
	private void checkObjectActions(Array<Node> selected){
		Array<Node> nodes = layerTree.getNodes();
		this.selectObjects(selected, nodes);
		for(final Node node: nodes){
			for(Node child: node.getChildren()){
				if(!(child instanceof ButtonNode)) continue;
				ButtonNode buttonNode = (ButtonNode)child;
				
				//Check for selection
				boolean found = false;
				for(Node sel: selected)
					if(node.getChildren().contains(sel, true)){
						found = true; break;
					}
				if(!found) buttonNode.getDelButton().setDisabled(true);
				else buttonNode.getDelButton().setDisabled(false);
				
				if(buttonNode.getAddButton().isPressed()){
					newObjectDialog.setLayerNode((LayerNode)node);
					newObjectDialog.show(stage);
				}
				if(buttonNode.getDelButton().isPressed() && found){
					removeLayerDialog.setTitle("Remove object");
					removeLayerDialog.setQuestion("Do you really want to remove " + ((selected.size == 1) ? "this object" : "these objects") + "?");
					removeLayerDialog.setRemoveAction(new LayerWidgetRemoveObject((LayerNode)node, selected));
					removeLayerDialog.show(stage);
				}
			}
		}
	}
	
	private void selectObjects(Array<Node> selected, Array<Node> nodes){
		for(Node node: nodes){
			for(Node child: node.getChildren()){
				if(!(child instanceof ObjectNode)) continue;
				ObjectNode obj = (ObjectNode)child;
				if(obj.object == null) continue;
				obj.object.selected = selected.contains(obj, true);
			}
		}
	}
	
	private Array<Node> getLayerNodesFrom(Array<Node> selected){
		Array<Node> layers = new Array<Node>();
		for(Node node: selected)
			if(node instanceof LayerNode) layers.add(node);
		return layers;
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
		Vector2 mouse = this.stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		Vector2 v = this.screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		Actor hit = this.stage.hit(mouse.x, mouse.y, false);
		return hit == this.hit(v.x,v.y, false) && hit != null;
	}
}
