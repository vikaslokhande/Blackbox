package com.whiznic.blackbox1.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class UIHelper {
	

	private Context context;
	
	public UIHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void setTypeFace(View rootView) {
		if(rootView instanceof ViewGroup) {
            setFonts((ViewGroup) rootView);
		}
	}
	
	private void setFonts(ViewGroup group) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Helvetica.ttf");
		
		for (int i = 0; i < group.getChildCount(); i++) {
			View view = group.getChildAt(i);
			if(view instanceof ViewGroup)
                setFonts((ViewGroup) view);
			else if(view instanceof EditText) {
				EditText editText = (EditText) view;
                try {
                    int style = editText.getTypeface().getStyle();
                    editText.setTypeface(typeface, style);
                } catch (Exception e) {
                    editText.setTypeface(typeface);
                }
            } else if(view instanceof TextView) {
				TextView textView = (TextView) view;
                try {
                    int style = textView.getTypeface().getStyle();
                    textView.setTypeface(typeface, style);
                } catch (Exception e) {
                    textView.setTypeface(typeface);
                }
            } else if(view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                try {
                    int style = radioButton.getTypeface().getStyle();
                    radioButton.setTypeface(typeface, style);
                } catch (Exception e) {
                    radioButton.setTypeface(typeface);
                }
            } else if(view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                try {
                    int style = checkBox.getTypeface().getStyle();
                    checkBox.setTypeface(typeface, style);
                } catch (Exception e) {
                    checkBox.setTypeface(typeface);
                }
            } else if(view instanceof Button) {
				Button button = (Button) view;
                try {
                    int style = button.getTypeface().getStyle();
                    button.setTypeface(typeface, style);
                } catch (Exception e) {
                    button.setTypeface(typeface);
                }
            }

		}
	}
	

}
