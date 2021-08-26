package com.company.testgraphics.web.screens;


import com.company.testgraphics.entity.TubeKindDNomEnum;
import com.company.testgraphics.service.PlcModuleService;
import com.company.testgraphics.service.ScanProfile.ScanProfile;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.executors.BackgroundTaskWrapper;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.vaadin.ui.Layout;
import org.slf4j.Logger;

import javax.inject.Inject;

@UiController("testgraphics_")
@UiDescriptor("new-screen.xml")
public class NewScreen extends Screen {
    @Inject
    private VBoxLayout vbox;
    @Inject
    private Logger log;

    TubeKindDNomEnum selectedD;

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

    private int windowHeight, windowWidth;
    @Inject
    private LookupField<TubeKindDNomEnum> choiceD;

    private ScanProfile scanProfileIn;
    private ScanProfile scanProfileOut;
    @Inject
    private Notifications notifications;


    @Subscribe
    public void onInit(InitEvent event) {

        laserSetting = new LaserSetting(800);
        laserSetting.setCellSize(10);
        vbox.unwrap(Layout.class).addComponent(laserSetting.getCanvas());

        laserIn = new LaserSetting(400);
        laserIn.setCellSize(laserSetting.getCellSize() * 2);
        inner.unwrap(Layout.class).addComponent(laserIn.getCanvas());

        laserOut = new LaserSetting(400);
        laserOut.setCellSize(laserSetting.getCellSize() * 2);
        outer.unwrap(Layout.class).addComponent(laserOut.getCanvas());

        choiceD.setOptionsEnum(TubeKindDNomEnum.class);
        choiceD.setValue(TubeKindDNomEnum.FiveHundredThirty);
        selectedD = choiceD.getValue();

        refreshTimer.start();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {

    }

    @Subscribe("refreshTimer")
    public void onRefreshTimerTimerAction(Timer.TimerActionEvent event) {
        refreshTaskWrapper.restart(UiUpdateLaser.create(this)
                .setPlcModuleService(plcModuleService)
                .setLaserSetting(laserSetting)
                .setLaserIn(laserIn)
                .setLaserOut(laserOut)
                .setInnerOffsetX(innerOffsetX)
                .setInnerOffsetY(innerOffsetY)
                .setOuterOffsetX(outerOffsetX)
                .setOuterOffsetY(outerOffsetY)
                .setD_nom(selectedD.getId())
                .setNotifications(notifications)
        );
    }

    @Subscribe("choiceD")
    public void onChoiceDValueChange(HasValue.ValueChangeEvent event) {
        selectedD = choiceD.getValue();
    }


}