package com.dongdong.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtil {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private BitmapUtil() {
		throw new Error("Do not need instantiate!");
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return 圆形Bitmap
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 压缩图片大小
	 * 
	 * @param image
	 *            源Bitmap
	 * @return 压缩后的Bitmap
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 图片压缩处理（使用Options的方法）
	 * <p/>
	 * <br>
	 * <b>说明</b> 使用方法：
	 * 首先你要将Options的inJustDecodeBounds属性设置为true，BitmapFactory.decode一次图片 。
	 * 然后将Options连同期望的宽度和高度一起传递到到本方法中。
	 * 之后再使用本方法的返回值做参数调用BitmapFactory.decode创建图片。
	 * <p/>
	 * <br>
	 * <b>说明</b> BitmapFactory创建bitmap会尝试为已经构建的bitmap分配内存
	 * ，这时就会很容易导致OOM出现。为此每一种创建方法都提供了一个可选的Options参数
	 * ，将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存
	 * ，返回值也不再是一个Bitmap对象， 而是null。虽然Bitmap是null了，但是Options的outWidth、
	 * outHeight和outMimeType属性都会被赋值。
	 * 
	 * @param reqWidth
	 *            目标宽度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
	 * @param reqHeight
	 *            目标高度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
	 */
	public static BitmapFactory.Options calculateInSampleSize(
			final BitmapFactory.Options options, final int reqWidth,
			final int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > 400 || width > 450) {
			if (height > reqHeight || width > reqWidth) {
				// 计算出实际宽高和目标宽高的比率
				final int heightRatio = Math.round((float) height
						/ (float) reqHeight);
				final int widthRatio = Math.round((float) width
						/ (float) reqWidth);
				// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
				// 一定都会大于等于目标的宽和高。
				inSampleSize = heightRatio < widthRatio ? heightRatio
						: widthRatio;
			}
		}
		// 设置压缩比例
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return options;
	}

	/**
	 * 获取一个指定大小的bitmap
	 * 
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 */
	public static Bitmap getBitmapFromFile(String pathName, int reqWidth,
			int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options = calculateInSampleSize(options, reqWidth, reqHeight);
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 *            源Bitmap
	 * @param w
	 *            宽
	 * @param h
	 *            高
	 * @return 目标Bitmap
	 */
	public static Bitmap zoom(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 *            源Bitmap
	 * @param roundPx
	 *            圆角大小
	 * @return 期望Bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,int roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
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

}
