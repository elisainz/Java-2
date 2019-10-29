package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date gameTime;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;

    public Game() {
    } //constructor vacio

    public Game(Date gameTime) {
        this.gameTime = gameTime;
    }

    public long getId() {
        return id;
    }

    public Date getGameTime() {
        return gameTime;
    }

    public Set<Score> getScores() {return scores; }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>(); //dto lo pasa a json
        dto.put("id", getId());
        dto.put("created", getGameTime().getTime());
        dto.put("gamePlayers", getGamePlayers()
                .stream()
                .map(GamePlayer::getDto)
                .collect(Collectors.toList()));
        return dto;
    }

}


