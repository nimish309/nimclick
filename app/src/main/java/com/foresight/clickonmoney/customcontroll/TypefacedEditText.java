package com.foresight.clickonmoney.customcontroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class TypefacedEditText extends EditText {

	public TypefacedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@SuppressLint("NewApi")
	public TypefacedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (attrs != null) {
			if (isInEditMode()) {
				return;
			}
			Typeface typeface = Typeface.createFromAsset(context.getAssets(),
					"fonts/regular.otf");
			setTypeface(typeface);
		}
	}

	public TypefacedEditText(Context context) {
		super(context);

	}

}