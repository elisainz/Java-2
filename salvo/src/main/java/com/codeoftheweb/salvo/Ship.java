package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity //para que JPA se pase a base de datos y compile.
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id") //quien comanda
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "shipLocation")
    private List<String> shipLocations = new ArrayList<>();

    public Ship() {
    } //constructor vacio

    public Ship(String type, GamePlayer gamePlayer, List<String> shipLocations) //esto es un constructor. alt ins
    {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
    }

    public long getId() //getters
    {
        return id;
    }

    public String getType() {
        return type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("shipType", getType());
        dto.put("shipLocations", getShipLocations());
        return dto;
    }
}
