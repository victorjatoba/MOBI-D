package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mobi.core.Mobi;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;
import mobi.exception.ExceptionURI;
import GUI.MobiDGui;
import GUI.Position;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * Listener responsible to create the new instance on the graph
 * 
 * @author Jatoba
 * @see ButtonListenerInstanceB
 */
public class ButtonInstanceListener implements ActionListener {

	private MobiDGui mobiDGui;
	private Mobi mobi;
	private JPanel panel_view;
	private mxGraph graphA;
	private mxGraphComponent graphComponent;
	private Position graphPosition;
	private JTextField nameInstance;
	private ButtonInstancePopupListener buttonListenerInstancePopup;
	private JPanel panelButtonListenerInstancePopup;
	private JTextField txtInstanceButtonListenerInstancePopup;
	private String className;

	private Class classMobi;
	private Instance instanceMobi;
	
	/**
	 * Constructor responsible to set your attributes
	 * 
	 * @param mobiDGui
	 * @param nameInstance
	 */
	public ButtonInstanceListener(MobiDGui mobiDGui,
			ButtonInstancePopupListener buttonListenerInstancePopup,
			String className) {

		this.mobiDGui = mobiDGui;
		this.mobi = mobiDGui.getMobi();
		this.panel_view = mobiDGui.getPanel_view();
		this.graphA = mobiDGui.getGraph();
		this.graphComponent = mobiDGui.getGraphComponent();
		this.buttonListenerInstancePopup = buttonListenerInstancePopup;
		this.nameInstance = buttonListenerInstancePopup.getTxtInstance();
		this.panelButtonListenerInstancePopup = buttonListenerInstancePopup
				.getPanel();
		this.txtInstanceButtonListenerInstancePopup = buttonListenerInstancePopup
				.getTxtInstance();
		this.className = className;
	}

	/**
	 * Event that create the new instance putting in the graph component
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("ButtonIsntanceLinstener:");
		System.out.println("\n-------" + className + "-------");
		classMobi = mobi.getClass(className);
		
		/*if the class don't exist add it*/
		if(classMobi == null) {
			addClassMobi(className);
		}

		if(!classContainInstance(classMobi, nameInstance.getText())) {
			setNewFeatureGraphOnScreen();
			addInstance(nameInstance.getText());
			associateInstanceClass(instanceMobi, classMobi);
			System.out.println("--------------\n");
		} else {
			System.out.println("the instance already exist into the "+classMobi.getUri());
			nameInstance.setText("name already exist!");
		}
		
		mobiDGui.setMobi(mobi);
	}

	/**
	 * The instance and the class are associated
	 * @param instanceMobi
	 * @param classMobi
	 * */
	private void associateInstanceClass(Instance instanceMobi, Class classMobi) {
		try {
			mobi.isOneOf(instanceMobi, classMobi);
		} catch (ExceptionURI e) {
			e.printStackTrace();
		} finally {
			System.out.println( "\n"+classMobi.getUri()+" and " + instanceMobi.getUri() +
					" here successfully associated!\n");
		}
	}

	/**
	 * Add the new Class into to the default mobi
	 * @param classMobi 
	 * */
	private void addClassMobi(String className) {

		this.classMobi = new Class(className);

		try {
			mobi.addConcept(classMobi);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Class: " + className
					+ "\nsuccessfully created!\n");
		}

	}
	
	/**
	 * Check if the instanceName exist into the classMobi
	 * @param classMobi
	 * @param instanceName
	 * @return boolean
	 * */
	private boolean classContainInstance(Class classMobi, String instanceName) {
		
		Set<Instance> classMobiInstances = mobi.getClassInstances(classMobi.getUri());
		
		if(classMobiInstances != null) {
			Iterator<Instance> it = classMobiInstances.iterator();
			
			while (it.hasNext()) {
				if(it.next().getUri().equals(instanceName)) {
					return true;
				}
			}
			
			return false;
		}else {
			return false;
		}
		
	}
	
	/**
	 * Add instance into to the default mobi
	 * @param nameInstance
	 * */
	private void addInstance(String nameInstance) {
		this.instanceMobi = new Instance(nameInstance);
		
		try {
			mobi.addConcept(instanceMobi);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("\nInstance: " + nameInstance
					+ "\nsuccessfully created!");
		}
		
	}

	/**
	 * Check if the Class already exist in the mobi
	 * 
	 * @param className
	 * @return boolean
	 * */
	private boolean mobiContainClass(Mobi mobi, String className) {

		HashMap<String, Class> mobiClasses = mobi.getAllClasses();
		Iterator<Class> it = mobiClasses.values().iterator();

		while (it.hasNext()) {
//			System.out.println(it.next().getUri() + "\n");
			if (it.next().getUri().equals(className)) {
				System.out.println("Class already exist!");
				return true;
			}
		}

		return false;
	}
	
	/**
	 * This method set all the features about the new graph
	 * */
	private void setNewFeatureGraphOnScreen() {
		this.panel_view = mobiDGui.getPanel_view();
		this.graphA = mobiDGui.getGraph();
		this.graphComponent = mobiDGui.getGraphComponent();

		setInstancePosition();

		graphA.getModel().beginUpdate();

		Object parent = graphA.getDefaultParent();
		graphA.insertVertex(parent, null, nameInstance.getText(),
				graphPosition.x, graphPosition.y, 80, 30, "ROUNDED");
		graphA.getModel().endUpdate();
		graphComponent = new mxGraphComponent(graphA);

		panel_view.add(graphComponent, "cell 0 0,alignx left,aligny top");

		mobiDGui.setPanel_view(panel_view);
		mobiDGui.setGraph(graphA);
		mobiDGui.setGraphComponent(graphComponent);
		buttonListenerInstancePopup.dispose();

	}

	/**
	 * Method who set the position of the instance because the position of the
	 * iA is different of the iB
	 * */
	private void setInstancePosition() {

		if (className.equals(mobiDGui.getTxtClassA().getText())) {
			graphPosition = mobiDGui.getInstanceAPosition();
			this.graphPosition.x = 0;
			this.graphPosition.y = graphPosition.y + 30;
			mobiDGui.setInstanceAPosition(graphPosition);
		} else {
			graphPosition = mobiDGui.getInstanceBPosition();
			this.graphPosition.x = 400;
			this.graphPosition.y = graphPosition.y + 30;
			mobiDGui.setInstanceBPosition(graphPosition);
		}
	}

	/* Getters and Setters */
	public JPanel getPanel_view() {
		return panel_view;
	}

	public Class getClassMobi() {
		return classMobi;
	}

	public void setClassMobi(Class classMobi) {
		this.classMobi = classMobi;
	}

	public Instance getInstanceMobi() {
		return instanceMobi;
	}

	public void setInstanceMobi(Instance instanceMobi) {
		this.instanceMobi = instanceMobi;
	}

	public void setPanel_view(JPanel panel_view) {
		this.panel_view = panel_view;
	}

	public mxGraph getGraphA() {
		return graphA;
	}

	public void setGraphA(mxGraph graphA) {
		this.graphA = graphA;
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public void setGraphComponent(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}

	public Position getGraphPosition() {
		return graphPosition;
	}

	public void setGraphPosition(Position graphPosition) {
		this.graphPosition = graphPosition;
	}

	public JTextField getNameInstance() {
		return nameInstance;
	}

	public void setNameInstance(JTextField nameInstance) {
		this.nameInstance = nameInstance;
	}

	public JPanel getPanelButtonListenerInstancePopup() {
		return panelButtonListenerInstancePopup;
	}

	public void setPanelButtonListenerInstancePopup(
			JPanel panelButtonListenerInstancePopup) {
		this.panelButtonListenerInstancePopup = panelButtonListenerInstancePopup;
	}

	public JTextField getTxtInstanceButtonListenerInstancePopup() {
		return txtInstanceButtonListenerInstancePopup;
	}

	public void setTxtInstanceButtonListenerInstancePopup(
			JTextField txtInstanceButtonListenerInstancePopup) {
		this.txtInstanceButtonListenerInstancePopup = txtInstanceButtonListenerInstancePopup;
	}

	public String getTypeOfInstance() {
		return className;
	}

	public void setTypeOfInstance(String typeOfInstance) {
		this.className = typeOfInstance;
	}

}
