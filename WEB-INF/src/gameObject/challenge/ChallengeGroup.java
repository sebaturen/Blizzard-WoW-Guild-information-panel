/**
 * File : ChallengeGroup.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.challenge;

import com.artOfWar.gameObject.Member;

import java.util.ArrayList;
import java.util.List;

public class ChallengeGroup extends GameObject
{
    //Attribute
    private int id;
    private Date timeDate;
    private int timeHours;
    private int timeMinutes;
    private int timeSeconds;
    private int timeMilliseconds;
    private boolean isPositive;
    private List<Member> members;

    //Constructor
    public ChallengeGroup()
    {
        members = new ArrayList<>();
    }

    //Getters/Setters
    public void setTimeDate(Date timDate) { this.timeDate = timDate; }
    public void setTimeHours(int timeHours) { this.timeHours = timeHours; }
    public void setTimeMinutes(int timeMinutes) { this.timeMinutes = timeMinutes; }
    public void setTimeSeconds(int timeSeconds) { this.timeSeconds = timeSeconds; }
    public void setTimeMilliseconds(int timeMilliseconds) { this.timeMilliseconds = timeMilliseconds; }
    public void setPositive(boolean isPositive) { this.isPositive = isPositive; }
    public void addMember(Member mb) { members.add(mb); }
	
}