package com.drumge.template.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.TextUtils;


import com.drumge.template.log.MLog;

import java.io.File;
import java.io.InputStream;

public class YYImageUtils {
    public static final int IMAGE_COMPRESS_RATE = 75;
    public static final int IMAGE_SCALE_WIDTH = 800;
    public static final int IMAGE_SCALE_HEIGHT = 800;
    public static Bitmap decodeFile(String filePath) {
        return decodeFile(filePath, null);
    }
    
    public static Bitmap decodeByWidth(String filePath, int desiredWidth) {
    	try {
    		return decodeFileOrThrow(filePath, desiredWidth, 0);
    	}
    	catch (Throwable e) {
    		return null;
    	}
    }
    
    public static Bitmap decodeBySize(String filePath, int size) {
        Rect rect = decodeBmpSize(filePath);
        if (rect.width() > rect.height()) {
            return decodeByWidth(filePath, size);
        }
        else {
            return decodeByHeight(filePath, size);
        }
    }

    public static Rect decodeBmpSize(String filePath) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        return new Rect(0, 0, opts.outWidth, opts.outHeight);
    }
    
    public static Rect decodeBmpSizeBy(String filePath, int desiredWidth, int desiredHeight) {
        Options opts = getProperOptions(filePath, desiredWidth, desiredHeight, false);
        if (opts == null) {
            return null;
        }
        return new Rect(0, 0, opts.outWidth, opts.outHeight);
    }
    
    public static Bitmap decodeByHeight(String filePath, int desiredHeight) {
    	try {
    		return decodeFileOrThrow(filePath, 0, desiredHeight);
    	}
    	catch (Throwable e) {
    		return null;
    	}
    }
    
    public static Bitmap decodeByWidthOrThrow(String filePath, int desiredWidth) {
    	return decodeFileOrThrow(filePath, desiredWidth, 0);
    }
    
    public static Bitmap decodeByHeightOrThrow(String filePath, int desiredHeight) {
    	return decodeFileOrThrow(filePath, 0, desiredHeight);
    }
    
    /**
     * Decode file with given options.
     * Will prefer use a smaller sample size to save memory,
     * If this is not up to demand, use the one with more parameter:
     * {@link #decodeFileOrThrow(String, int, int, boolean)}.
     * NOTE OutOfMemoryError will be eaten here, and null returned in this case.
     * @param filePath			File path.
     * @param desiredWidth		Desired width, can be 0. 
     * 							If set to 0, desiredHeight will be honored.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @param desiredHeight		Desired height, can be 0.
     * 							If set to 0, desiredWidth will be honored.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @return Bitmap decoded, or null if failed.
     */
    public static Bitmap decodeFile(String filePath, int desiredWidth, int desiredHeight) {
    	try {
    		return decodeFileOrThrow(filePath, desiredWidth, desiredHeight);
    	}
    	catch (Throwable e) {
    	    MLog.warn("decoeFile", "fail to decode %s, %s", filePath, e.toString());
    		return null;
    	}
    }
    
    /**
     * Decode file with given options.
     * Will prefer use a smaller sample size to save memory,
     * If this is not up to demand, use the one with more parameter:
     * {@link #decodeFileOrThrow(String, int, int, boolean)}.
     * NOTE OutOfMemoryError will be eaten here, and null returned in this case.
     * @param resId          resId
     * @param desiredWidth      Desired width, can be 0. 
     *                          If set to 0, desiredHeight will be honored.
     *                          If both desiredWidth and desiredHeight are 0,
     *                          the original bitmap will be decoded.
     * @param desiredHeight     Desired height, can be 0.
     *                          If set to 0, desiredWidth will be honored.
     *                          If both desiredWidth and desiredHeight are 0,
     *                          the original bitmap will be decoded.
     * @return Bitmap decoded, or null if failed.
     */
    public static Bitmap decodeResource(Context context, int resId, int desiredWidth, int desiredHeight) {
        if (desiredWidth <= 0 && desiredHeight <= 0) {
            return decodeResource(context, resId);
        }
        try {
            return decodeResOrThrow(context, resId, desiredWidth, desiredHeight, true);
        }
        catch (Throwable e) {
            MLog.error("YYImageUtils", e);
            return null;
        }
    }
    
    public static Bitmap decodeResource(Context context, int resId) {
        try {
            final Bitmap res = BitmapFactory.decodeResource(context.getResources(), resId);
            return res;
        }
        catch (OutOfMemoryError e) {
            MLog.error("YYImageUtils", e);
        }
        return null;
    }

    /**
     * Decode file with given options.
     * Will prefer use a smaller sample size to save memory,
     * If this is not up to demand, use the one with more parameter:
     * {@link #decodeFileOrThrow(String, int, int, boolean)}.
     * NOTE OutOfMemoryError can be throw here. 
     * @param filePath			File path.
     * @param desiredWidth		Desired width, can be 0. 
     * 							If set to 0, desiredHeight will be honored.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @param desiredHeight		Desired height, can be 0.
     * 							If set to 0, desiredWidth will be honored.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @return Bitmap decoded, or null if failed.
     */
    public static Bitmap decodeFileOrThrow(String filePath, int desiredWidth, int desiredHeight) {
    	return decodeFileOrThrow(filePath, desiredWidth, desiredHeight, true);
    }
    
    /**
     * Decode file with given options.
     * NOTE OutOfMemoryError can be throw here. 
     * @param filePath			File path.
     * @param desiredWidth		Desired width, can be 0. 
     * 							If set to 0, maximum width will be used,
     * 							i.e. : desiredHeight will take effect.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @param desiredHeight		Desired height, can be 0.
     * 							If set to 0, maximum height will be used.
     * 							i.e. : desiredWidth will take effect.
     * 							If both desiredWidth and desiredHeight are 0,
     * 							the original bitmap will be decoded.
     * @param isMemoryPrior		If true, will prefer to use a bigger sample size
     * 							to use less memory, otherwise prefer to use a smaller
     * 							sample size, the the returned bitmap can be with bigger size,
     * 							and can be probably more vivid.
     * @return Bitmap decoded, or null if failed.
     */
    public static Bitmap decodeFileOrThrow(String filePath,
										   int desiredWidth, int desiredHeight, boolean isMemoryPrior) {
		Options opts = getProperOptions(filePath, desiredWidth, desiredHeight, isMemoryPrior);
		if (opts == null) {
		    return null;
		}
		opts.inJustDecodeBounds = false;
		final Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
        return bmp;
    }

    public static   BitmapFactory.Options getProperOptions(byte[] data, int desiredWidth, int desiredHeight,
														   boolean isMemoryPrior) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        if (opts.outWidth <= 0 || opts.outHeight <= 0) {
            return null;
        }

        int sampleSize = calSampleSize(desiredWidth, desiredHeight, isMemoryPrior, opts);

        if (desiredHeight > 0 || desiredWidth > 0) {
            do {
                opts.inSampleSize = sampleSize;
                BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                sampleSize++;
            }
            while ((desiredWidth > 0 && opts.outWidth > desiredWidth)
                    || (desiredHeight > 0 && opts.outHeight > desiredHeight));
        }
        return opts;
    }



    private static Options getProperOptions(String filePath, int desiredWidth, int desiredHeight,
											boolean isMemoryPrior) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        if (opts.outWidth <= 0 || opts.outHeight <= 0) {
            return null;
        }

        int sampleSize = calSampleSize(desiredWidth, desiredHeight, isMemoryPrior, opts);
        
        if (desiredHeight > 0 || desiredWidth > 0) {
            do {
                opts.inSampleSize = sampleSize;
                BitmapFactory.decodeFile(filePath, opts);
                sampleSize++;
            }
            while ((desiredWidth > 0 && opts.outWidth > desiredWidth) 
                || (desiredHeight > 0 && opts.outHeight > desiredHeight));
        }
        return opts;
    }

    /**
     * Decode file with given options.
     * NOTE OutOfMemoryError can be throw here. 
     * @param desiredWidth      Desired width, can be 0.
     *                          If set to 0, maximum width will be used,
     *                          i.e. : desiredHeight will take effect.
     *                          If both desiredWidth and desiredHeight are 0,
     *                          the original bitmap will be decoded.
     * @param desiredHeight     Desired height, can be 0.
     *                          If set to 0, maximum height will be used.
     *                          i.e. : desiredWidth will take effect.
     *                          If both desiredWidth and desiredHeight are 0,
     *                          the original bitmap will be decoded.
     * @param isMemoryPrior     If true, will prefer to use a bigger sample size
     *                          to use less memory, otherwise prefer to use a smaller
     *                          sample size, the the returned bitmap can be with bigger size,
     *                          and can be probably more vivid.
     * @return Bitmap decoded, or null if failed.
     */
    public static Bitmap decodeResOrThrow(Context context, int drawableId,
										  int desiredWidth, int desiredHeight, boolean isMemoryPrior) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        
        final Resources res = context.getResources();
        BitmapFactory.decodeResource(res, drawableId, opts);
        if (opts.outWidth <= 0 || opts.outHeight <= 0) {
            return null;
        }

        int sampleSize = calSampleSize(desiredWidth, desiredHeight, isMemoryPrior, opts);

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sampleSize;
        final Bitmap ret = BitmapFactory.decodeResource(res, drawableId, opts);
        return ret;
    }
    
    private static int calSampleSize(int desiredWidth, int desiredHeight, boolean isMemoryPrior, Options opts) {
        int sampleSize = 1;
		if (desiredWidth == 0 && desiredHeight == 0) {
			sampleSize = 1;
		}
		else if (desiredHeight == 0) {
			sampleSize = (opts.outWidth + desiredWidth - 1) / desiredWidth;
		}
		else if (desiredWidth == 0) {
			sampleSize = (opts.outHeight + desiredHeight - 1) / desiredHeight;
		}
		else {
			final int horRatio = (opts.outWidth + desiredWidth - 1) / desiredWidth;
			final int verRatio = (opts.outHeight + desiredHeight - 1) / desiredHeight;
			sampleSize = isMemoryPrior ? Math.max(horRatio, verRatio) : Math.min(horRatio, verRatio);
		}
        return sampleSize;
    }
    
    public static Bitmap decodeFile(String filePath, Options opt) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        Bitmap bmp = null;      
        try {
            File file = new File(filePath);
            if (file.isFile()) {                
                bmp = BitmapFactory.decodeFile(filePath, opt);
            }
            else {
                MLog.error(YYImageUtils.class, filePath + " is not a file");
            }
        } catch (OutOfMemoryError err) {
            MLog.error(YYImageUtils.class, "oom: " + filePath);
            bmp = null;
        }
        return bmp;
    }

	public static Bitmap resizeBitmap(Bitmap bitmap, int maxBorderLength, boolean recycle) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int newHeight = 0;
		int newWidth = 0;
		if (width > height) {
			float ratio = ((float) height) / ((float) width);
			newWidth = maxBorderLength;
			newHeight = (int) ((newWidth) * ratio);
		}
		else if (height > width){
			float ratio = ((float) width) / ((float) height);
			newHeight = maxBorderLength;
			newWidth = (int) ((newHeight) * ratio);
		}
		else {
			newWidth = maxBorderLength;
			newHeight = maxBorderLength;
		}
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		try {
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);
			if (recycle && !bitmap.isRecycled() && bitmap != resizedBitmap) {
				bitmap.recycle();
			}
			return resizedBitmap;
		}
		catch (OutOfMemoryError e) {
		    MLog.error(YYImageUtils.class, "lcy resizeBitmap OOM %s", e);
		}
		return null;
	}

	public static boolean resizeAndRotateImage(String imageFile, String newFileName, int maxWidth, int maxHeight, Matrix matrix) {
		return resizeAndRotateImage(imageFile, newFileName, maxWidth, maxHeight, matrix, IMAGE_COMPRESS_RATE);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			if (inSampleSize <= 0){
				inSampleSize = 1;
			}

			final float totalPixels = width * height;

			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap
					|| isBitmapOverSize(inSampleSize, width, height)) {
				inSampleSize++;
			}
		}
		//HttpLog.v("Sample size is %d", inSampleSize);
		return inSampleSize;
	}

	/**
	 * bitmap有没有超过4096 X 4096，硬件加速下显示图片的限制
	 * @return true 超过限制
	 */
	public static boolean isBitmapOverSize(final int sampleSize, final int width, final int height){
		int correctSampleSize = sampleSize;
		if (sampleSize > 2) {
			correctSampleSize = sampleSize / 2 * 2;
		}
		//HttpLog.v("isBitmapOverSize correctSampleSize=%d", correctSampleSize);
		if (width / correctSampleSize > 4096 || height / correctSampleSize > 4096){
			//HttpLog.v("isBitmapOverSize true");
			return true;
		}
		return false;
	}

	public static boolean resizeImage(String imageFile, String newFileName, int destWidth, int destHeight, Matrix matrix, int quality) {
		Options options = new Options();
		options.outHeight = 0;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile, options);
		int originWidth = options.outWidth;
		int originHeight = options.outHeight;
		if (originWidth <= 0 || originHeight <= 0) {
			MLog.error(YYImageUtils.class, "bitmap width or height is zero");
			return false;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, destWidth, destHeight);

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(imageFile, options);

			int decodedWidth = bitmap.getWidth();
			int decodedHeight = bitmap.getHeight();

			if( matrix == null ) {
				matrix = new Matrix();
			}
			matrix.setScale(destWidth / (float) decodedWidth, destHeight / (float) decodedHeight);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, decodedWidth, decodedHeight, matrix, true);
		}
		catch (OutOfMemoryError e) {
		}
		if (bitmap != null) {
			try {
				YYFileUtils out = YYFileUtils.openFile(newFileName);
				out.write(bitmap, quality);
				out.close();
				return true;
			}
			catch (Exception e) {
				MLog.error(YYImageUtils.class, e);
			}
		}
		return false;
	}

	// resize and rotate image, if matrix is null, then no rotate will be done
	public static boolean resizeAndRotateImage(String imageFile, String newFileName, int maxWidth, int maxHeight, Matrix matrix, int quality) {
		Options options = new Options();
		options.outHeight = 0;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile, options);
		int originWidth = options.outWidth;
		int originHeight = options.outHeight;
		if (originWidth <= 0 || originHeight <= 0) {
			MLog.error(YYImageUtils.class, "bitmap width or height is zero");
			return false;
		}
		options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
		float factor;
		factor = options.inSampleSize;

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(imageFile, options);

			int decodedWidth = bitmap.getWidth();
			int decodedHeight = bitmap.getHeight();

			int actualWidth = (int) (originWidth / factor);
			int actualHeight = (int) (originHeight / factor);

			if( matrix == null ) {
				matrix = new Matrix();
			}
			matrix.setScale(actualWidth / (float) decodedWidth, actualHeight / (float) decodedHeight);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, decodedWidth, decodedHeight, matrix, true);
		}
		catch (OutOfMemoryError e) {
		}
		if (bitmap != null) {
			try {
				YYFileUtils out = YYFileUtils.openFile(newFileName);
				out.write(bitmap, quality);
				out.close();
				return true;
			}
			catch (Exception e) {
				MLog.error(YYImageUtils.class, e);
			}
		}
		return false;
	}

	public static int getRotate(String filepath) {
		try {
			ExifInterface exif = new ExifInterface(filepath);
			return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);
		}
		catch (Exception e) {
			return 0;
		}
	}

	public static int getAngleFromRotateEnum(int rotate) {
		switch (rotate) {
		case ExifInterface.ORIENTATION_ROTATE_180:
			return 180;
		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;
		default:
			return 0;
		}
	}
	

	// resize bitmap if it's size exceeded maxWidth or maxHeight. and return the passed in bitmap if
	// no need to change size
	public static Bitmap rotateAndResizeImage(Bitmap inBitmap, int maxWidth, int maxHeight, int rotate) {
		int imgWidth = inBitmap.getWidth();
		int imgHeight = inBitmap.getHeight();
		boolean needResize = imgWidth > maxWidth || imgHeight > maxHeight;
		boolean needRotate = getAngleFromRotateEnum(rotate) != 0;
		if (needResize || needRotate) {
			Matrix matrix = new Matrix();
			if (needResize) {
				float scale = Math.min(maxWidth / (float) imgWidth, maxHeight / (float) imgHeight);
				matrix.postScale(scale, scale);
			}
			if (needRotate) {
				matrix.postRotate(getAngleFromRotateEnum(rotate));
			}
			try {
    			Bitmap resultBitmap = Bitmap.createBitmap(inBitmap, 0, 0, imgWidth, imgHeight, matrix, true);
    			return resultBitmap;
			}
			catch (OutOfMemoryError e) {
			}
		}
		return inBitmap;
	}

	public static Bitmap decodeImageFromStream(InputStream queryStream, InputStream decodeStream) {
		Options options = new Options();
		options.outHeight = 0;
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(queryStream, null, options);

		if (options.outWidth <= 0 || options.outHeight <= 0) {
			MLog.error(YYImageUtils.class, "bitmap width or height is zero");
			return null;
		}
		options.inJustDecodeBounds = false;
		int widthScale = options.outWidth / IMAGE_SCALE_WIDTH;
		int heightScale = options.outHeight / IMAGE_SCALE_HEIGHT;
		options.inSampleSize = widthScale > heightScale ? widthScale : heightScale;
		options.inScaled = false;

		try {
		    final Bitmap ret = BitmapFactory.decodeStream(decodeStream, null, options);
            return ret;
		}
		catch (OutOfMemoryError e) {
		    MLog.error("YYImageUtils", "decodeImageFromStream error, OOM");
		}
		return null;
	}
	
	public static void saveBitmapToFile(Bitmap bitmap, String filename) throws Exception {
		if (bitmap != null && filename != null) {
			YYFileUtils out = YYFileUtils.openFile(filename);
			out.write(bitmap, IMAGE_COMPRESS_RATE);
			out.close();
		}
	}

	public static boolean isImage(InputStream queryStream) {
		Options options = new Options();
		options.outHeight = 0;
		options.inJustDecodeBounds = true;
		try {
		    BitmapFactory.decodeStream(queryStream, null, options);
		    return (options.outWidth > 0 && options.outHeight > 0);
		}
		catch (Throwable e) {
		    return false;
		}		
	}
	
	public static boolean isImage(File file) {
	    if (file == null) {
	        return false;
	    }
	    return isImage(file.getPath());
	}

	public static boolean isImage(String imageFile) {
	    if (TextUtils.isEmpty(imageFile)) {
	        return false;
	    }
		Options options = new Options();
		options.outHeight = 0;
		options.inJustDecodeBounds = true;
		try {
		    BitmapFactory.decodeFile(imageFile, options);
		    return (options.outWidth > 0 && options.outHeight > 0);
		}
		catch (Throwable e) {
            MLog.verbose("YYImageUtils", "%d isn't image file", imageFile);
		    return false;
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xffffffff;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	private static Bitmap sDefaultMalePhoto = null;
	private static Bitmap sDefaultMalePhotoOffline = null;
	private static Bitmap sDefaultFemalePhotoOffline = null;
	private static Bitmap sDefaultMalePhotoBitmap = null;
	private static Bitmap sDefaultFemalePhotoBitmap = null;
    
	public static boolean isNotDefaultPortrait(Bitmap image) {
        return (image != sDefaultFemalePhotoBitmap && image != sDefaultMalePhotoBitmap
                && image != sDefaultMalePhotoOffline && image != sDefaultFemalePhotoOffline
                && image != sDefaultMalePhoto && image != sDefaultMalePhotoOffline);
    }
	
    public static Bitmap getGrayBmp(final Bitmap image) {
        if (image != null) {
            try {
                Bitmap grayscalBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(),
                                                            Config.RGB_565);
                Canvas canvas = new Canvas(grayscalBitmap);
                Paint paint = new Paint();
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                paint.setColorFilter(filter);
                canvas.drawBitmap(image, 0, 0, paint);
                
                return grayscalBitmap;
            }
            catch (Exception e) {
                MLog.error("Utils.getGrayBmp", e);
            } catch (OutOfMemoryError e) {
                MLog.error("Utils.getGrayBmp", e);
            }
        }
        return null;
    }
	
    public static Bitmap createClipBitmap(Bitmap bmp, Rect photoRect) {
        // the right and bottom must be checked for their values are converted
        // from float math to integer value and might be bigger than actual
        // bitmap because of round error
        Bitmap portrait = null;
        try {
            if (bmp != null) {
                int bmpWidth = bmp.getWidth();
                int bmpHeight = bmp.getHeight();
                if (bmpWidth > 0 && bmpHeight > 0) {
                    photoRect.right = photoRect.right > bmpWidth ? bmpWidth
                        : photoRect.right;
                    photoRect.bottom = photoRect.bottom > bmpHeight ? bmpHeight
                        : photoRect.bottom;
                    portrait = Bitmap.createBitmap(bmp, photoRect.left,
                                                   photoRect.top, photoRect.width(), photoRect.height());
                    
                    if (bmp != portrait && !bmp.isRecycled()) // old bitmap is useless, free memory
                        bmp.recycle();
                }
            }
        }
        catch (Throwable e) {
            MLog.debug("hjinw", "e = " + e);
        }
        return portrait;
    }

    public static boolean renameFile(String oriPath, String newPath) {
        File file = new File(oriPath);		
        File newFile = new File(newPath);
        return file.renameTo(newFile);
    }

    public static Bitmap decodeResource(Context context, int resId, Options opt) {
        try {
            final Bitmap res = BitmapFactory.decodeResource(context.getResources(),
                    resId, opt);
            return res;
        }
        catch (OutOfMemoryError e) {
            MLog.error("lcy", e);
        }
        return null;
    }
    
    public static interface PORTRAIT_OPS {
        public static final int SMALL = 0;
        public static final int BIG = 1;
        public static final int ORIGINAL = 2;
    }

    /**
     * Created blended bitmap for given bitmap.
     * This aims to be used for the pressed state of an image icon.
     * This can cost much time for a big sized given bitmap.
     * 
     * @param src Cannot be null.
     * @return Blended bitmap.
     * 
     */
    public static Bitmap createBlended(Bitmap src) {
        if (src == null) {
            throw new IllegalArgumentException("Given src is null.");
        }
        final Bitmap target = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(target);
        c.drawBitmap(src, 0, 0, null);
        c.drawColor(0x8F000000 | (Color.GRAY & 0x00111111));
        
        return target;
    }

    public static Bitmap resize(Bitmap oriBitmap, int targetWidth, int targetHeight) {
        if (oriBitmap == null) {
            return null;
        }
        int width = oriBitmap.getWidth();
        int height = oriBitmap.getHeight();
        float scaleWidth = ((float) targetWidth) / width;
        float scaleHeight = ((float) targetHeight) / height;
        float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        try {
            Bitmap resizedBitmap = Bitmap.createBitmap(oriBitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        }
        catch (OutOfMemoryError e) {
            MLog.error(YYImageUtils.class, "resizeBitmap OOM %s", e);
        }
        return null;
    }


	public static int getCameraPhotoOrientation(String imagePath) {
		int rotate = 0;
		try {
			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(
					imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

    public static int getPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Throwable throwable) {
            MLog.error("YYImageUtils", "getPictureDegree error!" + throwable);
        }
        MLog.debug("YYImageUtils", "getPictureDegree path:" + path + ", degree = " + degree);
        return degree;
    }

}
