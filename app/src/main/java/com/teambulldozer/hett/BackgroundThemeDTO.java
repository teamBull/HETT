package com.teambulldozer.hett;

import java.io.Serializable;

/**
 * Created by GHKwon on 2016-02-27.
 */
public class BackgroundThemeDTO implements Serializable {
    private int backgroundCode;
    private String backgroundThemeName;
    private int isBackgroundPermission;
    private int isSelected;
    public BackgroundThemeDTO(){}
    public BackgroundThemeDTO(int backgroundCode, String backgroundThemeName, int isBackgroundPermission,int isSelected) {
        this.backgroundCode = backgroundCode;
        this.backgroundThemeName = backgroundThemeName;
        this.isBackgroundPermission = isBackgroundPermission;
        this.isSelected=isSelected;
    }
    public int getBackgroundCode() { return backgroundCode;}
    public String getBackgroundThemeName() { return backgroundThemeName; }
    public int getIsBackgroundPermission() {return isBackgroundPermission;}
    public int getIsSelected() {return isSelected;}

    public void setIsSelected(int isSelected) {this.isSelected = isSelected;}
    public void setBackgroundCode(int backgroundCode) {this.backgroundCode = backgroundCode; }
    public void setBackgroundThemeName(String backgroundThemeName) { this.backgroundThemeName = backgroundThemeName; }
    public void setIsBackgroundPermission(int isBackgroundPermission) {this.isBackgroundPermission = isBackgroundPermission; }
    public String toString() { return "BackgroundThemeDTO{"+"backgroundCode=" + backgroundCode +", backgroundThemeName='"
            + backgroundThemeName + '\'' +", isBackgroundPermission=" + isBackgroundPermission +'}'; }
}