package com.drumge.template.cache;

public class NoSuchKeyException extends CacheException {

	private static final long serialVersionUID = 6921968875814792791L;
	

	public NoSuchKeyException(){
		
	}
	public NoSuchKeyException(String key, String detailMessage, Throwable throwable){
		super(key, detailMessage, throwable);
	}
	
	public NoSuchKeyException(String key, String detailMessage){
		super(key, detailMessage);
	}
}
