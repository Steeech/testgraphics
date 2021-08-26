package com.company.testgraphics.web.screens;

import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.exceptions.TubeOutOfSightException;
import com.company.testgraphics.service.PlcModuleService;
import com.company.testgraphics.service.ScanProfile.Point;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.screen.Screen;
import org.json.JSONException;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UiUpdateLaser extends BackgroundTask<Void, LaserSetting> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UiUpdateLaser.class);
    private LaserSetting laserSetting;
    private LaserSetting laserIn;
    private LaserSetting laserOut;

    private PlcModuleService plcModuleService;
    private Label<String> innerOffsetX;
    private Label<String> innerOffsetY;

    private Label<String> outerOffsetX;
    private Label<String> outerOffsetY;

    private Notifications notifications;

    private int D_nom;


    protected UiUpdateLaser(Screen screen) {
        super(1, TimeUnit.SECONDS, screen);
    }

    public static UiUpdateLaser create(Screen screen) {
        return new UiUpdateLaser(screen);
    }

    @Override
    public LaserSetting run(TaskLifeCycle<Void> taskLifeCycle) throws Exception {
        return laserSetting;
    }

    @Override
    public void done(@Nullable LaserSetting laserSetting) {

        Map<LaserTypeEnum, ScanProfile> scanProfileWithoutPlc = null;
        try {
            scanProfileWithoutPlc = plcModuleService.getScanProfile();
            if (scanProfileWithoutPlc == null) return;
            ScanProfile scanProfileIn = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_IN);
            ScanProfile scanProfileOut = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_OUT);

            Point minPointIn = scanProfileIn.getMinPoint();
            Point minPointOut = scanProfileOut.getMinPoint();

            laserSetting.getCanvas().clear();
            laserIn.getCanvas().clear();
            laserOut.getCanvas().clear();

            laserSetting.drawPattern();
            laserSetting.paintLaser(1, minPointIn.getX() - 160, -minPointIn.getY());
            laserSetting.paintLaser(-1, -1 * (minPointOut.getX() - 160), -minPointOut.getY());

            innerOffsetX.setValue(String.format("%.3f mm", minPointIn.getX() - 160));
            innerOffsetY.setValue(String.format("%.3f mm", minPointIn.getY()));

            outerOffsetX.setValue(String.format("%.3f mm", -(minPointOut.getX() - 160)));
            outerOffsetY.setValue(String.format("%.3f mm", minPointOut.getY()));

            laserIn.drawPatterForSingleLaser(D_nom);
            laserIn.paintPoints(scanProfileIn.getPointList());
            laserIn.paintMinPoint(scanProfileIn.getMinPoint());

            laserOut.drawPatterForSingleLaser(D_nom);
            laserOut.paintPoints(scanProfileOut.getPointList());
            laserOut.paintMinPoint(scanProfileOut.getMinPoint());

        } catch (JSONException ex) {
            return;
        } catch (TubeOutOfSightException e) {
            if (plcModuleService.getExceptionsCount() == 1)
                laserSetting.getCanvas().clear();
                laserSetting.getCanvas().clear();
                laserSetting.getCanvas().clear();
                notifications.create()
                        .withDescription(e.getMessage())
                        .withType(Notifications.NotificationType.ERROR)
                        .withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .show();
        } catch (Exception e) {
            if (plcModuleService.getExceptionsCount() == 1)
                notifications.create()
                        .withDescription(e.getMessage())
                        .withType(Notifications.NotificationType.ERROR)
                        .withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .show();
        }
    }

    public UiUpdateLaser setLaserSetting(LaserSetting laserSetting) {
        this.laserSetting = laserSetting;
        return this;
    }

    public UiUpdateLaser setLaserIn(LaserSetting laserIn) {
        this.laserIn = laserIn;
        return this;
    }

    public UiUpdateLaser setLaserOut(LaserSetting laserOut) {
        this.laserOut = laserOut;
        return this;
    }

    public UiUpdateLaser setPlcModuleService(PlcModuleService plcModuleService) {
        this.plcModuleService = plcModuleService;
        return this;
    }

    public UiUpdateLaser setInnerOffsetX(Label<String> innerOffsetX) {
        this.innerOffsetX = innerOffsetX;
        return this;
    }

    public UiUpdateLaser setInnerOffsetY(Label<String> innerOffsetY) {
        this.innerOffsetY = innerOffsetY;
        return this;
    }

    public UiUpdateLaser setOuterOffsetX(Label<String> outerOffsetX) {
        this.outerOffsetX = outerOffsetX;
        return this;
    }

    public UiUpdateLaser setOuterOffsetY(Label<String> outerOffsetY) {
        this.outerOffsetY = outerOffsetY;
        return this;
    }

    public UiUpdateLaser setD_nom(int d_nom) {
        D_nom = d_nom;
        return this;
    }

    public UiUpdateLaser setNotifications(Notifications notifications) {
        this.notifications = notifications;
        return this;
    }
}
