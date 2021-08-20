package com.company.testgraphics.service;

import com.company.testgraphics.service.ScanProfile.ScanProfile;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service(PlcModuleService.NAME)
public class PlcModuleServiceBean implements PlcModuleService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PlcModuleServiceBean.class);
    private String baseUrl = "http://85.113.38.35:65116";
    private String getScanProfile = "/get-scan-profile";

    private boolean isServerAvailable = true;

    @Override
    public ScanProfile getScanProfile() {
        if (!isServerAvailable) return null;
        RestTemplate restTemplate = new RestTemplate();
        String fullReq = baseUrl + getScanProfile;
        try {
            String result = restTemplate.getForObject(fullReq, String.class);
            if (result != null) {
                if (result.equals("Error: Scanners don't see the tube!")) throw new Exception(result);
                JSONObject jsonObject = new JSONObject(result);
                int stop = 0;
            }
            //TODO exception
            return null;
        } catch (Exception ex) {
            log.error("Error", ex);
            //TODO exception
            //throw new ProcessingModuleError(ex.getMessage(), ex);
            return null;
        }
    }
}