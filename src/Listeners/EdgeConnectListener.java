package Listeners;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

import mobi.core.Mobi;
import mobi.core.cardinality.Cardinality;
import mobi.core.common.Relation;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;
import mobi.core.relation.CompositionRelation;
import mobi.core.relation.GenericRelation;
import mobi.core.relation.InheritanceRelation;
import mobi.core.relation.SymmetricRelation;
import mobi.extension.export.owl.Mobi2OWL;
import GUI.MobiDGui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class EdgeConnectListener implements mxIEventListener {

	MobiDGui mobiDGui;
	mxGraph graph;
	mxGraphComponent graphComponent;

	Mobi mobi;
	Class classA;
	Class classB;
	GenericRelation genericRelation;
	JTextField txtRelationA;
	JTextField txtRelationB;

	JTextField txtClassA;
	JTextField txtClassB;

	String classAName;
	String classBName;

	JRadioButton radioButtonTypeRelation;

	public EdgeConnectListener(MobiDGui mobiDGui) {
		this.mobiDGui = mobiDGui;
		this.graph = mobiDGui.getGraph();
		this.graphComponent = mobiDGui.getGraphComponent();
		this.genericRelation = mobiDGui.getGenericRelation();

		this.mobi = mobiDGui.getMobi();
		this.txtRelationA = mobiDGui.getTxtRelationA();
		this.txtRelationB = mobiDGui.getTxtRelationB();
		this.txtClassA = mobiDGui.getTxtClassA();
		this.txtClassB = mobiDGui.getTxtClassB();
	}

	@Override
	public void invoke(Object sender, mxEventObject event) {

		genericRelation = getGenericRelationIntoMobi();

		if (genericRelation == null) {
			genericRelation = (GenericRelation) mobi
					.createGenericRelation("genericRelation");
			System.out.println("EdgeConnectListener| "
					+ genericRelation.getName() + " is successfully created");
		} else {
			System.out.println("EdgeConnectListener| genericRelation already exist!");
		}

		if (genericRelation.getClassA() == null) {
			classA = mobi.getClass(mobiDGui.getTxtClassA().getText());

			if (classA != null) {

				genericRelation.setClassA(classA);
				System.out.println("EdgeConnectListener: " + classA.getUri()
						+ " was setted by " + genericRelation.getName());
			} else {
				System.out.println("EdgeConnectListener: Erro! ClassA is null");

			}

		}

		if (genericRelation.getClassB() == null) {
			classB = mobi.getClass(mobiDGui.getTxtClassB().getText());

			if (classB != null) {

				genericRelation.setClassB(classB);
				System.out.println("EdgeConnectListener: " + classB.getUri()
						+ " was setted by " + genericRelation.getName());
			} else {
				System.out.println("EdgeConnectListener: Erro! ClassB is null");

			}
		}

		mxCell edge = (mxCell) event.getProperty("cell");
		mxICell target = edge.getTarget();
		mxICell source = edge.getSource();
		
		System.out.println("TargetName : "+graph.getLabel(target));
		System.out.println("SourceName : "+graph.getLabel(source));
		
		addInstancesAssociatingWithClassIntoRelation(genericRelation, graph.getLabel(target), graph.getLabel(source));

		// genericRelation.addInstanceRelation(instanceA, instanceB);

		try {
			
			genericRelation.processCardinality();
			Cardinality cardinalityA = genericRelation.getCardinalityA();
			Cardinality cardinalityB = genericRelation.getCardinalityB();
			
			System.out.println("cardinalityA: " + cardinalityA + " cardinalityB:" + cardinalityB);
			
			mobi.addConcept(genericRelation);
			
			Collection<Integer> possibilities = mobi.infereRelation(genericRelation);
			
			
			/* Passo 19
			 * Salienta-se que se for um relação de herança , composição bidirecional e simétrica haverão conversões das seguintes formas respectivamente:
			 */
			if(!possibilities.isEmpty()) {
//				if(possibilities.contains(genericRelation.EQUIVALENCE)) {
//					System.out.println("Equivalence");
				if(possibilities.contains(genericRelation.INHERITANCE)) {
					InheritanceRelation inheritanceRelation = (InheritanceRelation) mobi.convertToInheritanceRelation(genericRelation,"inheritance");
					mobi.addConcept(inheritanceRelation);
					radioButtonTypeRelation = mobiDGui.getRdbtnInheritance();
					System.out.println("Inheritance");
				} else if(possibilities.contains(genericRelation.SYMMETRIC_COMPOSITION)) {
					SymmetricRelation symmetric = (SymmetricRelation) mobi.convertToSymmetricRelation(genericRelation, "simetrico");
					mobi.addConcept(symmetric);
					radioButtonTypeRelation = mobiDGui.getRdbtnEquivalence();
					System.out.println("symetric");
				} else if(possibilities.contains(genericRelation.BIDIRECIONAL_COMPOSITION)) {
					CompositionRelation composition = (CompositionRelation)mobi.convertToBidirecionalCompositionRelationship(genericRelation, "tem", "pertence");				
					mobi.addConcept(composition);
					radioButtonTypeRelation = mobiDGui.getRdbtnComposition();
					System.out.println("composition");
				}
			} else {
				radioButtonTypeRelation = null;
				System.out.println("EdgeConnectionListener| Problem: No Relation type infere");				
			}
				
			if(radioButtonTypeRelation != null) {
				radioButtonTypeRelation.setSelected(true);
			}
			deselectOthersTypeRelationField(radioButtonTypeRelation);					
			
			System.out.println("---Possibilities:");
			for(Integer i: possibilities) {
				System.out.println(i.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deselectOthersTypeRelationField(JRadioButton rdbtn) {
		if(rdbtn != null) {
			if (!rdbtn.equals(mobiDGui.getRdbtnComposition())) {
				mobiDGui.getRdbtnComposition().setSelected(false);
			}
			if (!rdbtn.equals(mobiDGui.getRdbtnEquivalence())) {
				mobiDGui.getRdbtnEquivalence().setSelected(false);
			}
			if (!rdbtn.equals(mobiDGui.getRdbtnInheritance())) {
				mobiDGui.getRdbtnInheritance().setSelected(false);
			}
			
		} else {
			mobiDGui.getRdbtnComposition().setSelected(false);
			mobiDGui.getRdbtnEquivalence().setSelected(false);
			mobiDGui.getRdbtnInheritance().setSelected(false);			
		}

	}

	private void addInstancesAssociatingWithClassIntoRelation(
			GenericRelation genericRelation, String target, String source) {
		Instance sourceInstance = new Instance(source);
		Instance targetInstance = new Instance(target);

		try {
			genericRelation.addInstanceRelation(sourceInstance, targetInstance);
			System.out.println("genericRelation add instances: " + source
					+ " and " + target);
		} catch (Exception e) {
			System.out
					.println("Problem with: EdgeConnectListener:genericRelation.addInstanceRelation(sourceInstance, targetInstance);");
			e.printStackTrace();
		}
	}

	private void printAllClasses() {

		HashMap<String, Class> mobiClasses = mobi.getAllClasses();
		Iterator<Class> it = mobiClasses.values().iterator();

		while (it.hasNext()) {
			System.out.println(it.next().getUri());
		}

	}

	private GenericRelation getGenericRelationIntoMobi() {

		HashMap<String, GenericRelation> mobiGenericRelations = mobi
				.getAllGenericRelations();
		GenericRelation genericRelationFound = null;

		// System.out.println(mobiGenericRelations);
		for (String key : mobiGenericRelations.keySet()) {
			GenericRelation value = mobiGenericRelations.get(key);
			if (value.getUri() == "genericRelation") {
				genericRelationFound = value;
				break;
			}
		}

		return genericRelationFound;
	}

	private boolean mobiContainClass(Mobi mobi, String className) {

		HashMap<String, Class> mobiClasses = mobi.getAllClasses();
		Iterator<Class> it = mobiClasses.values().iterator();

		while (it.hasNext()) {
			// System.out.println(it.next().getUri() + "\n");
			if (it.next().getUri().equals(className)) {
				System.out.println("Class already exist!");
				return true;
			}
		}

		return false;
	}

	private void setEdgeFeatures() {
		// Settings for edges
		Map<String, Object> edgeStyleMap = new HashMap<String, Object>();
		edgeStyleMap.put(mxConstants.STYLE_ROUNDED, true);
		edgeStyleMap.put(mxConstants.STYLE_ORTHOGONAL, false);
		edgeStyleMap.put(mxConstants.STYLE_EDGE, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		edgeStyleMap.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edgeStyleMap.put(mxConstants.STYLE_VERTICAL_ALIGN,
				mxConstants.ALIGN_MIDDLE);
		edgeStyleMap.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
		edgeStyleMap.put(mxConstants.STYLE_STROKECOLOR, Color.RED);
		// default is #6482B9
		edgeStyleMap.put(mxConstants.STYLE_FONTCOLOR, "#446299");
		mxStylesheet edgeStyle = new mxStylesheet();

		edgeStyle.setDefaultEdgeStyle(edgeStyleMap);
		graph.setStylesheet(edgeStyle);
	}

}
