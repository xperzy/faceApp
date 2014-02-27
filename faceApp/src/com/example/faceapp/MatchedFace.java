package com.example.faceapp;

public class MatchedFace {
	public int icon;
	public String title;
	private String mThumbnailUrl;


    public MatchedFace(String mTitle, String thumbnailUrl) {
        super();
        title = mTitle;
        mThumbnailUrl = thumbnailUrl;
    }


    public String getTitle() {
        return title;
    }


    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }
	public MatchedFace() {
		// TODO Auto-generated constructor stub
		super();
	}

	public MatchedFace(int icon, String title) {
		super();
		this.icon = icon;
		this.title = title;
	}
}
