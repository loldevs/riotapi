package net.boreeas.riotapi;

/**
 * Created on 4/12/2014.
 */
public enum Shard {
    EUW("euw", "chat.eu.lol.riotgames.com", 5223, "prod.euw.lol.riotgames.com"),
    EUNE("eune", "chat.eun1.lol.riotgames.com", 5223, "prod.eun1.lol.riotgames.com"),
    NA("na", "chat.na1.lol.riotgames.com", 5223, "prod.na1.lol.riotgames.com"),
    BR("br", "chat.br.lol.riotgames.com", 5223, "prod.br.lol.riotgames.com"),
    OCE("oce", "chat.oc1.lol.riotgames.com", 5223, "prod.oc1.lol.riotgames.com"),
    KR("kr", null, 0, null);  // TODO: kr urls

    public final String name;
    public final String jabberUrl;
    public final int jabberPort;
    public final String prodUrl;

    private Shard(String name, String jabberUrl, int jabberPort, String prodUrl) {
        this.name = name;
        this.jabberUrl = jabberUrl;
        this.jabberPort = jabberPort;
        this.prodUrl = prodUrl;
    }
}
