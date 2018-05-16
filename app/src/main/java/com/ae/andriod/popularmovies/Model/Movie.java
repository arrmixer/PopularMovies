package com.ae.andriod.popularmovies.Model;


import android.os.Parcel;
import android.os.Parcelable;



/*Model for the Movie object(POJO). It should populate on
 * the UI the movie title, Image of Movie Poster,
 * plot synopsis, user rating, and release date. */
public class Movie implements Parcelable{
    //only part of Movie object not shown to user
    private int movieId;

    /*All properties from JSON to be shown to user
     * on the details page in another activity*/
   private String mTitle;
   private double mUserRating;
   private String mReleaseDate;
   private String mDescription;


    /*Stores image path of poster to be
     * shown on main grid page*/
    private String mPoster;

    //Constant for URL
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";


    public Movie(int movieId, String title, String poster, double userRating, String releaseDate, String description) {
        this.movieId = movieId;
        mTitle = title;
        mPoster = poster;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public void setUserRating(double userRating) {
        mUserRating = userRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String poster) {
        mPoster = poster;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    /*Method used to create URL to retrieve photo jpg*/
    public String getImage(Movie movie){
        StringBuilder sb = new StringBuilder();
        sb.append(IMAGE_BASE_URL).append("w185").append(movie.mPoster);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", mTitle='" + mTitle + '\'' +
                ", mUserRating=" + mUserRating +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mPoster='" + mPoster + '\'' +
                ", Description= " + mDescription +
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

    private Movie(Parcel in){
        this.movieId = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = in.readString();
        mDescription = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(this.movieId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPoster);
        dest.writeDouble(this.mUserRating);
        dest.writeString(this.mReleaseDate);
        dest.writeString(this.mDescription);
    }
}
