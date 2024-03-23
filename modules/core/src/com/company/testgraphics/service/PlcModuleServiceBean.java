package com.company.testgraphics.service;

import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.exceptions.TubeOutOfSightException;
import com.company.testgraphics.service.ScanProfile.Point;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service(PlcModuleService.NAME)
public class PlcModuleServiceBean implements PlcModuleService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PlcModuleServiceBean.class);
    private String baseUrl = "http://0.0.0.0:8080";
    private String getScanProfile = "/get-scan-profile";

    private boolean isServerAvailable = true;
    private int exceptionsCount = 0;

    @Override
    public Map<LaserTypeEnum, ScanProfile> getScanProfile() throws Exception {
        if (!isServerAvailable) return null;
        RestTemplate restTemplate = new RestTemplate();
        String fullReq = baseUrl + getScanProfile;
        Map<LaserTypeEnum, ScanProfile> resultMap = new HashMap<>();
        String result = "";
        try {
            result = restTemplate.getForObject(fullReq, String.class);
            if (result != null) {
                if (result.equals("Error: Scanners don't see the tube!")) throw new TubeOutOfSightException("Труба вне зоны видимости сканера");

                JSONObject jsonObject = new JSONObject(result);
                ScanProfile scanProfileIn = getScanProfileFromJson("profile_in", jsonObject);
                ScanProfile scanProfileOut = getScanProfileFromJson("profile_out", jsonObject);

                resultMap.put(LaserTypeEnum.PROFILE_IN, scanProfileIn);
                resultMap.put(LaserTypeEnum.PROFILE_OUT, scanProfileOut);

                exceptionsCount = 0;
                return resultMap;
            }
            else {
                throw new Exception("Result is Null");
            }
            //TODO exception
        } catch (JSONException ex){
            log.info("JSONException");
            return null;
        }
        catch (TubeOutOfSightException ex){
            exceptionsCount++;
            throw ex;
        }
        catch (Exception ex) {
            log.error("Error", ex);
//            return null;
            //TODO exception
            exceptionsCount++;
            throw ex;
        }
    }

    @Override
    public Map<LaserTypeEnum, ScanProfile> getScanProfileWithoutPlc() throws IOException, TubeOutOfSightException {
        Map<LaserTypeEnum, ScanProfile> result = new HashMap<>();


        String json = readUsingFiles("jsonTxt.txt");

        if (json.equals("Error: Scanners don't see the tube!\r\n")) {
            exceptionsCount++;
            throw new TubeOutOfSightException("Труба вне зоны видимости сканера");
        }
        JSONArray jsonArray = new JSONArray(json);
        ScanProfile scanProfileIn = getScanProfileFromJson(jsonArray, "profile_in", 0);
        ScanProfile scanProfileOut = getScanProfileFromJson(jsonArray, "profile_out", 1);

        result.put(LaserTypeEnum.PROFILE_IN, scanProfileIn);
        result.put(LaserTypeEnum.PROFILE_OUT, scanProfileOut);
        return result;
    }

    private ScanProfile getScanProfileFromJson(JSONArray jsonArray, String laserType, int index){

        JSONObject jsonObject = jsonArray.getJSONObject(index);
        return getScanProfileFromJson(laserType, jsonObject);
    }

    private ScanProfile getScanProfileFromJson(String laserType, JSONObject jsonObject){
        ScanProfile scanProfile = new ScanProfile();

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

    @Override
    public int getExceptionsCount() {
        return exceptionsCount;
    }
}
