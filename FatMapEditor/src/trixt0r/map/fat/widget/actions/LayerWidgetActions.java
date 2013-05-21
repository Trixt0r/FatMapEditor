package trixt0r.map.fat.widget.actions;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.widget.RemoveDialog;
import trixt0r.map.fat.widget.layer.ButtonNode;
import trixt0r.map.fat.widget.layer.NewLayerDialog;
import trixt0r.map.fat.widget.layer.NewLayerObjectDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

public class LayerWidgetActions {
	
	public final static Tree layerTree = new Tree(FatMapEditor.skin);
	public final static Table layerTable = new Table();
	public final static Window window = new Window("Layers", FatMapEditor.skin);
	public final static ScrollPane scrollPane = new ScrollPane(layerTable, FatMapEditor.skin);
	public final static TextButton addLayerButton = new TextButton("ADD", FatMapEditor.skin);
	public final static TextButton deleteLayerButton = new TextButton("DELETE", FatMapEditor.skin);
	public final static NewLayerDialog newLayerDialog = new NewLayerDialog("New layer",FatMapEditor.skin, LayerWidgetActions.layerTree);
	public final static NewLayerObjectDialog newObjectDialog = new NewLayerObjectDialog(FatMapEditor.skin);
	public final static RemoveDialog removeLayerDialog = new RemoveDialog("Delete layer",FatMapEditor.skin, LayerWidgetActions.layerTree, "Do you really want to remove this layer?");
	
	public final static Array<Node> layerNodes = new Array<Node>();	
	
	private static Stage stage;
	
	
	public static void createLayerWidget(Stage stage, Skin skin){
		Dialog.fadeDuration = 0.2f;
		LayerWidgetActions.stage = stage;
		window.setSize(200, Gdx.graphics.getHeight());
		window.setX(Gdx.graphics.getWidth()-window.getWidth());
		window.setMovable(false);
		window.setClip(true);
		window.setTitleAlignment(Align.left);
		stage.addActor(window);
		
		layerTable.add(layerTree);
		layerTable.align(Align.top | Align.left);
		
		window.addActor(scrollPane);
		
		addLayerButton.setClip(true);
		window.addActor(addLayerButton);
		
		deleteLayerButton.setClip(true);
		window.addActor(deleteLayerButton);
		reArangeLayout();
	}

	public static void reArangeLayout(){
		window.setX(Gdx.graphics.getWidth()-window.getWidth());
		window.setSize(Math.min(Gdx.graphics.getWidth(), 200), Gdx.graphics.getHeight());
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
	
	public static void addLayerNode(String name){
		Label label = new Label(name,FatMapEditor.skin);
		Node node = new Node(label);
		layerTree.add(node);
		node.add(new ButtonNode(new ButtonNode.LayerButtonGroup(), FatMapEditor.skin));
		layerNodes.add(node);
	}
	
	public static void removeNode(Node node){
		if(node != null)
			layerTree.remove(node);
	}
	
	public static void removeNodes(Array<Node> selected){
		for(Node node: selected)
			removeNode(node);
	}
	
	public static void addLayerObjectNode(String name, Node root){
		Label label = new Label(name,FatMapEditor.skin);
		root.add(new Node(label));
	}
	
	public static void removeLayerNodes(Array<Node> selected){
		for(Node node: selected){
			if(layerNodes.contains(node, true)) removeNode(node);
		}
	}
	
	public static void removeNodesAtRoot(Node root, Array<Node> selected){
		for(Node node: selected){
			if(node.getParent() == root) removeNode(node);
		}
	}
	
	public static void act(){
		final Array<Node> selected = layerTree.getSelection();
		if(LayerWidgetActions.addLayerButton.isPressed()) newLayerDialog.show(stage);
		if(selected.size > 0 && conatainsLayerNodes(selected)){
			deleteLayerButton.setDisabled(false);
			if(LayerWidgetActions.deleteLayerButton.isPressed()){
				removeLayerDialog.setTitle("Remove layer");
				removeLayerDialog.setQuestion("Do you really want to remove " + ((selected.size == 1) ? "this layer" : "these layers") + "?");
				removeLayerDialog.setRemoveAction(new RemoveAction(){
					@Override
					public void remove() {
						LayerWidgetActions.removeLayerNodes(selected);
					}
				});
				removeLayerDialog.show(stage);
			}
		}
		else deleteLayerButton.setDisabled(true);
		

		Array<Node> nodes = layerTree.getNodes();
		for(final Node node: nodes){
			for(Node child: node.getChildren()){
				if(!(child instanceof ButtonNode)) continue;
				ButtonNode layerNode = (ButtonNode)child;
				
				//Check for selection
				boolean found = false;
				for(Node sel: selected)
					if(node.getChildren().contains(sel, true)){
						found = true;
						break;
					}
				if(!found) layerNode.getDelButton().setDisabled(true);
				else layerNode.getDelButton().setDisabled(false);
				
				if(layerNode.getAddButton().isPressed()){
					newObjectDialog.setLayerNode(node);
					newObjectDialog.show(stage);
				}
				if(layerNode.getDelButton().isPressed() && found){
					removeLayerDialog.setTitle("Remove object");
					removeLayerDialog.setQuestion("Do you really want to remove " + ((selected.size == 1) ? "this object" : "these objects") + "?");
					removeLayerDialog.setRemoveAction(new RemoveAction(){
						@Override
						public void remove() {
							LayerWidgetActions.removeNodesAtRoot(node, selected);
						}
					});
					removeLayerDialog.show(stage);
				}
			}
		}
		
	}
	
	private static boolean conatainsLayerNodes(Array<Node> selected){
		for(Node node: selected){
			if(layerNodes.contains(node, true)) return true;
		}
		return false;
	}
	
	public static interface RemoveAction{
		public void remove();
	}

}
