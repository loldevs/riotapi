package net.boreeas.riotapi.rest;

import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
public class AggregatedStats {
    private int averageAssists;
    private int averageChampionsKilled;
    private int averageCombatPlayerScore;
    @Getter private int averageNodeCapture;
    @Getter private int averageNodeCaptureAssist;
    @Getter private int averageNodeNeutralize;
    @Getter private int averageNodeNeutralizeAssist;
    private int averageNumDeaths;
    private int averageObjectivePlayerScore;
    private int averageTeamObjective;
    private int averageTotalPlayerScore;
    @Getter private int botGamesPlayed;
    @Getter private int killingSpree;
    private int maxAssists;
    @Getter private int maxChampionsKilled;
    private int maxCombatPlayerScore;
    @Getter private int maxLargestCriticalStrike;
    @Getter private int maxLargestKillingSpree;
    @Getter private int maxNodeCapture;
    @Getter private int maxNodeCaptureAssist;
    @Getter private int maxNodeNeutralize;
    @Getter private int maxNodeNeutralizeAssist;
    private int maxNumDeaths;
    private int maxObjectivePlayerScore;
    private int maxTeamObjective;
    @Getter private int maxTimePlayed;
    @Getter private int maxTimeSpentLiving;
    private int maxTotalPlayerScore;
    @Getter private int mostChampionKillsPerSession;
    @Getter private int mostSpellsCast;
    @Getter private int normalGamesPlayed;
    @Getter private int rankedPremadeGamesPlayed;
    @Getter private int rankedSoloGamesPlayed;
    @Getter private int totalAssists;
    @Getter private int totalChampionKills;
    @Getter private int totalDamageDealt;
    @Getter private int totalDamageTaken;
    private int totalDeathsPerSession;
    @Getter private int totalDoubleKills;
    @Getter private int totalFirstBlood;
    @Getter private int totalGoldEarned;
    @Getter private int totalHeal;
    @Getter private int totalMagicDamageDealt;
    @Getter private int totalMinionKills;
    @Getter private int totalNeutralMinionsKilled;
    @Getter private int totalNodeCapture;
    @Getter private int totalNodeNeutralize;
    @Getter private int totalPentaKills;
    @Getter private int totalPhysicalDamageDealt;
    @Getter private int totalQuadraKills;
    @Getter private int totalSessionsLost;
    @Getter private int totalSessionsPlayed;
    @Getter private int totalSessionsWon;
    @Getter private int totalTripleKills;
    @Getter private int totalTurretsKilled;
    @Getter private int totalUnrealKills;




    public int getRankedTotalDeathsPerSession() {
        return totalDeathsPerSession;
    }

    public int getDominionMaxTotalPlayerScore() {
        return maxTotalPlayerScore;
    }

    public int getDominionMaxTeamObjective() {
        return maxTeamObjective;
    }

    public int getDominionMaxObjectivePlayerScore() {
        return maxObjectivePlayerScore;
    }

    public int getRankedMaxNumDeaths() {
        return maxNumDeaths;
    }

    public int getDominionMaxCombatPlayerScore() {
        return maxCombatPlayerScore;
    }

    public int getDominionAverageAssists() {
        return averageAssists;
    }

    public int getDominionAverageChampionsKilled() {
        return averageChampionsKilled;
    }

    public int getDominionAverageCombatPlayerScore() {
        return averageCombatPlayerScore;
    }

    public int getDominionAverageNumDeaths() {
        return averageNumDeaths;
    }

    public int getDominionAverageObjectivePlayerScore() {
        return averageObjectivePlayerScore;
    }

    public int getDominionAverageTeamObjective() {
        return averageTeamObjective;
    }

    public int getDominionAverageTotalPlayerScore() {
        return averageTotalPlayerScore;
    }

    public int getDominionMaxAssists() {
        return maxAssists;
    }
}
