package com.foresight.clickonmoney.pushnotification;

public interface GCMConfig {

	// When you are using two simulator for testing this application.
	// Make SECOND_SIMULATOR value true when opening/installing application in
	// second simulator
	// Actually we are validating/saving device data on IMEI basis.
	// if it is true IMEI number change for second simulator

	static final boolean SECOND_SIMULATOR = false;

	// Google project id
	static final String GOOGLE_SENDER_ID = "390095134371";

	static final String packageName = "com.foresight.clickonmoney";

	/**
	 * Tag used on log messages.
	 */
	// Broadcast reciever name to show gcm registration messages on screen
	static final String DISPLAY_REGISTRATION_MESSAGE_ACTION = packageName
			+ ".pushnotification.DISPLAY_REGISTRATION_MESSAGE";

	// Broadcast reciever name to show user messages on screen
	static final String DISPLAY_MESSAGE_ACTION = packageName
			+ ".DISPLAY_MESSAGE";

	// Parse server message with this name
	static final String EXTRA_MESSAGE = "message";
}