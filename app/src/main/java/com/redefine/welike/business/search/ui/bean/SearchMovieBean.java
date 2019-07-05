package com.redefine.welike.business.search.ui.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchMovieBean implements Serializable{

    private static final long serialVersionUID = 1407252109530789919L;


    @SerializedName("imdb_id")
    private String imdb_id;
    @SerializedName("title")
    private String title;
    @SerializedName("year")
    private Integer year;
    @SerializedName("rated")
    private String rated; // "PG-13" Rated 分级
    @SerializedName("releaseDate")
    private Long releaseDate;
    @SerializedName("runtime")
    private Integer runtime; //seconds
    @SerializedName("genre")
    private List<String> genre; //"Action, Horror, Sci-Fi"  Genre分类
    @SerializedName("director")
    private List<String> director;
    @SerializedName("writers")
    private List<String> writers;
    @SerializedName("actors")
    private List<String> actors;
    @SerializedName("plot")
    private String plot;
    @SerializedName("country")
    private List<String> country;
    @SerializedName("language")
    private List<String> language;
    @SerializedName("metascore")
    private Double metascore;
    @SerializedName("poster")
    private String poster;
    @SerializedName("rating")
    private Double rating; //"6.2" Rating IMDB评分数 10分满分
    @SerializedName("votes")
    private Long votes;
    @SerializedName("production")
    private List<String> production;
    @SerializedName("type")
    private String type; //"movie" Type 类别 Movie/TV/...
    @SerializedName("alias")
    private List<String> alias;
    @SerializedName("searchwords")
    private List<String> searchwords;

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public Long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getDirector() {
        return director;
    }

    public void setDirector(List<String> director) {
        this.director = director;
    }

    public List<String> getWriters() {
        return writers;
    }

    public void setWriters(List<String> writers) {
        this.writers = writers;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public Double getMetascore() {
        return metascore;
    }

    public void setMetascore(Double metascore) {
        this.metascore = metascore;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public List<String> getProduction() {
        return production;
    }

    public void setProduction(List<String> production) {
        this.production = production;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public List<String> getSearchwords() {
        return searchwords;
    }

    public void setSearchwords(List<String> searchwords) {
        this.searchwords = searchwords;
    }
}
