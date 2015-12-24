package com.foresight.clickonmoney.customcontroll;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypefaceCache {

	private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

	private static final String MUSEO = "fonts/regular.otf";
	private static final String MUSEOLIGHT = "fonts/light.otf";
	private static final String MUSEOBOLD = "fonts/bold.otf";
	
	public static Typeface get(AssetManager manager, int typefaceCode) {
		synchronized (CACHE) {

			String typefaceName = getTypefaceName(typefaceCode);

			if (!CACHE.containsKey(typefaceName)) {
				Typeface t = Typeface.createFromAsset(manager, typefaceName);
				CACHE.put(typefaceName, t);
			}
			return CACHE.get(typefaceName);
		}
	}

	private static String getTypefaceName(int typefaceCode) {
		switch (typefaceCode) {
		case 0:
			return MUSEO;
		case 1:
			return MUSEOLIGHT;
		case 2:
			return MUSEOBOLD;
		default:
			return null;
		}
	}

}