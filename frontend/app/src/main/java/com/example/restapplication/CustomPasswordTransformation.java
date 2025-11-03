package com.example.restapplication;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

public class CustomPasswordTransformation extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private static class PasswordCharSequence implements CharSequence {
        private final CharSequence source;

        PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

        public char charAt(int index) {
            return 'ᗢ'; // ← your custom symbol here
        }

        public int length() {
            return source.length();
        }

        public CharSequence subSequence(int start, int end) {
            return source.subSequence(start, end);
        }
    }
}
