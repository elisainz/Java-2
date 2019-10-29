package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Entity //para que JPA se pase a base de datos y compile.
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id") //quien comanda
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvoLocation")
    private List<String> salvoLocations = new ArrayList<>();

    private int turn;

    public Salvo() {
    }

    //constructor personalizado: alt ins
    public Salvo(GamePlayer gamePlayer, List<String> salvoLocations, int turn) {
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvoLocations;
        this.turn = turn;
    }

    //getter
    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public int getTurn() {
        return turn;
    }


    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("turn", getTurn());
        dto.put("salvoLocations", getSalvoLocations());

        return dto;
    }

}
