/**
 * File : Challenges.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.challenge;

import java.util.ArrayList;
import java.util.List;

public class Challenges
{
    //Attribute
    private int id;
    private String mapName;
    private List<ChallengeGroup> chGroups;

    public Challenges()
    {
        chGroups = new ArrayList<>();
    }

    //Getters/Setters
    public void setId(int id) { this.id = id; }
    public void setMapName(String mapName) { this.mapName = mapName; }
    public void addChallengeGroup(ChallengeGroup chGroup) { this.chGroups.add(chGroup); }
}