package com.example.restapplication;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.util.Log;

import java.lang.Runnable;

public class CustomPasswordTransformation extends PasswordTransformationMethod {

    private static final char MASK = 'ᗢ';
    private PasswordCharSequence lastSequence;

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        lastSequence = new PasswordCharSequence(source);
	return lastSequence;
    }
	
    public void toggleLastCharMask(boolean masked){
	lastSequence.setMask(masked);
    }	

    private static class PasswordCharSequence implements CharSequence {
        private final CharSequence source;
	private boolean isMasked;

        PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

	@Override
        public char charAt(int index) {
	    return index == (source.length()-1) && !isMasked ? source.charAt(index) : MASK;
        }

	@Override
        public int length() {
            return source.length();
        }

	@Override
        public CharSequence subSequence(int start, int end) {
            return source.subSequence(start, end);
        }

	public void setMask(boolean isMasked){
		this.isMasked = isMasked;
	}
    }
}
