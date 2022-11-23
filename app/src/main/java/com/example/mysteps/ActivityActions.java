package com.example.mysteps;


interface ActivityActions {

    void startCountSteps();

    void stopCountSteps();

    void getTotalTimeForSteps();

    float getEstimatedDistanceOfSteps(long steps);
}