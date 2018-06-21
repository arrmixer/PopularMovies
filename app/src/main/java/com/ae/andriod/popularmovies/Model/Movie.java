package com.ae.andriod.popularmovies.Model;



import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;


/*Model for the Movie object(POJO). It should populate on
 * the UI the movie title, Image of Movie Poster,
 * plot synopsis, user rating, and release date. */
@Entity(tableName = "favorites")
public class Movie implements Parcelable {
    //only part of Movie object not shown to user
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @NonNull
    private int movieId;

    /*All properties from JSON to be shown to user
     * on the details page in another activity*/
    private String title;

    @ColumnInfo(name = "user_rating")
    private double userRating;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    private String description;

    //to be added after initial construction

    @ColumnInfo(name = "run_time")
    private int runtime;

    private List<String> authors;

    private List<String> reviews;

    @ColumnInfo(name = "youtube_keys")
    private List<String> youtubeKeys;


    /*Stores image path of poster to be
     * shown on main grid page*/

    private String poster;

    @Ignore
    //Constant for URL
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    @Ignore
    public Movie(@NonNull int movieId, String title,  String poster, double userRating, String releaseDate, String description) {
        this.movieId = movieId;
        this.title = title;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.description = description;
        this.poster = poster;
    }

    public Movie(@NonNull int movieId, String title, double userRating, String releaseDate, String description, int runtime, List<String> authors, List<String> reviews, List<String> youtubeKeys, String poster) {
        this.movieId = movieId;
        this.title = title;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.description = description;
        this.runtime = runtime;
        this.authors = authors;
        this.reviews = reviews;
        this.youtubeKeys = youtubeKeys;
        this.poster = poster;
    }

    @NonNull
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(@NonNull int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    /*Runtime and Reviews is not set in Parcel at this time.
     * It is not part of initial build in the Main page. If the
     * user selects a movie, both are added to the model
     * afterwards in Detail page*/
    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /*Using the JSON as my glue for my authors and reviews*/
    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public List<String> getYoutubeKeys() {
        return youtubeKeys;
    }

    public void setYoutubeKeys(List<String> youtubeKeys) {
        this.youtubeKeys = youtubeKeys;
    }

    /*Method used to create URL to retrieve photo jpg*/
    @Ignore
    public String getImage(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append(IMAGE_BASE_URL).append("w185").append(movie.poster);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", userRating=" + userRating +
                ", releaseDate='" + releaseDate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /*Parcel section*/
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @Ignore
    private Movie(Parcel in) {
        this.movieId = in.readInt();
        title = in.readString();
        poster = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        description = in.readString();
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }
    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(this.movieId);
        dest.writeString(this.title);
        dest.writeString(this.poster);
        dest.writeDouble(this.userRating);
        dest.writeString(this.releaseDate);
        dest.writeString(this.description);
    }
}
