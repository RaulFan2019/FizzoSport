package cn.hwh.sports.ui.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/7/21.
 */
public class EditTextPhone extends EditText {


    public EditTextPhone(Context context) {
        super(context);
        addInputListener();
    }

    public EditTextPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        addInputListener();
    }

    public EditTextPhone(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addInputListener();
    }


    private void addInputListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
                String contents = str.toString();
                int length = contents.length();
                if (length == 4) {
                    if (contents.substring(3).equals(new String(" "))) {
                        contents = contents.substring(0, 3);
                        setText(contents);
                        setSelection(contents.length());
                    } else {
                        contents = contents.substring(0, 3) + " " + contents.substring(3);
                        setText(contents);
                        setSelection(contents.length());
                    }
                } else if (length == 9) {
                    if (contents.substring(8).equals(new String(" "))) {
                        contents = contents.substring(0, 8);
                        setText(contents);
                        setSelection(contents.length());
                    } else {// +
                        contents = contents.substring(0, 8) + " " + contents.substring(8);
                        setText(contents);
                        setSelection(contents.length());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }
}
