package bjd.ctrl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import bjd.util.IDisposable;

@SuppressWarnings("serial")
public final class ListView extends JScrollPane implements IDisposable {

	private JTable table = null;
	private DefaultTableModel model;
	private String name = "";

	public ListView(String name) {
		//自動的にスクロールバーを表示
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//table = new JTable(model);
		model = new DefaultTableModel();
		table = new JTable();
		table.setModel(model); //Tableモデルの指定
		table.setDefaultEditor(Object.class, null); //セルの編集禁止
		table.setRowHeight(20); //行の高さ
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //横幅の自動調整をOFF
		table.setShowGrid(false); //グリッド線を描画しない
		table.getTableHeader().setReorderingAllowed(false); //カラムの入れ替えを禁止

		getViewport().add(table); // ScrollPaneにtebleをセットする

		this.name = name;

	}

	@Override
	public void dispose() {
		remove(table);
		model = null;
		table = null;
	}

	@Override
	public String getName() {
		return name;
	}

	//スクロールして最終行を表示する
	public void displayLastLine() {
		int i = table.convertRowIndexToView(model.getRowCount() - 1);
		Rectangle r = table.getCellRect(i, 0, true);
		table.scrollRectToVisible(r);
	}

	//フォントの設定
	public void setFont(Font font) {
		super.setFont(font);
		if (table != null) {
			table.setFont(font);
			//table.invalidate();
		}
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (table != null) {
			table.setBackground(color);
		}
		JViewport viewport = getViewport();
		if (viewport != null) {
			viewport.setBackground(color);
		}

	}

	//*******************************************************
	//行操作
	//*******************************************************
	//行追加
	public void itemAdd(String[] str) {
		model.addRow(str);
	}

	//カラム数取得
	public int getRowCount() {
		return model.getRowCount();
	}

	// 全行削除
	public void itemClear() {
		model.setRowCount(0);
	}

	//*******************************************************
	//列操作
	//*******************************************************
	//カラム追加
	public void addColumn(String str) {
		model.addColumn(str);
	}

	//カラム数取得
	public int getColumnCount() {
		return model.getColumnCount();
	}

	//カラムの設定
	public void setColumnText(int col, String text) {

		int colMax = model.getColumnCount();
		String[] columnNames = new String[colMax];
		int[] columnWidth = new int[colMax];
		for (int i = 0; i < colMax; i++) {
			if (col == i) {
				columnNames[i] = text;
			} else {
				columnNames[i] = model.getColumnName(i);
			}
			columnWidth[i] = getColWidth(i);
		}
		model.setColumnIdentifiers(columnNames);
		for (int i = 0; i < colMax; i++) {
			setColWidth(i, columnWidth[i]);
		}

	}

	//カラムの取得
	public String getColumnText(int col) {
		return model.getColumnName(col);
	}

	//*******************************************************
	//列幅の設定・取得
	//*******************************************************
	public void setColWidth(int index, int width) {
		TableColumn col = table.getColumnModel().getColumn(index);
		col.setPreferredWidth(width);
	}

	public int getColWidth(int index) {
		TableColumn col = table.getColumnModel().getColumn(index);
		return col.getPreferredWidth();
	}

	//*******************************************************
	//選択行の設定・取得
	//*******************************************************
	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}

	//*******************************************************
	//値の設定・取得
	//*******************************************************
	public void setText(int row, int col, String text) {
		model.setValueAt(text, row, col);
	}

	public String getText(int row, int col) {
		return (String) model.getValueAt(row, col);
	}

}
