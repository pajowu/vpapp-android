package de.pajowu.vp.models;

public class RegisterRequest {
	public String dev_id;
	public String reg_id;

	public RegisterRequest(String deviceID, String fcmID) {
		dev_id = deviceID;
		reg_id = fcmID;
	}
}
