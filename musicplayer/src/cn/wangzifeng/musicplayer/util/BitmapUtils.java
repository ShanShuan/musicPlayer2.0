package cn.wangzifeng.musicplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

public class BitmapUtils {

	/**
	 * @param is  ����Դ
	 * @param width  ͼƬ��Ŀ���� 
	 * @param height  ͼƬ��Ŀ��߶�
	 * @return ѹ�������ͼƬ
	 */
	public static Bitmap  loadBitmap(InputStream is, int width, int height)throws IOException{
		//ͨ��is ��ȡ ��һ�� byte[]
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int length=0;
		while( (length=is.read(buffer)) != -1){
			bos.write(buffer, 0, length);
			bos.flush();
		}
		byte[] bytes=bos.toByteArray();
		if(width==0&&height==0){
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		//ʹ��BitmapFactory��ȡͼƬ��ԭʼ��͸�
		Options opts = new Options();
		//��������ͼƬ�ı߽����� 
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		//ͨ��Ŀ���͸߼���ͼƬ��ѹ������
		int w = opts.outWidth / width;
		int h = opts.outHeight / height;
		int scale = w > h ? h : w;
		//��Options��������ѹ������
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		//���½���byte[] ��ȡBitmap
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
	}
	
	public interface BitmapCallback{
		void onBitmapLoaded(Bitmap bitmap);
	}


	/**
	 * ����ͼƬ
	 * @param bitmap
	 * @param path ͼƬ��Ŀ��·��
	 */
	public static void save(Bitmap bitmap, String path)throws IOException{
		File file = new File(path);
		if(!file.getParentFile().exists()){ //��Ŀ¼������
			file.getParentFile().mkdirs(); //������Ŀ¼
		}
		FileOutputStream os = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, os);
	}
	/**
	 * ͨ��һ�������·������һ��ͼƬ
	 * @param path
	 */
	public static void loadBitmap(Context context, final String path, final BitmapCallback callback,final int width,final int height){
		//��ȥ�ļ�������  ������û�����ع�
		String filename=path.substring(path.lastIndexOf("/")+1);
		File file = new File(context.getCacheDir() , filename);
		Bitmap bitmap = loadBitmap(file.getAbsolutePath());
		if(bitmap!=null){
			callback.onBitmapLoaded(bitmap);
			return;
		}
		//�ļ���û��ͼƬ   ��ȥ����
		new AsyncTask<String, String, Bitmap>() {
			protected Bitmap doInBackground(String... params) {
				try {
					InputStream is = HttpUrlUtil.getInputStream(path);
					Bitmap b = loadBitmap(is, width, height);
					return b;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Bitmap bitmap) {
				callback.onBitmapLoaded(bitmap);
			}
		}.execute();
	}
	
	/**
	 * ��ĳ��·���¶�ȡһ��bitmap
	 * @param path
	 * @return
	 */
	public static Bitmap loadBitmap(String path){
		File file = new File(path);
		if(!file.exists()){
			return null;
		}
		return BitmapFactory.decodeFile(path);
	}

	
}
