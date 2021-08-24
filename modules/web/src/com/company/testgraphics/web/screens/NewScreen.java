package com.company.testgraphics.web.screens;


import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.service.PlcModuleService;
import com.company.testgraphics.service.ScanProfile.Point;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.executors.BackgroundTaskWrapper;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.vaadin.ui.Layout;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

@UiController("testgraphics_")
@UiDescriptor("new-screen.xml")
public class NewScreen extends Screen {
    @Inject
    private VBoxLayout vbox;
    @Inject
    private Logger log;

    private LaserSetting laserSetting;
    private LaserSetting laserIn;
    private LaserSetting laserOut;
    @Inject
    private Timer refreshTimer;
    private BackgroundTaskWrapper<Void, LaserSetting> refreshTaskWrapper = new BackgroundTaskWrapper<>();

    private int offsetY;
    @Inject
    private PlcModuleService plcModuleService;
    @Inject
    private VBoxLayout inner;
    @Inject
    private VBoxLayout outer;
    @Inject
    private Label<String> innerOffsetX;
    @Inject
    private Label<String> innerOffsetY;
    @Inject
    private Label<String> outerOffsetX;
    @Inject
    private Label<String> outerOffsetY;

    @Subscribe
    public void onInit(InitEvent event) {
        laserSetting = new LaserSetting(800, 720);
        vbox.unwrap(Layout.class).addComponent(laserSetting.getCanvas());

        laserIn = new LaserSetting(400, 400);
        laserIn.setCellSize(20);
        inner.unwrap(Layout.class).addComponent(laserIn.getCanvas());

        laserOut = new LaserSetting(400, 400);
        laserOut.setCellSize(20);
        outer.unwrap(Layout.class).addComponent(laserOut.getCanvas());

        Map<LaserTypeEnum, ScanProfile> scanProfileWithoutPlc = null;
        try {
            scanProfileWithoutPlc = plcModuleService.getScanProfileWithoutPlc();
            ScanProfile scanProfileIn = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_IN);
            ScanProfile scanProfileOut = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_OUT);

            Point minPointIn = scanProfileIn.getMinPoint();
            Point minPointOut = scanProfileOut.getMinPoint();

            laserSetting.drawPattern();
            laserSetting.paintLaser(1, minPointIn.getX()-160,-minPointIn.getY());
            laserSetting.paintLaser(-1, -1*(minPointOut.getX()-160),-minPointOut.getY());

            innerOffsetX.setValue(String.format("%.3f mm", minPointIn.getX()-160));
            innerOffsetY.setValue(String.format("%.3f mm", minPointIn.getY()));

            outerOffsetX.setValue(String.format("%.3f mm", -(minPointOut.getX()-160)));
            outerOffsetY.setValue(String.format("%.3f mm", minPointOut.getY()));


            laserIn.drawPatterForSingleLaser();
            laserIn.paintPoints(scanProfileIn.getPointList());

            laserOut.drawPatterForSingleLaser();
            laserOut.paintPoints(scanProfileOut.getPointList());
//        refreshTimer.start();
            offsetY = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Subscribe("refreshTimer")
    public void onRefreshTimerTimerAction(Timer.TimerActionEvent event) {
        offsetY++;
        if (offsetY > 100) {
            refreshTimer.stop();
        } else {
            refreshTaskWrapper.restart(UiUpdateLaser.create(this)
                    .setLaserSetting(laserSetting)
                    .setOffsetY(offsetY)
                    .setOffsetX(0)
            );
        }
    }


}