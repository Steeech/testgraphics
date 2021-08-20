package com.company.testgraphics.service;

import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.service.ScanProfile.Point;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<LaserTypeEnum, ScanProfile> getScanProfileWithoutPlc() {
        Map<LaserTypeEnum, ScanProfile> result = new HashMap<>();

        File file = new File("C:\\Users\\chernomyrdina\\StudioProjects\\testgraphics\\modules\\core\\src\\com\\company\\testgraphics\\service\\jsonTxt.txt");
        String json;
        try {
            json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
        JSONArray jsonArray = new JSONArray(json);
        ScanProfile scanProfileIn = new ScanProfile();
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        JSONObject profile = (JSONObject) jsonObject.get("profile_in");
        JSONObject min_point = (JSONObject) profile.get("min_point");
        scanProfileIn.setMinPoint(new Point(min_point.getDouble("x"), min_point.getDouble("z")));
        scanProfileIn.setLaserType(LaserTypeEnum.PROFILE_IN);

        ScanProfile scanProfileOut = new ScanProfile();
        jsonObject = (JSONObject) jsonArray.get(1);
        profile = (JSONObject) jsonObject.get("profile_out");
        min_point = (JSONObject) profile.get("min_point");
        scanProfileOut.setMinPoint(new Point(min_point.getDouble("x"), min_point.getDouble("z")));
        scanProfileOut.setLaserType(LaserTypeEnum.PROFILE_OUT);

        result.put(LaserTypeEnum.PROFILE_IN, scanProfileIn);
        result.put(LaserTypeEnum.PROFILE_OUT, scanProfileOut);
        return result;
    }

}