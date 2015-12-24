package com.foresight.clickonmoney.customcontroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.foresight.clickonmoney.R;

public class TypefacedRadioButton extends RadioButton {

	@SuppressLint("NewApi")
	public TypefacedRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (attrs != null) {
			TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
					R.styleable.TypefacedRadioButton);
			int typefaceCode = styledAttrs.getInt(
					R.styleable.TypefacedRadioButton_fontStyle, -1);
			styledAttrs.recycle();

			// Typeface.createFromAsset doesn't work in the layout editor.
			// Skipping...
			if (isInEditMode()) {
				return;
			}

			Typeface typeface = TypefaceCache.get(context.getAssets(),
					typefaceCode);
			setTypeface(typeface);
		}

//		if (attrs != null) {
//			if (isInEditMode()) {
//				return;
//			}
//			Typeface typeface = Typeface.createFromAsset(context.getAssets(),
//					"fonts/regular.otf");
//			setTypeface(typeface);
//		}
	}

	public TypefacedRadioButton(Context context) {
		super(context);

	}
}
