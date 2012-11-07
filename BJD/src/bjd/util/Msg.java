package bjd.util;

import javax.swing.JOptionPane;

/**
 * メッセージダイログを表示するクラス
 * @author user1
 *
 */
public final class Msg {

	private Msg() {
		//デフォルトコンストラクタの隠蔽
	}

	public static int show(MsgKind msgKind, String msg) {
		String title = "BlackJumboDog";
		int optionType = JOptionPane.DEFAULT_OPTION;
		int messageType = JOptionPane.PLAIN_MESSAGE;
		switch (msgKind) {
			case ERROR:
				messageType = JOptionPane.ERROR_MESSAGE;
				break;
			case STOP:
				messageType = JOptionPane.ERROR_MESSAGE;
				break;
			case QUESTION:
				messageType = JOptionPane.QUESTION_MESSAGE;
				optionType = JOptionPane.YES_NO_CANCEL_OPTION;
				break;
			case INFOMATION:
				messageType = JOptionPane.INFORMATION_MESSAGE;
				break;
			case WARNING:
				messageType = JOptionPane.WARNING_MESSAGE;
				break;
			default:
				break;

		}
  
		return JOptionPane.showConfirmDialog(null, msg, title, optionType, messageType);
	}

//	static void setFont(Component[] components, Font font) {
//		if (components == null) {
//			return;
//		}
//		for (int i = 0; i < components.length; i++) {
//			components[i].setFont(font);
//			setFont(((JComponent) components[i]).getComponents(), font);
//		}
//	}
}
