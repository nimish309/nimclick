package com.foresight.clickonmoney.customcontroll;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TypedfacedButton extends Button {

	public TypedfacedButton(Context context, AttributeSet attrs) {

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

	public TypedfacedButton(Context context) {
		super(context);
	}

}