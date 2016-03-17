package com.teambulldozer.hett;

import java.io.Serializable;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendDto implements Serializable {
    private int id;
    private String friendName;
    private String friendTalkSt;
    private double todayPoint;
    private double totalPoint;

    public FriendDto() {
    }

    public FriendDto(int id, String friendName, String friendTalkSt, double todayPoint, double totalPoint) {
        super();
        this.id = id;
        this.friendName = friendName;
        this.friendTalkSt = friendTalkSt;
        this.todayPoint = todayPoint;
        this.totalPoint = totalPoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendTalkSt() {
        return friendTalkSt;
    }

    public void setFriendTalkSt(String friendTalkSt) {
        this.friendTalkSt = friendTalkSt;
    }

    public double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(double totalPoint) {
        this.totalPoint = totalPoint;
    }


}
