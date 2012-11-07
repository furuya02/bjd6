package bjd.ctrl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ステータスバー　コントロール
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public final class StatusBar extends JPanel {

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(10, 23));

		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(new JLabel(new String()), BorderLayout.SOUTH);
		rightPanel.setOpaque(false);

		add(rightPanel, BorderLayout.EAST);
		setBackground(SystemColor.control);
	}

	//	protected void paintComponent2(Graphics g) {
	//		super.paintComponent(g);
	//
	//		int y = 0;
	//		g.setColor(new Color(156, 154, 140));
	//		g.drawLine(0, y, getWidth(), y);
	//		y++;
	//		g.setColor(new Color(196, 194, 183));
	//		g.drawLine(0, y, getWidth(), y);
	//		y++;
	//		g.setColor(new Color(218, 215, 201));
	//		g.drawLine(0, y, getWidth(), y);
	//		y++;
	//		g.setColor(new Color(233, 231, 217));
	//		g.drawLine(0, y, getWidth(), y);
	//
	//		y = getHeight() - 3;
	//		g.setColor(new Color(233, 232, 218));
	//		g.drawLine(0, y, getWidth(), y);
	//		y++;
	//		g.setColor(new Color(233, 231, 216));
	//		g.drawLine(0, y, getWidth(), y);
	//		y = getHeight() - 1;
	//		g.setColor(new Color(221, 221, 220));
	//		g.drawLine(0, y, getWidth(), y);
	//	}

}
