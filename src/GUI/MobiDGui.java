package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import mobi.core.Mobi;
import mobi.core.relation.GenericRelation;
import net.miginfocom.swing.MigLayout;
import Listeners.ButtonInstancePopupListener;
import Listeners.ButtonGenerateOWLListener;
import Listeners.ChangeInstanceListener;
import Listeners.EdgeConnectListener;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * This class consist in show the visual part of system.
 * 
 * @author Jatoba
 * */

@SuppressWarnings("serial")
public class MobiDGui extends JFrame {

	JPanel contentPane;
	JTextField txtClassA;
	JTextField txtClassB;
	JTextField txtRelationA;
	JTextField txtRelationB;
	
	String classAName;
	String classBName;
	String relationAName;
	String relationBName;

	JButton btnInstanceA;
	JButton btnInstanceB;
	JButton btnGenerateOWL;

	JRadioButton rdbtnInheritance;
	JRadioButton rdbtnComposition;
	JRadioButton rdbtnEquivalence;
	
	JPanel panel_view; // The panel that contains the graph set
	JPanel panel_create; /* The south panel that contains the controls and
							manipulations of the instances */

	Position instanceAPosition; // Position of the new instanceA on the screen
	Position instanceBPosition; // Position of the new instanceB on the screen

	mxGraph graph;
	mxGraphComponent graphComponent;
	
	ArrayList<String> instancesA = new ArrayList<String>();
	ArrayList<String> instancesB = new ArrayList<String>();
	
	/* Kernel attributes*/
	Mobi mobi;
	GenericRelation genericRelation;
	
	/**
	 * Construction of the main frame.
	 */
	public MobiDGui() {

		this.mobi = new Mobi("TesteMOBIDefault");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 460);

		initGraph();

		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setForeground(Color.BLUE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		panel_view = new JPanel();
		panel_view.setBackground(Color.WHITE);
		panel_view.setForeground(Color.WHITE);
		panel_view.add(graphComponent);

		// panel_view.setPreferredSize(new Dimension(564, 230));
		// createGraph();
		contentPane.setLayout(new MigLayout("", "[564px,grow]",
				"[230px,grow][159px]"));

		panel_create = new JPanel();
		panel_create.setForeground(Color.LIGHT_GRAY);
		// panel_create.setPreferredSize(new Dimension(564, 159));
		contentPane.add(panel_create, "cell 0 1,alignx left,aligny top");

		btnInstanceA = new JButton("+Instance A");
		btnInstanceA.addActionListener(new ButtonInstancePopupListener(this));
		panel_create
				.setLayout(new MigLayout(
						"",
						"[50px:109px:600px,grow][2px][38px][60px][11px][2px][109px][33px][46px][9px][105px]",
						"[23px,grow][20px][4px][14px][21px][23px]"));
		panel_create.add(btnInstanceA, "cell 0 0,growx,aligny top");

		btnInstanceB = new JButton("+Instance B");
		btnInstanceB.addActionListener(new ButtonInstancePopupListener(this));
		panel_create.add(btnInstanceB, "cell 10 0,growx,aligny top");

		txtClassA = new JTextField();
		txtClassA.setText("Class A");
		panel_create.add(txtClassA, "cell 0 1,growx,aligny top");
		txtClassA.setColumns(10);
		
		btnGenerateOWL = new JButton("Generate OWL");
		btnGenerateOWL.addActionListener(new ButtonGenerateOWLListener(this));
		panel_create.add(btnGenerateOWL, "cell 6 1");

		txtClassB = new JTextField();
		txtClassB.setText("Class B");
		txtClassB.setColumns(10);
		panel_create.add(txtClassB, "cell 10 1,growx,aligny top");

		txtRelationB = new JTextField();
		txtRelationB.setText("Est\u00E1 contido");
		txtRelationB.setColumns(10);
		panel_create
				.add(txtRelationB, "cell 10 4,alignx center,aligny bottom");

		rdbtnInheritance = new JRadioButton("Inheritance");
		panel_create.add(rdbtnInheritance, "cell 0 5,growx,aligny top");

		rdbtnComposition = new JRadioButton("Composition");
		panel_create.add(rdbtnComposition, "cell 2 5 3 1,growx,aligny top");

		rdbtnEquivalence = new JRadioButton("Equivalence");
		panel_create.add(rdbtnEquivalence, "cell 6 5,growx,aligny top");

		JSeparator separator = new JSeparator();
		panel_create.add(separator, "cell 0 2 11 1,grow");

		JLabel lblRelationshipTypes = new JLabel("Relationship Types:");
		panel_create.add(lblRelationshipTypes, "cell 4 3 3 1,growx,aligny top");

		JLabel lblIda = new JLabel("Ida:");
		panel_create.add(lblIda, "cell 0 4,alignx left,aligny bottom");

		txtRelationA = new JTextField();
		txtRelationA.setText("Cont\u00E9m");
		panel_create.add(txtRelationA, "cell 0 4,growx,aligny top");
		txtRelationA.setColumns(10);

		JLabel lblVolta = new JLabel("Volta:");
		panel_create.add(lblVolta, "cell 8 4,growx,aligny bottom");

		contentPane.add(panel_view, "cell 0 0,alignx left,aligny top");

	}

	/**
	 * Initialization of the graph component
	 */
	public void initGraph() {
		this.graph = new mxGraph();

//		this.graph.getModel().addListener(mxEvent.CHANGE, new ChangeInstanceListener(this));
		this.graphComponent = new mxGraphComponent(graph);
		this.graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, new EdgeConnectListener(this));

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
	    {
	     
	        public void mouseReleased(MouseEvent e)
	        {
	            Object cell = graphComponent.getCellAt(e.getX(), e.getY());
	             
	            if (cell != null)
	            {
//	                System.out.println("cell="+graph.getLabel(cell));
	            }
	        }
	    });
		
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);

		graphComponent.setPreferredSize(new Dimension(550, 220));

		graph.getModel().beginUpdate();
			setEdgeFeatures();
			setGraphFeatures();
		graph.getModel().endUpdate();

		/*
		 * initing the new graphs with zero, because don't created one graph
		 * still
		 */
		this.instanceAPosition = new Position();
		instanceAPosition.x = 0;
		instanceAPosition.y = 0;
		this.instanceBPosition = new Position();
		instanceBPosition.x = 0;
		instanceBPosition.y = 0;
	}

	/**
	 * Set the default graph style
	 */
	private void setGraphFeatures() {
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> edgeStyleMap = new Hashtable<String, Object>();
		edgeStyleMap.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CLOUD);
		edgeStyleMap.put(mxConstants.STYLE_OPACITY, 50);
		edgeStyleMap.put(mxConstants.STYLE_FONTCOLOR, "#774400");

		edgeStyleMap.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // as margin
		// default is #6482B9
		// style.put(mxConstants.STYLE_FONTCOLOR, "#446299");
		stylesheet.putCellStyle("ROUNDED", edgeStyleMap);
	}

	/**
	 * Set the default edge style
	 */
	private void setEdgeFeatures() {
		// Settings for edges
		Map<String, Object> edgeStyleMap = new HashMap<String, Object>();
		edgeStyleMap.put(mxConstants.STYLE_ROUNDED, true);
		edgeStyleMap.put(mxConstants.STYLE_ORTHOGONAL, false);
		edgeStyleMap.put(mxConstants.STYLE_EDGE, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edgeStyleMap.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		edgeStyleMap.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
		edgeStyleMap.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		// default is #6482B9
		edgeStyleMap.put(mxConstants.STYLE_FONTCOLOR, "#446299");
		mxStylesheet edgeStyle = new mxStylesheet();

		edgeStyle.setDefaultEdgeStyle(edgeStyleMap);
		graph.setStylesheet(edgeStyle);
	}

	/* Getters and Setters methods */

	public JPanel getPanel_view() {
		return panel_view;
	}

	public Mobi getMobi() {
		return mobi;
	}

	public void setMobi(Mobi mobi) {
		this.mobi = mobi;
	}

	public void setPanel_view(JPanel panel_view) {
		this.panel_view = panel_view;
	}

	public JPanel getPanel_create() {
		return panel_create;
	}

	public void setPanel_create(JPanel panel_create) {
		this.panel_create = panel_create;
	}

	public mxGraph getGraph() {
		return graph;
	}

	public void setGraph(mxGraph graph) {
		this.graph = graph;
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public void setGraphComponent(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}

	public JTextField getTxtClassA() {
		return txtClassA;
	}

	public void setTxtClassA(JTextField txtClassA) {
		this.txtClassA = txtClassA;
	}

	public JTextField getTxtClassB() {
		return txtClassB;
	}

	public void setTxtClassB(JTextField txtClassB) {
		this.txtClassB = txtClassB;
	}

	public JTextField getTxtRelationA() {
		return txtRelationA;
	}

	public void setTxtRelationA(JTextField txtRelationA) {
		this.txtRelationA = txtRelationA;
	}

	public JTextField getTxtRelationB() {
		return txtRelationB;
	}

	public void setTxtRelationB(JTextField txtRelationB) {
		this.txtRelationB = txtRelationB;
	}

	public Position getInstanceAPosition() {
		return instanceAPosition;
	}

	public void setInstanceAPosition(Position instanceAPosition) {
		this.instanceAPosition = instanceAPosition;
	}

	public Position getInstanceBPosition() {
		return instanceBPosition;
	}

	public void setInstanceBPosition(Position instanceBPosition) {
		this.instanceBPosition = instanceBPosition;
	}

	public JButton getBtnInstanceA() {
		return btnInstanceA;
	}

	public void setBtnInstanceA(JButton btnInstanceA) {
		this.btnInstanceA = btnInstanceA;
	}

	public JButton getBtnInstanceB() {
		return btnInstanceB;
	}

	public void setBtnInstanceB(JButton btnInstanceB) {
		this.btnInstanceB = btnInstanceB;
	}

	public ArrayList<String> getInstancesA() {
		return instancesA;
	}

	public void setInstancesA(ArrayList<String> instancesA) {
		this.instancesA = instancesA;
	}

	public ArrayList<String> getInstancesB() {
		return instancesB;
	}

	public void setInstancesB(ArrayList<String> instancesB) {
		this.instancesB = instancesB;
	}

	public String getClassAName() {
		return classAName;
	}

	public void setClassAName(String classAName) {
		this.classAName = classAName;
	}

	public String getClassBName() {
		return classBName;
	}

	public void setClassBName(String classBName) {
		this.classBName = classBName;
	}

	public String getRelationAName() {
		return relationAName;
	}

	public void setRelationAName(String relationAName) {
		this.relationAName = relationAName;
	}

	public String getRelationBName() {
		return relationBName;
	}

	public void setRelationBName(String relationBName) {
		this.relationBName = relationBName;
	}

	public JButton getBtnGenerateOWL() {
		return btnGenerateOWL;
	}

	public void setBtnGenerateOWL(JButton btnGenerateOWL) {
		this.btnGenerateOWL = btnGenerateOWL;
	}

	public GenericRelation getGenericRelation() {
		return genericRelation;
	}

	public void setGenericRelation(GenericRelation genericRelation) {
		this.genericRelation = genericRelation;
	}

	public JRadioButton getRdbtnInheritance() {
		return rdbtnInheritance;
	}

	public void setRdbtnInheritance(JRadioButton rdbtnInheritance) {
		this.rdbtnInheritance = rdbtnInheritance;
	}

	public JRadioButton getRdbtnComposition() {
		return rdbtnComposition;
	}

	public void setRdbtnComposition(JRadioButton rdbtnComposition) {
		this.rdbtnComposition = rdbtnComposition;
	}

	public JRadioButton getRdbtnEquivalence() {
		return rdbtnEquivalence;
	}

	public void setRdbtnEquivalence(JRadioButton rdbtnEquivalence) {
		this.rdbtnEquivalence = rdbtnEquivalence;
	}

}
