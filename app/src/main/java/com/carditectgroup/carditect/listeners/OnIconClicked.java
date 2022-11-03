package com.carditectgroup.carditect.listeners;

import com.carditectgroup.carditect.model.User_Patient;

public interface OnIconClicked {

    void initiateVideoMeeting(User_Patient userPatient);

    void initiateAudioMeeting(User_Patient userPatient);

}
