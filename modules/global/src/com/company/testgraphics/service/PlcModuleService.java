package com.company.testgraphics.service;

import com.company.testgraphics.service.ScanProfile.ScanProfile;

public interface PlcModuleService {
    String NAME = "testgraphics_PlcModuleService";

    ScanProfile getScanProfile();
}