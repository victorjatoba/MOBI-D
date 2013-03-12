package Listeners;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import GUI.MobiDGui;

/**
 * Listener responsible to direct for the appropriated button, 
 * sending the name of the instance that will be created
 * @author Victor Jatobá
 * @see ButtonInstanceListener
 */
@SuppressWarnings("serial")
public class ButtonInstancePopupListener extends JDialog implements ActionListener {

	private JPanel contentPane;
	private JPanel panel;
	private JTextField txtInstance;
	private MobiDGui mobiDGui;
	private JButton buttonOK;
	private String className;
	
	/**
	 * ButtonListenerInstancePopup Constructor
	 * @param mobiDGui
	 */
	public ButtonInstancePopupListener(MobiDGui mobiDGui) {

		this.mobiDGui = mobiDGui;
	}

	/**
	 * Event that direct to appropriated listener sending the 
	 * name of the instance that will be created.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		createPanels();
		
		setLayout(new FlowLayout());
		setSize(200, 100);
//		setLocationRelativeTo(null); //middle to the monitor
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(mobiDGui);//middle on the software screen
		setAlwaysOnTop(true);

		this.buttonOK = new JButton("OK");

		//is class A instance
		if(e.getSource().equals(mobiDGui.getBtnInstanceA())) {
			txtInstance = createTxtInstanceName("iA"); 
			panel.add(txtInstance, "cell 0 0,growx,aligny top");
			className = mobiDGui.getTxtClassA().getText();
			mobiDGui.setClassAName(className);
			buttonOK.addActionListener(new ButtonInstanceListener(mobiDGui, this, className));
		}else { // is class B instance
			txtInstance = createTxtInstanceName("iB"); 
			panel.add(txtInstance, "cell 0 0,growx,aligny top");
			className = mobiDGui.getTxtClassB().getText();
			mobiDGui.setClassBName(className);
			buttonOK.addActionListener(new ButtonInstanceListener(mobiDGui, this, className));
		}
		panel.add(buttonOK, "cell 0 0,growx,aligny top");
		contentPane.add(panel, "cell 0 0,alignx left,aligny top");
		
		setModal(true); //mandatory answer this dialog
		setVisible(true);
	}

	/**
	 * Create contentPane and panel
	 * */
	private void createPanels() {
		this.contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setForeground(Color.BLUE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		contentPane.setLayout(new MigLayout("", "[564px,grow]",
				"[230px,grow][159px]"));

		this.panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setForeground(Color.WHITE);
		panel.setLayout(new FlowLayout());
	}

	/**
	 * Create a new txtField with the name iA or iB
	 * @param String
	 */
	private JTextField createTxtInstanceName(String name) {
		JTextField txtInstance = new JTextField();
		txtInstance.setText(name);
		txtInstance.setColumns(10);

		return txtInstance;
	}
	
	/*Getters and setters*/
	
	public JButton getButtonOK() {
		return buttonOK;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JTextField getTxtInstance() {
		return txtInstance;
	}

	public void setButtonOK(JButton buttonOK) {
		this.buttonOK = buttonOK;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public void setTxtInstance(JTextField txtInstance) {
		this.txtInstance = txtInstance;
	}
	
}
