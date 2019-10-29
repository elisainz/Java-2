package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    private String password;

    public Player() {
    }  //constructor vacio

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Player(String user) {
        this.userName = user;

    }


    public Set<Score> getTied() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 0.5)
                .collect(toSet()); //lo vuelve a pasar al set de la clase Score. nuevo array
    }

    public Set<Score> getLost() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 0)
                .collect(toSet());
    }

    public Set<Score> getWon() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 1)
                .collect(toSet());
    }


    public double getTotalScore() {

        return getWon().size() + getTied().size() * 0.5 + getLost().size() * 0;
    }

    public Map<String, Object> getLeaderboardDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("name", getUserName());
        dto.put("total", getTotalScore());
        dto.put("won", getWon().size());
        dto.put("lost", getLost().size());
        dto.put("tied", getTied().size());
        return dto;
    }


    public Set<Score> getScores() {
        return scores;
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public String getUserName() {
        return userName;
    }

    public String toString() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getPlayerDto() {
        Map<String, Object> dto = new LinkedHashMap<>(); //dto lo pasa a json
        dto.put("id", getId());
        dto.put("email", getUserName());
        return dto;
    }
}