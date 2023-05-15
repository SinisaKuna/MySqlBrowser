package Servis;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MaxLenght {

    public PlainDocument maxLenght(Integer maxLen) {

        PlainDocument doc = new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (getLength() + str.length() <= maxLen) {
                    super.insertString(offs, str, a);
                }
            }
        };
        return doc;
    }

}
