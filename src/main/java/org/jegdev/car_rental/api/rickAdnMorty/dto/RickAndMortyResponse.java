package org.jegdev.car_rental.api.rickAdnMorty.dto;

import lombok.Data;

import java.util.List;

@Data
public class RickAndMortyResponse {

    public Info info;
    public List<Character> results;

    @Data
    public static class Info {
        public int count;
        public int pages;
        public String next;
        public String prev;
    }

    @Data
    public static class Character {
        public int id;
        public String name;
        public String status;
        public String species;
        public String type;
        public String gender;
    }
}
