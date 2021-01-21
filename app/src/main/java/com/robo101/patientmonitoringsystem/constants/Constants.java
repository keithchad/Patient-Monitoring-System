package com.robo101.patientmonitoringsystem.constants;

import java.util.HashMap;

public class Constants {

    //Authentication
    public static final String MOBILE = "mobile";
    public static final String VERIFICATION_ID = "verificationId";

    //Database (User)
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageUrl";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL = "email";
    public static final String HOSPITAL = "hospital";
    public static final String SPECIALIZATION = "specialization";
    public static final String BIRTHDAY = "birthday";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String DUMMY_IMAGE = "https://firebasestorage.googleapis.com/v0/b/patient-monitoring-syste-c3a8f.appspot.com/o/dummy_profile.png?alt=media&token=6ebb7f75-b9ac-4a0a-abfa-0dc40a846c71";

    //Database(Vitals)
    public static final String HEARTBEAT = "heartBeat";
    public static final String BLOOD_OXYGEN = "bloodOxygen";
    public static final String BLOOD_PRESSURE = "bloodPressure";
    public static final String BODY_TEMPERATURE = "bodyTemperature";

    //Shared Preferences
    public static final String KEY_PREFERENCE_NAME = "patientPrefs";
    public static final String USER_NAME = "userName";
    public static final String IS_LOGGED_IN = "isLoggedIn";

    //Maps
    public static final int PERMISSION_REQUEST_CODE = 21;

    //Firebase Messaging
    public static  final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public  static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public  static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                ""
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }


}
