package bjd.ctrl;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

// 数値入力に制限する
public final class IntegerDocumentFilter extends DocumentFilter {
    private int digits;

    public IntegerDocumentFilter(int digits) {
        this.digits = digits;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) {
            return;
        } else {
            replace(fb, offset, 0, string, attr);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        replace(fb, offset, length, "", null);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        Document doc = fb.getDocument();
        int currentLength = doc.getLength();
        String currentContent = doc.getText(0, currentLength);
        String before = currentContent.substring(0, offset);
        String after = currentContent.substring(length + offset, currentLength);
        String newValue = before + (text == null ? "" : text) + after;

        if (newValue.length() > digits) { // 桁オーバー
            return;
        }

        checkInput(newValue, offset);
        fb.replace(offset, length, text, attrs);
    }

    private int checkInput(String str, int offset) throws BadLocationException {
        int ret = 0;
        if (str.length() > 0) {
            try {
                ret = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                throw new BadLocationException(str, offset);
            }
        }
        return ret;
    }
}

