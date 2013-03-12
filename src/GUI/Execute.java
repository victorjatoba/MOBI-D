package GUI;

import mobi.core.Mobi;
import mobi.core.concept.Instance;

/**
 * Class for execute the program
 * @author Jatoba
 * */
public class Execute {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MobiDGui frame = new MobiDGui();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
