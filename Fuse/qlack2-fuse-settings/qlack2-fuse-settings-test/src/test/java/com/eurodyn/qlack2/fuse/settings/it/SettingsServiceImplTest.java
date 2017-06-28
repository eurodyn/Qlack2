package com.eurodyn.qlack2.fuse.settings.it;

import com.eurodyn.qlack2.fuse.settings.api.SettingsService;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class SettingsServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    SettingsService settingsService;

    @Test
    public void createSetting(){
        SettingDTO settingDTO = TestUtilities.createSettingDTO();
        settingsService.createSetting(settingDTO.getOwner(),settingDTO.getGroup(),settingDTO.getKey(),settingDTO.getVal(),settingDTO.isSensitive(),settingDTO.isPassword());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()));
    }

    @Test
    public void getSettings(){
        SettingDTO settingDTO = TestUtilities.createSettingDTO();
        settingsService.createSetting(settingDTO.getOwner(),settingDTO.getGroup(),settingDTO.getKey(),settingDTO.getVal(),settingDTO.isSensitive(),settingDTO.isPassword());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()));

        Assert.assertNotNull(settingsService.getSettings(settingDTO.getOwner(),true));
    }

    @Test
    public void getGroupSettings(){
        SettingDTO settingDTO = TestUtilities.createSettingDTO();
        settingsService.createSetting(settingDTO.getOwner(),settingDTO.getGroup(),settingDTO.getKey(),settingDTO.getVal(),settingDTO.isSensitive(),settingDTO.isPassword());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()));

        Assert.assertNotNull(settingsService.getGroupSettings(settingDTO.getOwner(),settingDTO.getGroup()));
    }

    @Test
    public void setVal(){
        SettingDTO settingDTO = TestUtilities.createSettingDTO();
        settingsService.createSetting(settingDTO.getOwner(),settingDTO.getGroup(),settingDTO.getKey(),settingDTO.getVal(),settingDTO.isSensitive(),settingDTO.isPassword());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()));

        settingsService.setVal(settingDTO.getOwner(),settingDTO.getKey(),"val01",settingDTO.getGroup());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()).getVal());
    }

    @Test
    public void getGroupNames(){
        SettingDTO settingDTO = TestUtilities.createSettingDTO();
        settingsService.createSetting(settingDTO.getOwner(),settingDTO.getGroup(),settingDTO.getKey(),settingDTO.getVal(),settingDTO.isSensitive(),settingDTO.isPassword());
        Assert.assertNotNull(settingsService.getSetting(settingDTO.getOwner(),settingDTO.getKey(),settingDTO.getGroup()));

        Assert.assertNotNull(settingsService.getGroupNames(settingDTO.getOwner()));
    }

}


