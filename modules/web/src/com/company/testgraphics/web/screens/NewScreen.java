package com.company.testgraphics.web.screens;


import com.company.testgraphics.entity.LaserTypeEnum;
import com.company.testgraphics.service.PlcModuleService;
import com.company.testgraphics.service.ScanProfile.Point;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
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
import java.util.Map;

@UiController("testgraphics_")
@UiDescriptor("new-screen.xml")
public class NewScreen extends Screen {
    @Inject
    private VBoxLayout vbox;
    @Inject
    private Logger log;

    private LaserSetting laserSetting;
    @Inject
    private Timer refreshTimer;
    private BackgroundTaskWrapper<Void, LaserSetting> refreshTaskWrapper = new BackgroundTaskWrapper<>();

    private int offsetY;
    @Inject
    private PlcModuleService plcModuleService;

    @Subscribe
    public void onInit(InitEvent event) {
        laserSetting = new LaserSetting(800, 700);
        vbox.unwrap(Layout.class).addComponent(laserSetting.getCanvas());
        Map<LaserTypeEnum, ScanProfile> scanProfileWithoutPlc = plcModuleService.getScanProfileWithoutPlc();
        Point minPointIn = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_IN).getMinPoint();
        Point minPointOut = scanProfileWithoutPlc.get(LaserTypeEnum.PROFILE_OUT).getMinPoint();

        laserSetting.drawPattern();
        laserSetting.paintLaser(1, minPointIn.getY()-160,-minPointIn.getX());
        laserSetting.paintLaser(-1, 160-minPointOut.getY(),minPointOut.getX());
//        refreshTimer.start();
        offsetY = 1;
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