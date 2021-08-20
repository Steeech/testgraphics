package com.company.testgraphics.web.screens;

import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.screen.Screen;
import org.vaadin.hezamu.canvas.Canvas;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class UiUpdateLaser extends BackgroundTask<Void, LaserSetting> {

    private LaserSetting laserSetting;
    private double offsetX;
    private double offsetY;

    protected UiUpdateLaser(Screen screen) {
        super(50, TimeUnit.MILLISECONDS, screen);
    }

    public static UiUpdateLaser create(Screen screen){
        return new UiUpdateLaser(screen);
    }

    @Override
    public LaserSetting run(TaskLifeCycle<Void> taskLifeCycle) throws Exception {
        return laserSetting;
    }

    @Override
    public void done(@Nullable LaserSetting laserSetting) {
        laserSetting.getCanvas().clear();
        laserSetting.drawPattern();
        laserSetting.paintLaser(1, offsetX, offsetY);
    }




    public UiUpdateLaser setLaserSetting(LaserSetting laserSetting) {
        this.laserSetting = laserSetting;
        return this;
    }


    public UiUpdateLaser setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        return this;
    }


    public UiUpdateLaser setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        return this;
    }
}
