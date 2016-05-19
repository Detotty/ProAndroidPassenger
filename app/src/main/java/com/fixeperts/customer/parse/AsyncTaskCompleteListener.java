package com.fixeperts.customer.parse;

/**
 * @author Hardik A Bhalodi
 */
public interface AsyncTaskCompleteListener {
	void onTaskCompleted(String response, int serviceCode);
}
