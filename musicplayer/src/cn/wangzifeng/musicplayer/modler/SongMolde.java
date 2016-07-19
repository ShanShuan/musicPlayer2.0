package cn.wangzifeng.musicplayer.modler;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import cn.wangzifeng.musicplayer.entity.LrcLine;
import cn.wangzifeng.musicplayer.entity.Song;
import cn.wangzifeng.musicplayer.entity.SongInfo;
import cn.wangzifeng.musicplayer.entity.SongUrl;
import cn.wangzifeng.musicplayer.util.HttpUrlUtil;
import cn.wangzifeng.musicplayer.util.JSONParser;
import cn.wangzifeng.musicplayer.util.LrcUtils;
import cn.wangzifeng.musicplayer.util.UrlFactory;
import cn.wangzifeng.musicplayer.util.XmlParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SongMolde {
	public void downLoadLrc(final Context context,final String path,final LrcCallBack callBack){
		AsyncTask<String, String, List<LrcLine>> task=new AsyncTask<String, String, List<LrcLine>>(){

			@Override
			protected List<LrcLine> doInBackground(String... params) {
				try {
					String fileName=path.substring(path.lastIndexOf("/")+1);
					File file=new File(context.getCacheDir(),fileName);
					List<LrcLine> lines=LrcUtils.parseLrc(file);
					if(lines!=null){
						return lines;
					}
					InputStream in = HttpUrlUtil.getInputStream(path);
					return LrcUtils.parseLrc(in, file);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<LrcLine> result) {
				callBack.onLrcLoaded(result);
			}
			
		};
		task.execute();
	}
	
	/**
	 * ��ѯ������ϸ��Ϣ
	 * @param songId
	 * @param callback
	 */
	public void getSongInfoBySongId(final String songId, final SongInfoCallback callback){
		AsyncTask<String, String, Song> task = new AsyncTask<String, String, Song>(){
			//�ڹ����߳��з�������  ����json
			protected Song doInBackground(String... params) {
				//��������
				String path = UrlFactory.getSongInfoUrl(songId);
				try {
					InputStream is = HttpUrlUtil.get(path);
					String json=HttpUrlUtil.isToString(is);
					//Log.i("info", ""+json);
					Song song = JSONParser.parseSongInfo(json);
					return song;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			//���߳��е���callback�ص�����
			protected void onPostExecute(Song song) {
				if(song!=null){
					callback.onSongInfoLoaded(song.getUrls(), song.getSongInfo());
				}else{
					callback.onSongInfoLoaded(null, null);
				}
			}
		};
		//ִ���첽����
		task.execute();
	}
	/**
	 * ���Ҵ� soffset��ʼ��size��Song 
	 * @param callback
	 * @param offset
	 * @param size
	 */
	public  void findNewSong(final CallBack callback,final int offset,final int size){
		 AsyncTask<String, String, List<Song>> task=new AsyncTask<String, String, List<Song>>(){

			@Override
			protected List<Song> doInBackground(String... params) {
				String path=UrlFactory.getNewMusicUrl(offset, size);
				try {
					InputStream is = HttpUrlUtil.getInputStream(path);
					List<Song> songs=XmlParser.parseSongList(is);
					if(songs==null){
						return null;
					}
					return songs;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<Song> result) {
				callback.onSonglistLoaded(result);
			}
			 
		 };
		 task.execute();

	}
	/**
	 * ���Ҵ� soffset��ʼ��size��HotSong 
	 * @param callback
	 * @param offset
	 * @param size
	 */
	public  void findHotSong(final CallBack callback,final int offset,final int size){
		 AsyncTask<String, String, List<Song>> task=new AsyncTask<String, String, List<Song>>(){

			@Override
			protected List<Song> doInBackground(String... params) {
				String path=UrlFactory.getHotMusicUrl(offset, size);
				Log.i("hoturl", path);
				try {
					InputStream is = HttpUrlUtil.getInputStream(path);
					List<Song> songs=XmlParser.parseSongList(is);
					if(songs==null){
						return null;
					}
					return songs;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<Song> result) {
				callback.onSonglistLoaded(result);
			}
			 
		 };
		 task.execute();

	}
	public interface CallBack{
		public void onSonglistLoaded(List<Song> songs);
	}

	/**
	 * ���ݹؼ��ֲ�ѯ���ֽ���б�
	 * @param key
	 * @param callback
	 */
	public void searchSong(final String key, final int pageno, final CallBack callback){
		new AsyncTask<String, String, List<Song>>(){
			@Override
			protected List<Song> doInBackground(String[] params) {
				try {
					String url = UrlFactory.getSearchSongUrl(key,pageno, 30);
					InputStream is = HttpUrlUtil.get(url);
					String json = HttpUrlUtil.isToString(is);
					//����json
					List<Song> songs = JSONParser.parseSearchResult(json);
					
					return songs;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(List<Song> result) {
				callback.onSonglistLoaded(result);
			}
		}.execute();
	}
	/**
	 * ����songInfo����Ҫ�Ļص��ӿ�
	 */
	public interface SongInfoCallback{
		/**
		 * �����ֵĻ�����Ϣ������Ϻ�  
		 * ���������߳����Զ�ִ��
		 * @param url
		 * @param info
		 */
		void onSongInfoLoaded(List<SongUrl> url, SongInfo info);
	}
	public interface LrcCallBack{
		void onLrcLoaded(List<LrcLine> lrcs);
	}
}
