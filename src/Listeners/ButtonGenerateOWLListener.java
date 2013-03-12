package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JRadioButton;

import mobi.core.Mobi;
import mobi.core.cardinality.Cardinality;
import mobi.core.common.Relation;
import mobi.core.relation.CompositionRelation;
import mobi.core.relation.GenericRelation;
import mobi.core.relation.InheritanceRelation;
import mobi.core.relation.SymmetricRelation;
import mobi.extension.export.owl.Mobi2OWL;

import GUI.MobiDGui;

public class ButtonGenerateOWLListener implements ActionListener {

	MobiDGui mobiDGui;
	private Relation genericRelation;
	private Mobi mobi;
	private JRadioButton radioButtonTypeRelation;

	public ButtonGenerateOWLListener(MobiDGui mobiDGui) {
		this.mobiDGui = mobiDGui;
		this.mobi = mobiDGui.getMobi();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			genericRelation = getGenericRelationIntoMobi();
			
			genericRelation.processCardinality();
			Cardinality cardinalityA = genericRelation.getCardinalityA();
			Cardinality cardinalityB = genericRelation.getCardinalityB();

			System.out.println("BtnGenerateOWLListener| cardinalityA: " + cardinalityA
					+ " cardinalityB:" + cardinalityB);

			mobi.addConcept(genericRelation);

			Collection<Integer> possibilities = mobi
					.infereRelation(genericRelation);

			/*
			 * Passo 19 Salienta-se que se for um relação de herança ,
			 * composição bidirecional e simétrica haverão conversões das
			 * seguintes formas respectivamente:
			 */
			if (!possibilities.isEmpty()) {
				// if(possibilities.contains(genericRelation.EQUIVALENCE)) {
				// System.out.println("Equivalence");
				if (possibilities.contains(genericRelation.INHERITANCE)) {
					InheritanceRelation inheritanceRelation = (InheritanceRelation) mobi
							.convertToInheritanceRelation(genericRelation,
									"inheritance");
					mobi.addConcept(inheritanceRelation);
					radioButtonTypeRelation = mobiDGui.getRdbtnInheritance();
					System.out.println("Inheritance");
				} else if (possibilities
						.contains(genericRelation.SYMMETRIC_COMPOSITION)) {
					SymmetricRelation symmetric = (SymmetricRelation) mobi
							.convertToSymmetricRelation(genericRelation,
									"simetrico");
					mobi.addConcept(symmetric);
					radioButtonTypeRelation = mobiDGui.getRdbtnEquivalence();
					System.out.println("symetric");
				} else if (possibilities
						.contains(genericRelation.BIDIRECIONAL_COMPOSITION)) {
					CompositionRelation composition = (CompositionRelation) mobi
							.convertToBidirecionalCompositionRelationship(
									genericRelation, "tem", "pertence");
					mobi.addConcept(composition);
					radioButtonTypeRelation = mobiDGui.getRdbtnComposition();
					System.out.println("composition");
				}
			} else {
				radioButtonTypeRelation = null;
				System.out
						.println("BtnGenerateOWLListener|  Problem: No Relation type infere");
			}

			if (radioButtonTypeRelation != null) {
				radioButtonTypeRelation.setSelected(true);
			}
			deselectOthersTypeRelationField(radioButtonTypeRelation);

			System.out.println("---Possibilities:");
			for (Integer i : possibilities) {
				System.out.println(i.toString());
			}

			/*
			 * Generate OWL
			 */
			Mobi2OWL mobi2OWL = new Mobi2OWL("http://www.mobi.org/", mobi);
			mobi2OWL.setExportPath("C:\\BaseOntologia");
			mobi2OWL.exportMobiToOWL("TestOntology.owl");

			System.out.println("OWL Successfull created!");

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

}
