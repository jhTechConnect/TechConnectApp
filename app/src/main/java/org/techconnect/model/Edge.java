package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Edge implements Parcelable {
	public static final Creator<Edge> CREATOR = new Creator<Edge>() {
		@Override
		public Edge createFromParcel(Parcel in) {
			return new Edge(in);
		}

		@Override
		public Edge[] newArray(int size) {
			return new Edge[size];
		}
	};
	//Fields defined as Edge by our GraphSON
	private String _id;
	private String _label;
	private String _outV; //source vertex
	private String _inV; //target vertex
	private String details;

	public Edge() {

	}

	protected Edge(Parcel in) {
		_id = in.readString();
		_label = in.readString();
		_outV = in.readString();
		_inV = in.readString();
		details = in.readString();
	}

	public String getId() {
		return this._id;
	}
	
	public void setId(String id) {
		this._id = id;
	}
	
	public String getLabel() {
		return this._label;
	}
	
	public void setLabel(String label) {
		this._label = label;
	}
	
	public String getOutV() {
		return this._outV;
	}
	
	public void setOutV(String outV) {
		this._outV = outV;
	}
	
	public String getInV() {
		return this._inV;
	}
	
	public void setInV(String inV) {
		this._inV = inV;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(_id);
		parcel.writeString(_label);
		parcel.writeString(_outV);
		parcel.writeString(_inV);
		parcel.writeString(details);
	}
}
