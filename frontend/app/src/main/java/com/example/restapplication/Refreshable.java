package com.example.restapplication;

import java.lang.Runnable;

public interface Refreshable{
	void refresh(Runnable onComplete);
}
