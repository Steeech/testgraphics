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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    public Map<LaserTypeEnum, ScanProfile> getScanProfileWithoutPlc() throws IOException {
        Map<LaserTypeEnum, ScanProfile> result = new HashMap<>();

        String json = readUsingFiles("jsonTxt.txt");

        JSONArray jsonArray = new JSONArray(json);
        ScanProfile scanProfileIn = getScanProfileFromJson(jsonArray, "profile_in", 0);
        ScanProfile scanProfileOut = getScanProfileFromJson(jsonArray, "profile_out", 1);

        result.put(LaserTypeEnum.PROFILE_IN, scanProfileIn);
        result.put(LaserTypeEnum.PROFILE_OUT, scanProfileOut);
        return result;
    }

    private ScanProfile getScanProfileFromJson(JSONArray jsonArray, String laserType, int index){
        ScanProfile scanProfile = new ScanProfile();

        JSONObject jsonObject = jsonArray.getJSONObject(index);

        JSONObject profile = jsonObject.getJSONObject(laserType);
        JSONObject min_point = profile.getJSONObject("min_point");
        scanProfile.setSize(profile.getInt("points_count"));
        scanProfile.setMinPoint(new Point(min_point.getDouble("z"), min_point.getDouble("x")));
        scanProfile.setLaserType(LaserTypeEnum.fromId(laserType));

        ArrayList<Point> list = new ArrayList<>();
        JSONObject point;
        JSONArray pointsArray = profile.getJSONArray("points");
        if (pointsArray != null) {
            int len = pointsArray.length();
            for (int i=0;i<len;i++){
                point = pointsArray.getJSONObject(i);
                list.add(new Point(point.getDouble("x"), point.getDouble("z")));
            }
        }

        scanProfile.setPointList(list);

        return scanProfile;
    }

    private String readUsingFiles(String fileName) throws IOException, NullPointerException {
        String everything;
        try (InputStream in = getClass().getResourceAsStream(fileName)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                everything = sb.toString();
            }
        }
        return everything;
    }

}