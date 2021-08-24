package com.company.testgraphics.service;

import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.service.ScanProfile.ScanProfile;

import java.io.IOException;
import java.util.Map;

public interface PlcModuleService {
    String NAME = "testgraphics_PlcModuleService";

    ScanProfile getScanProfile();

    Map<LaserTypeEnum, ScanProfile> getScanProfileWithoutPlc() throws IOException;
}