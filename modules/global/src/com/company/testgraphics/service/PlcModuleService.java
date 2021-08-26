package com.company.testgraphics.service;

import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.exceptions.TubeOutOfSightException;
import com.company.testgraphics.service.ScanProfile.ScanProfile;

import java.io.IOException;
import java.util.Map;

public interface PlcModuleService {
    String NAME = "testgraphics_PlcModuleService";

    Map<LaserTypeEnum, ScanProfile> getScanProfile() throws Exception;

    Map<LaserTypeEnum, ScanProfile> getScanProfileWithoutPlc() throws IOException, TubeOutOfSightException;

    int getExceptionsCount();
}