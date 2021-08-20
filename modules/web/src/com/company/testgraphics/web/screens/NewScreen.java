package com.company.testgraphics.web.screens;


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

    @Subscribe
    public void onInit(InitEvent event) {
        laserSetting = new LaserSetting(800, 700);
        vbox.unwrap(Layout.class).addComponent(laserSetting.getCanvas());
        refreshTimer.start();
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