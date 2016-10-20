package com.drumge.template.cache;

public class CacheException extends Exception {

	private static final long serialVersionUID = 2606810248388215947L;

	private String key;
	
	public CacheException(){
		
	}
	public CacheException(String key, String detailMessage, Throwable throwable){
		super(detailMessage, throwable);
		this.key = key;
	}
	
	public CacheException(String key, String detailMessage){
		super(detailMessage);
		this.key = key;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
