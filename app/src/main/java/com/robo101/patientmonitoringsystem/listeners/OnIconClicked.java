package com.robo101.patientmonitoringsystem.listeners;

import com.robo101.patientmonitoringsystem.model.User_Patient;

public interface OnIconClicked {

    void initiateVideoMeeting(User_Patient userPatient);

    void initiateAudioMeeting(User_Patient userPatient);

}
